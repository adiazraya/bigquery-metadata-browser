package com.mercadolibre.incidenciabq.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.mercadolibre.incidenciabq.config.BigQueryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Controller for managing BigQuery service account credentials
 * Provides endpoints to view current credentials and upload new ones
 */
@RestController
@RequestMapping("/api/service-account")
@CrossOrigin(origins = "*")
public class ServiceAccountController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAccountController.class);

    @Autowired
    private BigQueryConfig config;

    /**
     * Get information about the current service account
     * Returns non-sensitive information like email, project ID, and permission requirements
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServiceAccountInfo() {
        logger.info("╔══════════════════════════════════════════════════════════");
        logger.info("║ GET SERVICE ACCOUNT INFO");
        logger.info("╚══════════════════════════════════════════════════════════");

        Map<String, Object> response = new HashMap<>();

        try {
            String keyPath = config.getServiceAccountKeyPath();
            logger.info("Reading service account from: {}", keyPath);

            // Load credentials from file
            GoogleCredentials credentials;
            try (java.io.FileInputStream serviceAccountStream = new java.io.FileInputStream(keyPath)) {
                credentials = GoogleCredentials.fromStream(serviceAccountStream);
            }
            
            if (credentials instanceof ServiceAccountCredentials) {
                ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
                
                response.put("status", "active");
                response.put("serviceAccountEmail", serviceAccountCredentials.getClientEmail());
                response.put("projectId", serviceAccountCredentials.getProjectId());
                response.put("keyPath", keyPath);
                response.put("keyExists", Files.exists(Paths.get(keyPath)));
                
                logger.info("Service Account Email: {}", serviceAccountCredentials.getClientEmail());
                logger.info("Project ID: {}", serviceAccountCredentials.getProjectId());
            } else {
                response.put("status", "active");
                response.put("credentialsType", credentials.getClass().getSimpleName());
                response.put("projectId", config.getProjectId());
                response.put("keyPath", keyPath);
            }

            // Add permission requirements
            response.put("requiredPermissions", getRequiredPermissions());
            response.put("permissionGuide", getPermissionGuide());

            logger.info("✓ Service account info retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("✗ Error getting service account info: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("requiredPermissions", getRequiredPermissions());
            response.put("permissionGuide", getPermissionGuide());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Upload a new service account key file
     * Accepts JSON file and validates it before saving
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadServiceAccountKey(
            @RequestParam("file") MultipartFile file) {
        
        logger.info("╔══════════════════════════════════════════════════════════");
        logger.info("║ UPLOAD NEW SERVICE ACCOUNT KEY");
        logger.info("╚══════════════════════════════════════════════════════════");

        Map<String, Object> response = new HashMap<>();

        try {
            if (file.isEmpty()) {
                response.put("status", "error");
                response.put("message", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate JSON format
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            logger.info("Validating service account key JSON...");

            // Try to parse as ServiceAccountCredentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
            );

            if (!(credentials instanceof ServiceAccountCredentials)) {
                response.put("status", "error");
                response.put("message", "Invalid service account key format");
                return ResponseEntity.badRequest().body(response);
            }

            ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
            String email = serviceAccountCredentials.getClientEmail();
            String projectId = serviceAccountCredentials.getProjectId();

            logger.info("Valid service account key detected:");
            logger.info("  • Email: {}", email);
            logger.info("  • Project ID: {}", projectId);

            // Save to a new location (don't overwrite the original)
            String newKeyPath = "./service-account-key-new.json";
            Path targetPath = Paths.get(newKeyPath);
            
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }

            logger.info("✓ New service account key saved to: {}", newKeyPath);
            logger.info("");
            logger.info("⚠️  IMPORTANT: To activate this new service account:");
            logger.info("   1. Rename 'service-account-key.json' to 'service-account-key-backup.json'");
            logger.info("   2. Rename 'service-account-key-new.json' to 'service-account-key.json'");
            logger.info("   3. Restart the application");
            logger.info("   OR set GOOGLE_APPLICATION_CREDENTIALS environment variable");

            response.put("status", "uploaded");
            response.put("message", "Service account key uploaded successfully");
            response.put("serviceAccountEmail", email);
            response.put("projectId", projectId);
            response.put("savedTo", newKeyPath);
            response.put("nextSteps", Arrays.asList(
                "Verify the service account has the required BigQuery permissions",
                "Backup your current service-account-key.json file",
                "Rename service-account-key-new.json to service-account-key.json",
                "Restart the application or update GOOGLE_APPLICATION_CREDENTIALS"
            ));
            response.put("requiredPermissions", getRequiredPermissions());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("✗ Error uploading service account key: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Invalid service account key file: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("✗ Unexpected error: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Error processing file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update service account key from JSON content
     * Accepts raw JSON string instead of file upload
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateServiceAccountKey(
            @RequestBody Map<String, String> request) {
        
        logger.info("╔══════════════════════════════════════════════════════════");
        logger.info("║ UPDATE SERVICE ACCOUNT KEY FROM JSON");
        logger.info("╚══════════════════════════════════════════════════════════");

        Map<String, Object> response = new HashMap<>();

        try {
            String jsonContent = request.get("serviceAccountJson");
            
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Missing 'serviceAccountJson' in request body");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Validating service account key JSON...");

            // Try to parse as ServiceAccountCredentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8))
            );

            if (!(credentials instanceof ServiceAccountCredentials)) {
                response.put("status", "error");
                response.put("message", "Invalid service account key format");
                return ResponseEntity.badRequest().body(response);
            }

            ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
            String email = serviceAccountCredentials.getClientEmail();
            String projectId = serviceAccountCredentials.getProjectId();

            logger.info("Valid service account key detected:");
            logger.info("  • Email: {}", email);
            logger.info("  • Project ID: {}", projectId);

            // Save to a new location
            String newKeyPath = "./service-account-key-new.json";
            Path targetPath = Paths.get(newKeyPath);
            
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(jsonContent.getBytes(StandardCharsets.UTF_8));
            }

            logger.info("✓ New service account key saved to: {}", newKeyPath);

            response.put("status", "uploaded");
            response.put("message", "Service account key updated successfully");
            response.put("serviceAccountEmail", email);
            response.put("projectId", projectId);
            response.put("savedTo", newKeyPath);
            response.put("nextSteps", Arrays.asList(
                "Verify the service account has the required BigQuery permissions",
                "Backup your current service-account-key.json file",
                "Rename service-account-key-new.json to service-account-key.json",
                "Restart the application"
            ));
            response.put("requiredPermissions", getRequiredPermissions());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("✗ Error updating service account key: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Invalid service account key JSON: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("✗ Unexpected error: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Error processing JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get detailed permission requirements
     */
    @GetMapping("/permissions")
    public ResponseEntity<Map<String, Object>> getPermissions() {
        Map<String, Object> response = new HashMap<>();
        response.put("requiredPermissions", getRequiredPermissions());
        response.put("permissionGuide", getPermissionGuide());
        response.put("gcloudCommands", getGcloudCommands());
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to get required permissions
     */
    private Map<String, Object> getRequiredPermissions() {
        Map<String, Object> permissions = new LinkedHashMap<>();
        
        Map<String, String> metadataViewer = new LinkedHashMap<>();
        metadataViewer.put("role", "roles/bigquery.metadataViewer");
        metadataViewer.put("description", "View metadata for all datasets and tables");
        metadataViewer.put("purpose", "Required to list datasets, tables, and schemas");
        metadataViewer.put("permissions", "bigquery.datasets.get, bigquery.tables.get, bigquery.tables.list");
        
        Map<String, String> jobUser = new LinkedHashMap<>();
        jobUser.put("role", "roles/bigquery.jobUser");
        jobUser.put("description", "Run BigQuery jobs (queries)");
        jobUser.put("purpose", "Required for JDBC driver to execute SQL queries");
        jobUser.put("permissions", "bigquery.jobs.create");
        
        permissions.put("minimum", Arrays.asList(metadataViewer, jobUser));
        
        Map<String, String> dataViewer = new LinkedHashMap<>();
        dataViewer.put("role", "roles/bigquery.dataViewer");
        dataViewer.put("description", "Read table data and metadata");
        dataViewer.put("purpose", "Optional: If you need to query actual table data");
        dataViewer.put("permissions", "bigquery.tables.getData");
        
        permissions.put("optional", Collections.singletonList(dataViewer));
        
        Map<String, String> admin = new LinkedHashMap<>();
        admin.put("role", "roles/bigquery.admin");
        admin.put("description", "Full BigQuery access");
        admin.put("purpose", "⚠️ NOT RECOMMENDED - Too permissive for metadata browsing");
        admin.put("permissions", "All BigQuery permissions");
        
        permissions.put("notRecommended", Collections.singletonList(admin));
        
        return permissions;
    }

    /**
     * Helper method to get permission guide
     */
    private Map<String, Object> getPermissionGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();
        
        guide.put("summary", "Your service account needs these permissions to work with this application");
        guide.put("minimumRoles", Arrays.asList(
            "roles/bigquery.metadataViewer - View datasets, tables, and schemas",
            "roles/bigquery.jobUser - Run queries via JDBC driver"
        ));
        
        guide.put("howToAssign", Arrays.asList(
            "Option 1: Use Google Cloud Console (IAM & Admin > Service Accounts)",
            "Option 2: Use gcloud CLI commands (see /api/service-account/permissions endpoint)"
        ));
        
        guide.put("verification", Arrays.asList(
            "Test with: GET /api/metadata/datasets (REST API method)",
            "Test with: GET /api/jdbc/datasets (JDBC method)"
        ));
        
        return guide;
    }

    /**
     * Helper method to get gcloud commands for assigning permissions
     */
    private Map<String, Object> getGcloudCommands() {
        Map<String, Object> commands = new LinkedHashMap<>();
        
        commands.put("description", "Use these commands to assign the required permissions to your service account");
        
        commands.put("step1", Map.of(
            "description", "Set your service account email and project ID",
            "command", "export SERVICE_ACCOUNT_EMAIL=\"your-service-account@your-project.iam.gserviceaccount.com\"\nexport PROJECT_ID=\"your-project-id\""
        ));
        
        commands.put("step2", Map.of(
            "description", "Assign BigQuery Metadata Viewer role",
            "command", "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.metadataViewer\""
        ));
        
        commands.put("step3", Map.of(
            "description", "Assign BigQuery Job User role",
            "command", "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.jobUser\""
        ));
        
        commands.put("step4", Map.of(
            "description", "Verify permissions",
            "command", "gcloud projects get-iam-policy $PROJECT_ID \\\n  --flatten=\"bindings[].members\" \\\n  --filter=\"bindings.members:serviceAccount:$SERVICE_ACCOUNT_EMAIL\""
        ));
        
        commands.put("optional", Map.of(
            "description", "If you need to read actual table data (not just metadata)",
            "command", "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.dataViewer\""
        ));
        
        return commands;
    }
}

