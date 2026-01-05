package com.mercadolibre.incidenciabq.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.TableId;
import com.mercadolibre.incidenciabq.config.BigQueryConfig;
import com.mercadolibre.incidenciabq.config.SessionAwareCredentialsProvider;
import com.mercadolibre.incidenciabq.model.Dataset;
import com.mercadolibre.incidenciabq.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BigQueryService {

    private final BigQueryConfig config;
    
    @Autowired
    private SessionAwareCredentialsProvider credentialsProvider;

    public BigQueryService(BigQueryConfig config) {
        this.config = config;
    }

    private BigQuery getBigQueryClient() throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("[TIMING] Starting BigQuery client initialization");
        log.info("[DETAIL] ┌─────────────────────────────────────────────────────");
        log.info("[DETAIL] │ BACKEND: Initializing BigQuery Client");
        log.info("[DETAIL] ├─────────────────────────────────────────────────────");
        
        // Step 1: Load credentials (session-aware)
        long credentialsStart = System.currentTimeMillis();
        log.info("[DETAIL] │ Step 1: Loading service account credentials");
        
        String sessionId = credentialsProvider.getSessionId();
        boolean hasCustom = credentialsProvider.hasSessionCredentials();
        
        log.info("[DETAIL] │   → Session ID: {}", sessionId);
        log.info("[DETAIL] │   → Has Custom Credentials: {}", hasCustom);
        log.info("[DETAIL] │   → Project ID: {}", config.getProjectId());
        
        GoogleCredentials credentials = credentialsProvider.getCredentials();
        
        long credentialsTime = System.currentTimeMillis() - credentialsStart;
        log.info("[TIMING] Credentials loaded in {} ms", credentialsTime);
        log.info("[DETAIL] │   ✓ Using {} credentials", hasCustom ? "session-specific" : "default");
        log.info("[DETAIL] │   ✓ Credentials loaded successfully");
        
        // Step 2: Build BigQuery client (always fresh for session-specific credentials)
        long clientStart = System.currentTimeMillis();
        log.info("[DETAIL] │ Step 2: Building BigQuery client");
        log.info("[DETAIL] │   → Creating BigQueryOptions builder");
        log.info("[DETAIL] │   → Setting project: {}", config.getProjectId());
        log.info("[DETAIL] │   → Attaching credentials");
        
        BigQuery bigQuery = BigQueryOptions.newBuilder()
            .setProjectId(config.getProjectId())
            .setCredentials(credentials)
            .build()
            .getService();
        long clientTime = System.currentTimeMillis() - clientStart;
        
        log.info("[DETAIL] │   ✓ BigQuery client built successfully");
        log.info("[DETAIL] │   ✓ Ready to make API calls to BigQuery");
        
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("[TIMING] BigQuery client initialized in {} ms (credentials: {}ms, client: {}ms)", 
                totalTime, credentialsTime, clientTime);
        log.info("[DETAIL] └─────────────────────────────────────────────────────");
        
        return bigQuery;
    }

    public List<Dataset> listDatasets() {
        long operationStart = System.currentTimeMillis();
        log.info("[TIMING] ========== Starting listDatasets operation ==========");
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ BACKEND PROCESSING: List All Datasets");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        
        List<Dataset> datasets = new ArrayList<>();
        
        try {
            // Step 1: Get BigQuery client
            long clientStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 1/3: Acquiring BigQuery Client");
            BigQuery bigQueryClient = getBigQueryClient();
            long clientTime = System.currentTimeMillis() - clientStart;
            log.info("[TIMING] Step 1/3: Client acquired in {} ms", clientTime);
            
            // Step 2: Make API call to BigQuery
            long queryStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 2/3: Making API call to BigQuery");
            log.info("[DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────");
            log.info("[DETAIL] ║   │ API Method: BigQuery.listDatasets()");
            log.info("[DETAIL] ║   │ Target Project: {}", config.getProjectId());
            log.info("[DETAIL] ║   │ Endpoint: BigQuery Data API v2");
            log.info("[DETAIL] ║   │ Operation: LIST_DATASETS");
            log.info("[DETAIL] ║   │ Request Type: REST API Call");
            log.info("[DETAIL] ║   └────────────────────────────────────────────────");
            log.info("[DETAIL] ║   → Sending request to Google BigQuery API...");
            
            com.google.api.gax.paging.Page<com.google.cloud.bigquery.Dataset> datasetPage = 
                bigQueryClient.listDatasets(config.getProjectId());
            
            long queryTime = System.currentTimeMillis() - queryStart;
            log.info("[TIMING] Step 2/3: Dataset list retrieved in {} ms", queryTime);
            log.info("[DETAIL] ║   ✓ API Response received from BigQuery");
            log.info("[DETAIL] ║   ✓ Status: SUCCESS");
            
            // Step 3: Process results
            long processingStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 3/3: Processing API Response");
            log.info("[DETAIL] ║   → Iterating through BigQuery datasets...");
            
            int count = 0;
            boolean verboseLogging = false;
            for (com.google.cloud.bigquery.Dataset bqDataset : datasetPage.iterateAll()) {
                count++;
                
                // Only log details for first 10 datasets to avoid performance issues
                verboseLogging = (count <= 10);
                
                if (verboseLogging) {
                    log.info("[DETAIL] ║   ┌─ DATASET #{} ──────────────────────────────────", count);
                    log.info("[DETAIL] ║   │ Processing dataset from BigQuery response");
                    log.info("[DETAIL] ║   │ Raw Dataset ID: {}", bqDataset.getDatasetId());
                    log.info("[DETAIL] ║   │ Dataset Name: {}", bqDataset.getDatasetId().getDataset());
                } else if (count % 100 == 0) {
                    // Log progress every 100 datasets
                    log.info("[DETAIL] ║   → Processed {} datasets so far...", count);
                }
                
                // Create our dataset object
                Dataset dataset = new Dataset();
                dataset.setDatasetId(bqDataset.getDatasetId().getDataset());
                dataset.setProjectId(config.getProjectId());
                
                // Get optional metadata
                String friendlyName = bqDataset.getFriendlyName();
                String description = bqDataset.getDescription();
                String location = bqDataset.getLocation();
                Long creationTime = bqDataset.getCreationTime();
                
                dataset.setFriendlyName(friendlyName);
                dataset.setDescription(description);
                dataset.setLocation(location);
                dataset.setCreationTime(creationTime);
                
                if (verboseLogging) {
                    log.info("[DETAIL] ║   │ → Extracted Fields:");
                    log.info("[DETAIL] ║   │   • datasetId: {}", dataset.getDatasetId());
                    log.info("[DETAIL] ║   │   • projectId: {}", dataset.getProjectId());
                    log.info("[DETAIL] ║   │   • friendlyName: {}", friendlyName != null ? friendlyName : "(none)");
                    log.info("[DETAIL] ║   │   • description: {}", description != null ? description : "(none)");
                    log.info("[DETAIL] ║   │   • location: {}", location != null ? location : "(none)");
                    log.info("[DETAIL] ║   │   • creationTime: {}", creationTime != null ? creationTime : "(none)");
                    log.info("[DETAIL] ║   │ ✓ Dataset object created and added to list");
                    log.info("[DETAIL] ║   └────────────────────────────────────────────────");
                }
                
                datasets.add(dataset);
                
                log.debug("Found dataset #{}: {}", count, dataset.getDatasetId());
            }
            
            long processingTime = System.currentTimeMillis() - processingStart;
            log.info("[TIMING] Step 3/3: Processed {} datasets in {} ms", count, processingTime);
            
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ Processing Summary:");
            log.info("[DETAIL] ║   • Total datasets found: {}", count);
            log.info("[DETAIL] ║   • All datasets converted to model objects");
            log.info("[DETAIL] ║   • Ready to serialize to JSON for response");
            
            long totalTime = System.currentTimeMillis() - operationStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ OPERATION COMPLETE");
            log.info("[DETAIL] ║   ✓ Client initialization: {} ms", clientTime);
            log.info("[DETAIL] ║   ✓ BigQuery API call: {} ms", queryTime);
            log.info("[DETAIL] ║   ✓ Response processing: {} ms", processingTime);
            log.info("[DETAIL] ║   ✓ Total operation time: {} ms", totalTime);
            log.info("[DETAIL] ║   ✓ Datasets returned: {}", datasets.size());
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ========== listDatasets completed in {} ms (client: {}ms, query: {}ms, process: {}ms) ==========", 
                    totalTime, clientTime, queryTime, processingTime);
            log.info("Successfully retrieved {} datasets", datasets.size());
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ OPERATION FAILED");
            log.error("[DETAIL] ║   ✗ Error occurred: {}", e.getMessage());
            log.error("[DETAIL] ║   ✗ Exception type: {}", e.getClass().getSimpleName());
            log.error("[DETAIL] ║   ✗ Time until failure: {} ms", totalTime);
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error listing datasets after {} ms", totalTime, e);
            throw new RuntimeException("Failed to list datasets", e);
        }
        
        return datasets;
    }

    public List<Table> listTables(String datasetId) {
        long operationStart = System.currentTimeMillis();
        log.info("[TIMING] ========== Starting listTables operation for dataset: {} ==========", datasetId);
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ BACKEND PROCESSING: List Tables in Dataset");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Input Parameter:");
        log.info("[DETAIL] ║   → datasetId: {}", datasetId);
        
        List<Table> tables = new ArrayList<>();
        
        try {
            // Step 1: Get BigQuery client
            long clientStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 1/3: Acquiring BigQuery Client");
            BigQuery bigQueryClient = getBigQueryClient();
            long clientTime = System.currentTimeMillis() - clientStart;
            log.info("[TIMING] Step 1/3: Client acquired in {} ms", clientTime);
            
            // Step 2: Make API call to BigQuery
            long queryStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 2/3: Making API call to BigQuery");
            log.info("[DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────");
            log.info("[DETAIL] ║   │ API Method: BigQuery.listTables()");
            log.info("[DETAIL] ║   │ Target Project: {}", config.getProjectId());
            log.info("[DETAIL] ║   │ Target Dataset: {}", datasetId);
            log.info("[DETAIL] ║   │ Full Dataset Path: {}.{}", config.getProjectId(), datasetId);
            log.info("[DETAIL] ║   │ Endpoint: BigQuery Data API v2");
            log.info("[DETAIL] ║   │ Operation: LIST_TABLES");
            log.info("[DETAIL] ║   │ Request Type: REST API Call");
            log.info("[DETAIL] ║   └────────────────────────────────────────────────");
            log.info("[DETAIL] ║   → Creating DatasetId object...");
            
            DatasetId dataset = DatasetId.of(config.getProjectId(), datasetId);
            log.info("[DETAIL] ║   → DatasetId created: {}", dataset);
            log.info("[DETAIL] ║   → Sending request to Google BigQuery API...");
            
            com.google.api.gax.paging.Page<com.google.cloud.bigquery.Table> tablePage = 
                bigQueryClient.listTables(dataset);
            
            long queryTime = System.currentTimeMillis() - queryStart;
            log.info("[TIMING] Step 2/3: Initial table list page retrieved in {} ms", queryTime);
            log.info("[DETAIL] ║   ✓ First page of API Response received from BigQuery");
            log.info("[DETAIL] ║   ✓ Status: SUCCESS");
            log.info("[DETAIL] ║   ⚠️  NOTE: iterateAll() may fetch additional pages during iteration");
            log.info("[DETAIL] ║   ⚠️  Each page fetch will add latency to processing time");
            
            // Step 3: Process results
            long processingStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 3/3: Processing API Response");
            log.info("[DETAIL] ║   → Iterating through BigQuery tables...");
            log.info("[DETAIL] ║   → ULTRA-DETAILED LOGGING ENABLED - timing each table");
            
            int count = 0;
            int paginationEventCount = 0;
            long totalPaginationTime = 0;
            long lastTableTime = System.currentTimeMillis();
            
            for (com.google.cloud.bigquery.Table bqTable : tablePage.iterateAll()) {
                long tableStartTime = System.currentTimeMillis();
                count++;
                
                // Log EVERY table with full timing details
                long timeSinceLastTable = tableStartTime - lastTableTime;
                log.info("[DETAIL] ║   ┌─ TABLE #{} ─────────────────────────────────────", count);
                log.info("[DETAIL] ║   │ Time since last table: {} ms", timeSinceLastTable);
                
                // Detect if this might be a pagination boundary (sudden large delay)
                if (timeSinceLastTable > 100 && count > 1) {
                    paginationEventCount++;
                    totalPaginationTime += timeSinceLastTable;
                    log.warn("[DETAIL] ║   │ ⚠️  ALERT: PAGINATION EVENT #{} - Large delay detected ({}ms)!", 
                            paginationEventCount, timeSinceLastTable);
                    log.warn("[DETAIL] ║   │ ⚠️  Fetching next page of results from BigQuery API");
                }
                
                log.info("[DETAIL] ║   │ Getting table ID...");
                long idStartTime = System.currentTimeMillis();
                String tableId = bqTable.getTableId().getTable();
                long idTime = System.currentTimeMillis() - idStartTime;
                log.info("[DETAIL] ║   │ Table ID retrieved in {} ms: {}", idTime, tableId);
                
                // Create table object and measure each operation
                log.info("[DETAIL] ║   │ Creating Table object...");
                long objectCreateStart = System.currentTimeMillis();
                Table table = new Table();
                long objectCreateTime = System.currentTimeMillis() - objectCreateStart;
                log.info("[DETAIL] ║   │ Object creation: {} ms", objectCreateTime);
                
                log.info("[DETAIL] ║   │ Setting table fields...");
                long setFieldsStart = System.currentTimeMillis();
                table.setTableId(tableId);
                table.setDatasetId(datasetId);
                table.setProjectId(config.getProjectId());
                table.setType("TABLE");  // Default - getting actual type triggers lazy loading
                long setFieldsTime = System.currentTimeMillis() - setFieldsStart;
                log.info("[DETAIL] ║   │ Field setting: {} ms", setFieldsTime);
                
                log.info("[DETAIL] ║   │ Adding to list...");
                long addListStart = System.currentTimeMillis();
                tables.add(table);
                long addListTime = System.currentTimeMillis() - addListStart;
                log.info("[DETAIL] ║   │ List add: {} ms", addListTime);
                
                long totalTableTime = System.currentTimeMillis() - tableStartTime;
                log.info("[DETAIL] ║   │ ✓ TOTAL TABLE TIME: {} ms", totalTableTime);
                log.info("[DETAIL] ║   └────────────────────────────────────────────────");
                
                lastTableTime = System.currentTimeMillis();
            }
            
            long processingTime = System.currentTimeMillis() - processingStart;
            log.info("[TIMING] Step 3/3: Processed {} tables in {} ms", count, processingTime);
            
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ Processing Summary:");
            log.info("[DETAIL] ║   • Total tables found: {}", count);
            log.info("[DETAIL] ║   • Dataset: {}", datasetId);
            log.info("[DETAIL] ║   • Pagination events: {}", paginationEventCount);
            log.info("[DETAIL] ║   • Time in pagination: {} ms ({} seconds)", 
                    totalPaginationTime, totalPaginationTime / 1000);
            if (paginationEventCount > 0) {
                log.info("[DETAIL] ║   • Average per page fetch: {} ms", 
                        totalPaginationTime / paginationEventCount);
                log.info("[DETAIL] ║   • Pagination overhead: {}% of total time", 
                        (totalPaginationTime * 100) / processingTime);
            }
            log.info("[DETAIL] ║   • All tables converted to model objects");
            log.info("[DETAIL] ║   • Ready to serialize to JSON for response");
            
            long totalTime = System.currentTimeMillis() - operationStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ OPERATION COMPLETE");
            log.info("[DETAIL] ║   ✓ Client initialization: {} ms", clientTime);
            log.info("[DETAIL] ║   ✓ BigQuery API call: {} ms", queryTime);
            log.info("[DETAIL] ║   ✓ Response processing: {} ms", processingTime);
            log.info("[DETAIL] ║   ✓ Total operation time: {} ms", totalTime);
            log.info("[DETAIL] ║   ✓ Tables returned: {}", tables.size());
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ========== listTables for dataset '{}' completed in {} ms (client: {}ms, query: {}ms, process: {}ms) ==========", 
                    datasetId, totalTime, clientTime, queryTime, processingTime);
            log.info("Successfully retrieved {} tables from dataset {}", tables.size(), datasetId);
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ OPERATION FAILED");
            log.error("[DETAIL] ║   ✗ Dataset: {}", datasetId);
            log.error("[DETAIL] ║   ✗ Error occurred: {}", e.getMessage());
            log.error("[DETAIL] ║   ✗ Exception type: {}", e.getClass().getSimpleName());
            log.error("[DETAIL] ║   ✗ Time until failure: {} ms", totalTime);
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error listing tables for dataset '{}' after {} ms", datasetId, totalTime, e);
            throw new RuntimeException("Failed to list tables for dataset: " + datasetId, e);
        }
        
        return tables;
    }

    public List<com.mercadolibre.incidenciabq.model.Field> getTableSchema(String datasetId, String tableId) {
        long operationStart = System.currentTimeMillis();
        log.info("[TIMING] ========== Starting getTableSchema operation for {}.{} ==========", datasetId, tableId);
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ BACKEND PROCESSING: Get Table Schema");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Input Parameters:");
        log.info("[DETAIL] ║   → datasetId: {}", datasetId);
        log.info("[DETAIL] ║   → tableId: {}", tableId);
        
        List<com.mercadolibre.incidenciabq.model.Field> fields = new ArrayList<>();
        
        try {
            // Step 1: Get BigQuery client
            long clientStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 1/3: Acquiring BigQuery Client");
            BigQuery bigQueryClient = getBigQueryClient();
            long clientTime = System.currentTimeMillis() - clientStart;
            log.info("[TIMING] Step 1/3: Client acquired in {} ms", clientTime);
            
            // Step 2: Get table metadata
            long queryStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 2/3: Fetching table metadata from BigQuery");
            log.info("[DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────");
            log.info("[DETAIL] ║   │ API Method: BigQuery.getTable()");
            log.info("[DETAIL] ║   │ Target: {}.{}.{}", config.getProjectId(), datasetId, tableId);
            log.info("[DETAIL] ║   └────────────────────────────────────────────────");
            
            TableId bqTableId = TableId.of(config.getProjectId(), datasetId, tableId);
            com.google.cloud.bigquery.Table table = bigQueryClient.getTable(bqTableId);
            
            long queryTime = System.currentTimeMillis() - queryStart;
            log.info("[TIMING] Step 2/3: Table metadata retrieved in {} ms", queryTime);
            
            if (table == null) {
                log.error("[DETAIL] ║   ✗ Table not found: {}.{}", datasetId, tableId);
                throw new RuntimeException("Table not found: " + datasetId + "." + tableId);
            }
            
            log.info("[DETAIL] ║   ✓ Table metadata received");
            
            // Step 3: Extract schema fields
            long processingStart = System.currentTimeMillis();
            log.info("[DETAIL] ║ Step 3/3: Extracting schema fields");
            
            com.google.cloud.bigquery.Schema schema = table.getDefinition().getSchema();
            
            if (schema == null) {
                log.warn("[DETAIL] ║   ⚠️  Table has no schema (might be a view or external table)");
            } else {
                int fieldCount = 0;
                for (com.google.cloud.bigquery.Field bqField : schema.getFields()) {
                    fieldCount++;
                    
                    com.mercadolibre.incidenciabq.model.Field field = new com.mercadolibre.incidenciabq.model.Field();
                    field.setName(bqField.getName());
                    field.setType(bqField.getType().toString());
                    field.setMode(bqField.getMode() != null ? bqField.getMode().toString() : "NULLABLE");
                    field.setDescription(bqField.getDescription());
                    
                    fields.add(field);
                    
                    if (fieldCount <= 5) {
                        log.info("[DETAIL] ║   → Field #{}: {} ({}, {})", 
                                fieldCount, field.getName(), field.getType(), field.getMode());
                    }
                }
                
                if (fieldCount > 5) {
                    log.info("[DETAIL] ║   → ... and {} more fields", fieldCount - 5);
                }
            }
            
            long processingTime = System.currentTimeMillis() - processingStart;
            log.info("[TIMING] Step 3/3: Extracted {} fields in {} ms", fields.size(), processingTime);
            
            long totalTime = System.currentTimeMillis() - operationStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ OPERATION COMPLETE");
            log.info("[DETAIL] ║   ✓ Client initialization: {} ms", clientTime);
            log.info("[DETAIL] ║   ✓ Table metadata retrieval: {} ms", queryTime);
            log.info("[DETAIL] ║   ✓ Schema processing: {} ms", processingTime);
            log.info("[DETAIL] ║   ✓ Total operation time: {} ms", totalTime);
            log.info("[DETAIL] ║   ✓ Fields returned: {}", fields.size());
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ========== getTableSchema for '{}.{}' completed in {} ms ==========", 
                    datasetId, tableId, totalTime);
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ OPERATION FAILED");
            log.error("[DETAIL] ║   ✗ Table: {}.{}", datasetId, tableId);
            log.error("[DETAIL] ║   ✗ Error: {}", e.getMessage());
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error getting table schema after {} ms", totalTime, e);
            throw new RuntimeException("Failed to get table schema: " + datasetId + "." + tableId, e);
        }
        
        return fields;
    }

    public void testConnection() {
        long operationStart = System.currentTimeMillis();
        log.info("[TIMING] ========== Starting connection test ==========");
        
        try {
            long clientStart = System.currentTimeMillis();
            BigQuery bigQueryClient = getBigQueryClient();
            long clientTime = System.currentTimeMillis() - clientStart;
            
            // Test by listing datasets (quick operation)
            long testStart = System.currentTimeMillis();
            com.google.api.gax.paging.Page<com.google.cloud.bigquery.Dataset> page = 
                bigQueryClient.listDatasets(config.getProjectId());
            
            // Just get the first page to test connectivity
            page.getValues().iterator().hasNext();
            long testTime = System.currentTimeMillis() - testStart;
            
            log.info("BigQuery connection test successful!");
            log.info("Project: {}", config.getProjectId());
            
            long totalTime = System.currentTimeMillis() - operationStart;
            log.info("[TIMING] ========== Connection test completed in {} ms (client: {}ms, test: {}ms) ==========", 
                    totalTime, clientTime, testTime);
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            log.error("[TIMING] BigQuery connection test failed after {} ms", totalTime, e);
            throw new RuntimeException("Connection test failed", e);
        }
    }
}

