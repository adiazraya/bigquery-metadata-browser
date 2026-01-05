package com.mercadolibre.incidenciabq.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages service account credentials per user session
 * Each session gets its own isolated service account key
 */
@Service
public class SessionCredentialsManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionCredentialsManager.class);
    private static final String SESSION_CREDENTIALS_KEY = "sessionCredentialsId";
    private static final String CREDENTIALS_DIR = "./session-credentials/";

    // In-memory cache of session credentials
    private final Map<String, GoogleCredentials> credentialsCache = new HashMap<>();

    public SessionCredentialsManager() {
        // Create credentials directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(CREDENTIALS_DIR));
            logger.info("Session credentials directory initialized: {}", CREDENTIALS_DIR);
        } catch (IOException e) {
            logger.error("Failed to create session credentials directory", e);
        }
    }

    /**
     * Get or create a unique credentials ID for the session
     */
    public String getSessionCredentialsId(HttpSession session) {
        String credentialsId = (String) session.getAttribute(SESSION_CREDENTIALS_KEY);
        
        if (credentialsId == null) {
            // Generate a new unique ID for this session
            credentialsId = UUID.randomUUID().toString();
            session.setAttribute(SESSION_CREDENTIALS_KEY, credentialsId);
            logger.info("Created new session credentials ID: {} for session: {}", 
                credentialsId, session.getId());
        }
        
        return credentialsId;
    }

    /**
     * Get the file path for session-specific credentials
     */
    public String getSessionCredentialsPath(String credentialsId) {
        return CREDENTIALS_DIR + "service-account-" + credentialsId + ".json";
    }

    /**
     * Save service account JSON for a specific session
     */
    public Map<String, Object> saveSessionCredentials(HttpSession session, String jsonContent) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String credentialsId = getSessionCredentialsId(session);
            String credentialsPath = getSessionCredentialsPath(credentialsId);
            
            logger.info("Saving credentials for session ID: {}", credentialsId);
            
            // Validate the credentials first
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(jsonContent.getBytes())
            );
            
            if (!(credentials instanceof ServiceAccountCredentials)) {
                result.put("success", false);
                result.put("message", "Invalid service account key format");
                return result;
            }
            
            ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
            String email = serviceAccountCredentials.getClientEmail();
            String projectId = serviceAccountCredentials.getProjectId();
            
            logger.info("Valid service account key detected:");
            logger.info("  • Email: {}", email);
            logger.info("  • Project ID: {}", projectId);
            logger.info("  • Session ID: {}", credentialsId);
            
            // Write to session-specific file
            Path targetPath = Paths.get(credentialsPath);
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(jsonContent.getBytes());
            }
            
            // Cache the credentials
            credentialsCache.put(credentialsId, credentials);
            
            logger.info("✓ Session credentials saved successfully: {}", credentialsPath);
            
            result.put("success", true);
            result.put("message", "Service account uploaded successfully for your session");
            result.put("serviceAccountEmail", email);
            result.put("projectId", projectId);
            result.put("sessionId", credentialsId);
            result.put("savedTo", credentialsPath);
            
        } catch (IOException e) {
            logger.error("✗ Error saving session credentials: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "Invalid service account key: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get credentials for the current session
     * Falls back to default service account if no session credentials exist
     */
    public GoogleCredentials getCredentials(HttpSession session, String defaultKeyPath) throws IOException {
        String credentialsId = getSessionCredentialsId(session);
        
        // Check cache first
        if (credentialsCache.containsKey(credentialsId)) {
            logger.debug("Using cached credentials for session: {}", credentialsId);
            return credentialsCache.get(credentialsId);
        }
        
        // Check if session-specific credentials exist
        String sessionCredentialsPath = getSessionCredentialsPath(credentialsId);
        File sessionFile = new File(sessionCredentialsPath);
        
        if (sessionFile.exists()) {
            logger.info("Loading session-specific credentials: {}", sessionCredentialsPath);
            try (FileInputStream fis = new FileInputStream(sessionFile)) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(fis);
                credentialsCache.put(credentialsId, credentials);
                return credentials;
            }
        }
        
        // Fall back to default credentials
        logger.info("Using default service account for session: {}", credentialsId);
        try (FileInputStream fis = new FileInputStream(defaultKeyPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(fis);
            return credentials;
        }
    }

    /**
     * Check if session has custom credentials
     */
    public boolean hasSessionCredentials(HttpSession session) {
        String credentialsId = getSessionCredentialsId(session);
        String sessionCredentialsPath = getSessionCredentialsPath(credentialsId);
        return new File(sessionCredentialsPath).exists();
    }

    /**
     * Get service account info for the session
     */
    public Map<String, Object> getSessionCredentialsInfo(HttpSession session, String defaultKeyPath) {
        Map<String, Object> info = new HashMap<>();
        String credentialsId = getSessionCredentialsId(session);
        
        try {
            GoogleCredentials credentials = getCredentials(session, defaultKeyPath);
            
            if (credentials instanceof ServiceAccountCredentials) {
                ServiceAccountCredentials sa = (ServiceAccountCredentials) credentials;
                info.put("serviceAccountEmail", sa.getClientEmail());
                info.put("projectId", sa.getProjectId());
            }
            
            String sessionCredentialsPath = getSessionCredentialsPath(credentialsId);
            boolean hasCustom = new File(sessionCredentialsPath).exists();
            
            info.put("sessionId", credentialsId);
            info.put("hasCustomCredentials", hasCustom);
            info.put("credentialsSource", hasCustom ? "Session-specific" : "Default");
            info.put("credentialsPath", hasCustom ? sessionCredentialsPath : defaultKeyPath);
            
        } catch (IOException e) {
            logger.error("Error getting session credentials info", e);
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    /**
     * Clear session credentials
     */
    public void clearSessionCredentials(HttpSession session) {
        String credentialsId = getSessionCredentialsId(session);
        String sessionCredentialsPath = getSessionCredentialsPath(credentialsId);
        
        // Remove from cache
        credentialsCache.remove(credentialsId);
        
        // Delete file
        File file = new File(sessionCredentialsPath);
        if (file.exists()) {
            if (file.delete()) {
                logger.info("Deleted session credentials: {}", sessionCredentialsPath);
            } else {
                logger.warn("Failed to delete session credentials: {}", sessionCredentialsPath);
            }
        }
        
        // Remove from session
        session.removeAttribute(SESSION_CREDENTIALS_KEY);
    }
}

