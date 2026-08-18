package com.trophix.api.games.application.ports.out;

/**
 * Stores a remote image locally (MinIO/S3) and returns its public URL.
 * Owned by the games module and implemented by an outbound storage adapter.
 */
public interface ImageStoragePort {

    /** Downloads the image from {@code sourceUrl} and stores it in the given folder. */
    String downloadAndStore(String sourceUrl, String folder, String filename);
}
