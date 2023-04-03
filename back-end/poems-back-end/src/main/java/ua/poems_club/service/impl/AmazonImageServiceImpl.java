package ua.poems_club.service.impl;

import com.amazonaws.HttpMethod;

import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.service.AmazonImageService;

import java.io.IOException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AmazonImageServiceImpl implements AmazonImageService {
    private final AmazonS3Client amazonS3;
    private final ObjectMetadata metaData;

    private final int EXPIRATION = 1000 * 60 * 60;

    @Value("${BUCKET_NAME}")
    private String bucketName;

    @Override
    public void saveImage(String fileName, MultipartFile file) throws IOException {
        metaData.setContentType(file.getContentType());
        metaData.setContentLength(file.getSize());
        amazonS3.putObject(bucketName,fileName,file.getInputStream(),metaData);
    }

    @Override
    public String getImage(String fileName) {
        var expiration = createExpiration();
        return amazonS3.generatePresignedUrl(bucketName,fileName,expiration, HttpMethod.GET)
                .toString();

    }

    private Date createExpiration(){
        var expiration = new java.util.Date();
        long expTimeMillis = (expiration.getTime() + EXPIRATION);
        expiration.setTime(expTimeMillis);

        return expiration;
    }


    @Override
    public void deleteImage(String fileName) {
        if(amazonS3.doesObjectExist(bucketName,fileName))
            amazonS3.deleteObject(bucketName,fileName);
    }
}
