package my.project.productservice.service.impl;

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
import java.util.UUID;


@Service
public class MinioClientService implements FileUploadService {

//    @Value("${minio.access-key}")
//    private String accessKey;
//
//    @Value("${minio.secret-key}")
//    private String secretKey;
//
//    @Value("${minio.endpoint}")
    private final String endpoint;
//
//    @Value("${minio.region}")
//    private String region;

    private final S3Client s3Client;

    public MinioClientService(
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.region}") String region) {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .region(Region.of(region))
                .build();
        this.endpoint = endpoint;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        createBucketIfNotExists(bucketName);
        String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        return endpoint + "/" + bucketName + "/" + uniqueFileName;
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            // Проверяем существует ли бакет
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest); // Если бакет существует, это не вызовет исключение
        } catch (NoSuchBucketException e) {
            // Если бакет не существует, создаем новый
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
            System.out.println("Бакет " + bucketName + " был успешно создан.");
        } catch (S3Exception e) {
            // Обработка других ошибок, например, если проблема с доступом
            throw new RuntimeException("Ошибка при проверке или создании бакета: " + e.getMessage(), e);
        }
    }
}