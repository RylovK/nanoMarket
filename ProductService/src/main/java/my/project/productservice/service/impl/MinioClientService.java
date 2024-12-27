package my.project.productservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.productservice.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
@Service
@Slf4j
public class MinioClientService implements FileUploadService {

    private final String endpoint;
    private final S3Client s3Client;

    public MinioClientService(
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.region}") String region) {
        this.endpoint = endpoint;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .region(Region.of(region))
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String bucketName) {
        createBucketIfNotExists(bucketName);
        String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            log.info("File {} uploaded successfully to bucket {}", file.getOriginalFilename(), bucketName);
            return new URI(endpoint + "/" + bucketName + "/" + uniqueFileName).toString();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest);
            log.info("Bucket {} exists", bucketName);
        } catch (NoSuchBucketException e) {
            log.info("Bucket {} does not exist. Creating it now.", bucketName);
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
            log.info("Bucket {} created successfully", bucketName);
        } catch (S3Exception e) {
            log.error("Error interacting with S3: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error interacting with S3", e);
        }
    }
}
