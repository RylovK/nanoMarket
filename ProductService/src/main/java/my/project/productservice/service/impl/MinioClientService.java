package my.project.productservice.service.impl;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import my.project.productservice.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@Slf4j
public class MinioClientService implements FileUploadService {

    @Value("${minio.MINIO_ROOT_USER}")
    private String accessKey;

    @Value("${minio.MINIO_ROOT_PASSWORD}")
    private String secretKey;

    @Value("${minio.endpoint}")
    private String endpoint;

//    @Value("${minio.region}")
//    private String region;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket {} created", bucketName);
            }
            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
            String urlToFile = endpoint + "/" + objectWriteResponse.bucket() + "/" + objectWriteResponse.object();
            log.info("File uploaded to Minio: {}", urlToFile);
            return urlToFile;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException | InsufficientDataException | ErrorResponseException | InvalidResponseException | XmlParserException | InternalException exception) {
            log.error("Error occurred during file upload", exception);
            throw new RuntimeException("Failed to upload file", exception);
        }
    }
}