package com.trophix.api.shared.infrastructure.storage;

import com.trophix.api.shared.exception.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

/**
 * Base service for object storage backed by MinIO (S3-compatible).
 * <p>
 * Uploads files to the configured bucket ({@code trophix-media} by default)
 * and returns the public URL of the stored object. At startup the bucket is
 * created when missing and set to {@code public-read}, so the generated links
 * are directly accessible (or serve it via a CDN / reverse proxy).
 */
@Service
@Slf4j
public class MinioStorageService {

    private static final String DEFAULT_FOLDER = "uploads";

    /** Read-only policy for anonymous access to the bucket objects. */
    private static final String PUBLIC_READ_POLICY = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @PostConstruct
    void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.bucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
            }
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(properties.bucket())
                    .config(PUBLIC_READ_POLICY.formatted(properties.bucket()))
                    .build());
            log.info("Bucket '{}' pronto (policy public-read).", properties.bucket());
        } catch (Exception e) {
            log.error("Falha ao preparar o bucket '{}' no MinIO", properties.bucket(), e);
        }
    }

    /**
     * Uploads a file to the default folder and returns its public URL.
     *
     * @param file multipart file to store
     * @return public URL of the stored object
     */
    public String upload(MultipartFile file) {
        return upload(file, DEFAULT_FOLDER);
    }

    /**
     * Uploads a file into {@code folder} and returns its public URL.
     */
    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Nenhum arquivo enviado.");
        }
        String objectName = objectName(folder, file.getOriginalFilename());
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .stream(stream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (ErrorResponseException e) {
            log.error("MinIO recusou o upload de '{}'", objectName, e);
            throw new StorageException("Falha ao armazenar o arquivo. Verifique as credenciais do MinIO.");
        } catch (Exception e) {
            log.error("Falha no upload do arquivo '{}'", objectName, e);
            throw new StorageException("Falha ao armazenar o arquivo. Tente novamente.");
        }
        return publicUrl(objectName);
    }

    private String objectName(String folder, String originalFilename) {
        String extension = extensionOf(originalFilename);
        String name = UUID.randomUUID().toString().replace("-", "");
        return folder + "/" + name + extension;
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        String ext = filename.substring(dot).toLowerCase(Locale.ROOT);
        return ext.length() <= 10 ? ext : "";
    }

    private String publicUrl(String objectName) {
        return properties.publicUrl() + "/" + properties.bucket() + "/" + objectName;
    }
}
