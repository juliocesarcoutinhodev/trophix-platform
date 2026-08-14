package com.trophix.api.shared.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO / S3-compatible storage configuration.
 *
 * @param url        endpoint of the MinIO server (e.g. http://localhost:9000)
 * @param accessKey  access key (MINIO_ROOT_USER)
 * @param secretKey  secret key (MINIO_ROOT_PASSWORD)
 * @param bucket     bucket used by the platform (default trophix-media)
 * @param publicUrl  base URL used to build public links (may be a CDN / reverse proxy)
 */
@ConfigurationProperties(prefix = "trophix.minio")
public record MinioProperties(
        String url,
        String accessKey,
        String secretKey,
        String bucket,
        String publicUrl) {

    public MinioProperties {
        url = url == null || url.isBlank() ? "http://localhost:9000" : url;
        bucket = bucket == null || bucket.isBlank() ? "trophix-media" : bucket;
        publicUrl = publicUrl == null || publicUrl.isBlank() ? url : publicUrl;
    }
}
