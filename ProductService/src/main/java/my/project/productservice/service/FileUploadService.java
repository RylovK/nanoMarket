package my.project.productservice.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for uploading files to a storage system.
 * Provides a method to upload a file to a specified bucket.
 */
public interface FileUploadService {

    /**
     * Uploads a file to the specified bucket.
     * <p>
     * This method saves the file in the storage, creating a unique name for the file.
     * It returns the URL of the uploaded file, which can be used to access the file.
     * </p>
     *
     * @param file       the file to be uploaded. Cannot be {@code null}.
     * @param bucketName the name of the bucket where the file will be uploaded. Cannot be {@code null}.
     * @return a string representing the URL of the uploaded file, which can be used to access the file.
     * Cannot be {@code null}.
     */
    String uploadFile(MultipartFile file, String bucketName);
}
