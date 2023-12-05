package ua.poems_club.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.poems_club.exception.InvalidGoogleCredentialsException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleStorageConfig {

    @Value("${GOOGLE_CREDENTIALS}")
    private String credentials;

    @Value("${PROJECT_ID}")
    private String projectId;

    @Bean
    public Storage getStorage(){
        var googleCredentials = getGoogleCredentialOrElseThrowsException();
        return createStorage(googleCredentials);
    }

    private GoogleCredentials getGoogleCredentialOrElseThrowsException(){
        try{
            return getGoogleCredentials();
        } catch (IOException e) {
            throw new InvalidGoogleCredentialsException();
        }
    }
    private GoogleCredentials getGoogleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new ByteArrayInputStream(new JSONObject(credentials).toString().getBytes()));
    }


    private Storage createStorage(GoogleCredentials credentials){
        var options = createStorageOptions(credentials);
        return options.getService();
    }

    private StorageOptions createStorageOptions(GoogleCredentials credentials){
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();
    }

}
