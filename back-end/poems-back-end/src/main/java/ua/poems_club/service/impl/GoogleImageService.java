package ua.poems_club.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.exception.ImageNotFoundException;
import ua.poems_club.exception.InvalidImageException;
import ua.poems_club.service.ImageService;

import com.google.cloud.storage.Storage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GoogleImageService implements ImageService {
    private final Storage googleStorage;

    @Value("${BUCKET_NAME}")
    private String bucketName;
    private final long duration = 15;
    private final TimeUnit unit = TimeUnit.MINUTES;

    @Override
    public void uploadImage(MultipartFile image,String imageName) {
        var blobInfo = createBlobInfo(image,imageName);
        var bytesArrayOfImage = getBytesFromImageOrTrowException(image);
        addImage(blobInfo,bytesArrayOfImage);
    }

    private BlobInfo createBlobInfo(MultipartFile image, String imageName){
        return BlobInfo.newBuilder(getBlobId(imageName))
                .setContentType(image.getContentType())
                .build();
    }
    private byte[] getBytesFromImageOrTrowException(MultipartFile image) {
        try {
            return getBytesArrayFromImage(image);
        } catch (IOException e) {
            throw new InvalidImageException("Uploaded file is invalid");
        }
    }
    private byte[] getBytesArrayFromImage(MultipartFile image) throws IOException {
        return image.getBytes();
    }

    private void addImage(BlobInfo blobInfo, byte[] bytesArrayOfImage) {
        googleStorage.create(blobInfo, bytesArrayOfImage);
    }


    @Override
    public String getImage(String imageName) {
        var blob = createBlob(imageName);
        checkBlobIsNotNull(blob);
        return googleStorage.signUrl(blob,duration,unit, Storage.SignUrlOption.withV4Signature()).toString();
    }

    private Blob createBlob(String imageName){
        return googleStorage.get(getBlobId(imageName));
    }

    private void checkBlobIsNotNull(Blob blob){
        if (Objects.isNull(blob))
            throw new ImageNotFoundException("User image is not found");
    }

    @Override
    public void removeImage(String imageName) {
        var blobId = getBlobId(imageName);
        googleStorage.delete(blobId);
    }

    private BlobId getBlobId(String imageName){
        return BlobId.of(bucketName,imageName);
    }
}
