package com.mercadolibre.incidenciabq.controller;

import com.mercadolibre.incidenciabq.model.Dataset;
import com.mercadolibre.incidenciabq.model.Table;
import com.mercadolibre.incidenciabq.model.Field;
import com.mercadolibre.incidenciabq.service.BigQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/bigquery")
@CrossOrigin(origins = "*")
@Slf4j
public class BigQueryController {

    private final BigQueryService bigQueryService;

    public BigQueryController(BigQueryService bigQueryService) {
        this.bigQueryService = bigQueryService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        long requestStart = System.currentTimeMillis();
        log.info("[TIMING] ########## Received GET /api/bigquery/test ##########");
        
        try {
            long serviceStart = System.currentTimeMillis();
            bigQueryService.testConnection();
            long serviceTime = System.currentTimeMillis() - serviceStart;
            
            long totalTime = System.currentTimeMillis() - requestStart;
            log.info("[TIMING] ########## GET /api/bigquery/test completed in {} ms (service: {} ms) ##########", 
                    totalTime, serviceTime);
            
            return ResponseEntity.ok("Connection successful!");
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - requestStart;
            log.error("[TIMING] Connection test failed after {} ms", totalTime, e);
            return ResponseEntity.status(500).body("Connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/datasets")
    public ResponseEntity<List<Dataset>> listDatasets() {
        long requestStart = System.currentTimeMillis();
        log.info("[TIMING] ########## Received GET /api/bigquery/datasets ##########");
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ HTTP REQUEST RECEIVED");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Endpoint: GET /api/bigquery/datasets");
        log.info("[DETAIL] ║ Purpose: Retrieve all datasets from BigQuery project");
        log.info("[DETAIL] ║ Request Time: {}", new java.util.Date());
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        
        try {
            log.info("[DETAIL] ║ CONTROLLER: Processing request");
            log.info("[DETAIL] ║   → No query parameters");
            log.info("[DETAIL] ║   → No path variables");
            log.info("[DETAIL] ║   → Calling service layer...");
            
            long serviceStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER → SERVICE: Delegating to BigQueryService.listDatasets()");
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Entering SERVICE LAYER...");
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            
            List<Dataset> datasets = bigQueryService.listDatasets();
            
            long serviceTime = System.currentTimeMillis() - serviceStart;
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Returned from SERVICE LAYER");
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ SERVICE → CONTROLLER: Received response");
            log.info("[DETAIL] ║   ✓ Service call completed in {} ms", serviceTime);
            log.info("[DETAIL] ║   ✓ Datasets received: {}", datasets.size());
            log.info("[DETAIL] ║   → Dataset IDs: {}", 
                     datasets.stream().map(Dataset::getDatasetId).toArray());
            
            // Serialize to JSON
            long serializationStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER: Preparing HTTP response");
            log.info("[DETAIL] ║   → Converting {} datasets to JSON", datasets.size());
            log.info("[DETAIL] ║   → Creating ResponseEntity with HTTP 200 OK");
            
            ResponseEntity<List<Dataset>> response = ResponseEntity.ok(datasets);
            
            long serializationTime = System.currentTimeMillis() - serializationStart;
            log.info("[DETAIL] ║   ✓ JSON serialization completed in {} ms", serializationTime);
            log.info("[DETAIL] ║   ✓ Response entity created");
            
            long totalTime = System.currentTimeMillis() - requestStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ HTTP RESPONSE READY");
            log.info("[DETAIL] ║   ✓ Status Code: 200 OK");
            log.info("[DETAIL] ║   ✓ Content-Type: application/json");
            log.info("[DETAIL] ║   ✓ Body: List<Dataset> with {} items", datasets.size());
            log.info("[DETAIL] ║   ✓ Total request time: {} ms", totalTime);
            log.info("[DETAIL] ║ Breakdown:");
            log.info("[DETAIL] ║   • Service layer: {} ms ({}%)", 
                     serviceTime, (serviceTime * 100 / totalTime));
            log.info("[DETAIL] ║   • JSON serialization: {} ms ({}%)", 
                     serializationTime, (serializationTime * 100 / totalTime));
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ########## GET /api/bigquery/datasets completed in {} ms (service: {} ms, serialization: {} ms, datasets: {}) ##########", 
                    totalTime, serviceTime, serializationTime, datasets.size());
            
            return response;
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - requestStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ HTTP REQUEST FAILED");
            log.error("[DETAIL] ║   ✗ Error: {}", e.getMessage());
            log.error("[DETAIL] ║   ✗ Exception: {}", e.getClass().getSimpleName());
            log.error("[DETAIL] ║   ✗ Time until failure: {} ms", totalTime);
            log.error("[DETAIL] ║   → Returning HTTP 500 Internal Server Error");
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error listing datasets after {} ms", totalTime, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/datasets/{datasetId}/tables")
    public ResponseEntity<List<Table>> listTables(@PathVariable String datasetId) {
        long requestStart = System.currentTimeMillis();
        log.info("[TIMING] ########## Received GET /api/bigquery/datasets/{}/tables ##########", datasetId);
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ HTTP REQUEST RECEIVED");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Endpoint: GET /api/bigquery/datasets/{datasetId}/tables");
        log.info("[DETAIL] ║ Purpose: Retrieve all tables from a specific dataset");
        log.info("[DETAIL] ║ Request Time: {}", new java.util.Date());
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Request Parameters:");
        log.info("[DETAIL] ║   → PATH VARIABLE: datasetId = '{}'", datasetId);
        log.info("[DETAIL] ║   → Full path: /api/bigquery/datasets/{}/tables", datasetId);
        
        try {
            log.info("[DETAIL] ║ CONTROLLER: Processing request");
            log.info("[DETAIL] ║   → Extracted datasetId from path: {}", datasetId);
            log.info("[DETAIL] ║   → Validating parameter...");
            
            if (datasetId == null || datasetId.trim().isEmpty()) {
                log.error("[DETAIL] ║   ✗ Invalid datasetId: empty or null");
                return ResponseEntity.badRequest().build();
            }
            
            log.info("[DETAIL] ║   ✓ Parameter validation passed");
            log.info("[DETAIL] ║   → Calling service layer with datasetId: {}", datasetId);
            
            long serviceStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER → SERVICE: Delegating to BigQueryService.listTables()");
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Entering SERVICE LAYER...");
            log.info("[DETAIL] ║ │ Parameter: datasetId = '{}'", datasetId);
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            
            List<Table> tables = bigQueryService.listTables(datasetId);
            
            long serviceTime = System.currentTimeMillis() - serviceStart;
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Returned from SERVICE LAYER");
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ SERVICE → CONTROLLER: Received response");
            log.info("[DETAIL] ║   ✓ Service call completed in {} ms", serviceTime);
            log.info("[DETAIL] ║   ✓ Tables received: {}", tables.size());
            log.info("[DETAIL] ║   → Table IDs: {}", 
                     tables.stream().map(Table::getTableId).toArray());
            
            // Serialize to JSON
            long serializationStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER: Preparing HTTP response");
            log.info("[DETAIL] ║   → Converting {} tables to JSON", tables.size());
            log.info("[DETAIL] ║   → Creating ResponseEntity with HTTP 200 OK");
            
            ResponseEntity<List<Table>> response = ResponseEntity.ok(tables);
            
            long serializationTime = System.currentTimeMillis() - serializationStart;
            log.info("[DETAIL] ║   ✓ JSON serialization completed in {} ms", serializationTime);
            log.info("[DETAIL] ║   ✓ Response entity created");
            
            long totalTime = System.currentTimeMillis() - requestStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ HTTP RESPONSE READY");
            log.info("[DETAIL] ║   ✓ Status Code: 200 OK");
            log.info("[DETAIL] ║   ✓ Content-Type: application/json");
            log.info("[DETAIL] ║   ✓ Body: List<Table> with {} items", tables.size());
            log.info("[DETAIL] ║   ✓ Total request time: {} ms", totalTime);
            log.info("[DETAIL] ║ Breakdown:");
            log.info("[DETAIL] ║   • Service layer: {} ms ({}%)", 
                     serviceTime, (serviceTime * 100 / totalTime));
            log.info("[DETAIL] ║   • JSON serialization: {} ms ({}%)", 
                     serializationTime, (serializationTime * 100 / totalTime));
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ########## GET /api/bigquery/datasets/{}/tables completed in {} ms (service: {} ms, serialization: {} ms, tables: {}) ##########", 
                    datasetId, totalTime, serviceTime, serializationTime, tables.size());
            
            return response;
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - requestStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ HTTP REQUEST FAILED");
            log.error("[DETAIL] ║   ✗ Dataset: {}", datasetId);
            log.error("[DETAIL] ║   ✗ Error: {}", e.getMessage());
            log.error("[DETAIL] ║   ✗ Exception: {}", e.getClass().getSimpleName());
            log.error("[DETAIL] ║   ✗ Time until failure: {} ms", totalTime);
            log.error("[DETAIL] ║   → Returning HTTP 500 Internal Server Error");
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error listing tables for dataset '{}' after {} ms", datasetId, totalTime, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/datasets/{datasetId}/tables/{tableId}/schema")
    public ResponseEntity<List<Field>> getTableSchema(
            @PathVariable String datasetId,
            @PathVariable String tableId) {
        long requestStart = System.currentTimeMillis();
        log.info("[TIMING] ########## Received GET /api/bigquery/datasets/{}/tables/{}/schema ##########", 
                datasetId, tableId);
        log.info("[DETAIL] ╔══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ HTTP REQUEST RECEIVED");
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Endpoint: GET /api/bigquery/datasets/{datasetId}/tables/{tableId}/schema");
        log.info("[DETAIL] ║ Purpose: Retrieve schema (field definitions) for a specific table");
        log.info("[DETAIL] ║ Request Time: {}", new java.util.Date());
        log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
        log.info("[DETAIL] ║ Request Parameters:");
        log.info("[DETAIL] ║   → PATH VARIABLE: datasetId = '{}'", datasetId);
        log.info("[DETAIL] ║   → PATH VARIABLE: tableId = '{}'", tableId);
        log.info("[DETAIL] ║   → Full path: /api/bigquery/datasets/{}/tables/{}/schema", datasetId, tableId);
        
        try {
            log.info("[DETAIL] ║ CONTROLLER: Processing request");
            log.info("[DETAIL] ║   → Extracted datasetId: {}", datasetId);
            log.info("[DETAIL] ║   → Extracted tableId: {}", tableId);
            log.info("[DETAIL] ║   → Validating parameters...");
            
            if (datasetId == null || datasetId.trim().isEmpty() || 
                tableId == null || tableId.trim().isEmpty()) {
                log.error("[DETAIL] ║   ✗ Invalid parameters");
                return ResponseEntity.badRequest().build();
            }
            
            log.info("[DETAIL] ║   ✓ Parameter validation passed");
            log.info("[DETAIL] ║   → Calling service layer...");
            
            long serviceStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER → SERVICE: Delegating to BigQueryService.getTableSchema()");
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Entering SERVICE LAYER...");
            log.info("[DETAIL] ║ │ Parameters: datasetId='{}', tableId='{}'", datasetId, tableId);
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            
            List<Field> fields = bigQueryService.getTableSchema(datasetId, tableId);
            
            long serviceTime = System.currentTimeMillis() - serviceStart;
            log.info("[DETAIL] ║ ┌────────────────────────────────────────────────────");
            log.info("[DETAIL] ║ │ Returned from SERVICE LAYER");
            log.info("[DETAIL] ║ └────────────────────────────────────────────────────");
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ SERVICE → CONTROLLER: Received response");
            log.info("[DETAIL] ║   ✓ Service call completed in {} ms", serviceTime);
            log.info("[DETAIL] ║   ✓ Fields received: {}", fields.size());
            
            // Serialize to JSON
            long serializationStart = System.currentTimeMillis();
            log.info("[DETAIL] ║");
            log.info("[DETAIL] ║ CONTROLLER: Preparing HTTP response");
            log.info("[DETAIL] ║   → Converting {} fields to JSON", fields.size());
            log.info("[DETAIL] ║   → Creating ResponseEntity with HTTP 200 OK");
            
            ResponseEntity<List<Field>> response = ResponseEntity.ok(fields);
            
            long serializationTime = System.currentTimeMillis() - serializationStart;
            log.info("[DETAIL] ║   ✓ JSON serialization completed in {} ms", serializationTime);
            log.info("[DETAIL] ║   ✓ Response entity created");
            
            long totalTime = System.currentTimeMillis() - requestStart;
            log.info("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.info("[DETAIL] ║ HTTP RESPONSE READY");
            log.info("[DETAIL] ║   ✓ Status Code: 200 OK");
            log.info("[DETAIL] ║   ✓ Content-Type: application/json");
            log.info("[DETAIL] ║   ✓ Body: List<Field> with {} items", fields.size());
            log.info("[DETAIL] ║   ✓ Total request time: {} ms", totalTime);
            log.info("[DETAIL] ║ Breakdown:");
            log.info("[DETAIL] ║   • Service layer: {} ms ({}%)", 
                     serviceTime, (serviceTime * 100 / totalTime));
            log.info("[DETAIL] ║   • JSON serialization: {} ms ({}%)", 
                     serializationTime, (serializationTime * 100 / totalTime));
            log.info("[DETAIL] ╚══════════════════════════════════════════════════════════");
            
            log.info("[TIMING] ########## GET /api/bigquery/datasets/{}/tables/{}/schema completed in {} ms (service: {} ms, fields: {}) ##########", 
                    datasetId, tableId, totalTime, serviceTime, fields.size());
            
            return response;
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - requestStart;
            log.error("[DETAIL] ╠══════════════════════════════════════════════════════════");
            log.error("[DETAIL] ║ HTTP REQUEST FAILED");
            log.error("[DETAIL] ║   ✗ Table: {}.{}", datasetId, tableId);
            log.error("[DETAIL] ║   ✗ Error: {}", e.getMessage());
            log.error("[DETAIL] ║   ✗ Time until failure: {} ms", totalTime);
            log.error("[DETAIL] ╚══════════════════════════════════════════════════════════");
            log.error("[TIMING] Error getting table schema for '{}.{}' after {} ms", 
                    datasetId, tableId, totalTime, e);
            return ResponseEntity.status(500).build();
        }
    }
}

