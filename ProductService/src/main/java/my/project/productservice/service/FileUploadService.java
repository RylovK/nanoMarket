package my.project.productservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String bucketName);
}
