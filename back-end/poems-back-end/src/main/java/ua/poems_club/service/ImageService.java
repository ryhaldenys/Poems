package ua.poems_club.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void uploadImage(MultipartFile image,String imageName);
    String getImage(String imageName);
    void removeImage(String fileName);
}
