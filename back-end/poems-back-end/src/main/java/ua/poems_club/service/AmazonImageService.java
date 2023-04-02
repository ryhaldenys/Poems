package ua.poems_club.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AmazonImageService {
    void saveImage(String fileName, MultipartFile multipartFile) throws IOException;
    String getImage(String fileName);
    void deleteImage(String fileName);
}
