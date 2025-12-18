package com.mercadolibre.incidenciabq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Getter
@Slf4j
public class BigQueryConfig {

    @Value("${bigquery.project.id}")
    private String projectId;

    @Value("${bigquery.service.account.email}")
    private String serviceAccountEmail;

    @Value("${bigquery.service.account.key.path}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void init() throws IOException {
        // For Heroku deployment: Handle service account JSON from environment variable
        String jsonContent = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
        
        if (jsonContent != null && !jsonContent.isEmpty()) {
            log.info("Loading service account credentials from environment variable");
            
            // Create a temporary file for the credentials
            Path tempFile = Files.createTempFile("service-account-", ".json");
            tempFile.toFile().deleteOnExit();
            
            // Write the JSON content to the temp file
            Files.writeString(tempFile, jsonContent);
            
            // Update the path to point to the temp file
            this.serviceAccountKeyPath = tempFile.toAbsolutePath().toString();
            
            log.info("Service account credentials configured successfully");
        } else {
            log.info("Using service account key from file: {}", serviceAccountKeyPath);
        }
        
        // Verify the key file exists
        File keyFile = new File(serviceAccountKeyPath);
        if (!keyFile.exists()) {
            log.error("Service account key file not found at: {}", serviceAccountKeyPath);
            throw new IOException("Service account key file not found: " + serviceAccountKeyPath);
        }
    }
}

