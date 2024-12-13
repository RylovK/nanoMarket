package my.project.productservice.service.impl;

import my.project.productservice.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;


@Service
public class MinioClientService implements FileUploadService {

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.region}")
    private String region;

    private final S3Client s3Client;

    public MinioClientService() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .region(Region.of(region))
                .build();
    }

    public String uploadFile(MultipartFile file, String bucketName) {
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
}