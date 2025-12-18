package com.mercadolibre.incidenciabq.controller;

import com.mercadolibre.incidenciabq.model.Dataset;
import com.mercadolibre.incidenciabq.model.Table;
import com.mercadolibre.incidenciabq.model.Field;
import com.mercadolibre.incidenciabq.service.BigQueryJdbcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BigQuery JDBC metadata operations
 */
@RestController
@RequestMapping("/api/bigquery-jdbc")
public class BigQueryJdbcController {

    private static final Logger logger = LoggerFactory.getLogger(BigQueryJdbcController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");

    private final BigQueryJdbcService bigQueryJdbcService;

    public BigQueryJdbcController(BigQueryJdbcService bigQueryJdbcService) {
        this.bigQueryJdbcService = bigQueryJdbcService;
    }

    @GetMapping("/datasets")
    public ResponseEntity<List<Dataset>> listDatasets() {
        long startTime = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ########## Received GET /api/bigquery-jdbc/datasets ##########");
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ HTTP REQUEST RECEIVED (JDBC)");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Endpoint: GET /api/bigquery-jdbc/datasets");
        logger.info("[DETAIL][JDBC] ║ Purpose: Retrieve all datasets via JDBC");
        logger.info("[DETAIL][JDBC] ║ Request Time: {}", ZonedDateTime.now().format(FORMATTER));
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ CONTROLLER: Processing request");
        logger.info("[DETAIL][JDBC] ║   → Method: JDBC/SQL");
        logger.info("[DETAIL][JDBC] ║   → Calling JDBC service layer...");
        logger.info("[DETAIL][JDBC] ║");
        logger.info("[DETAIL][JDBC] ║ CONTROLLER → SERVICE: Delegating to BigQueryJdbcService.listDatasets()");
        logger.info("[DETAIL][JDBC] ║ ┌────────────────────────────────────────────────────");
        logger.info("[DETAIL][JDBC] ║ │ Entering JDBC SERVICE LAYER...");
        logger.info("[DETAIL][JDBC] ║ └────────────────────────────────────────────────────");

        try {
            List<Dataset> datasets = bigQueryJdbcService.listDatasets();
            long serviceEndTime = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║ ┌────────────────────────────────────────────────────");
            logger.info("[DETAIL][JDBC] ║ │ Returned from JDBC SERVICE LAYER");
            logger.info("[DETAIL][JDBC] ║ └────────────────────────────────────────────────────");
            logger.info("[DETAIL][JDBC] ║");
            logger.info("[DETAIL][JDBC] ║ SERVICE → CONTROLLER: Received response");
            logger.info("[DETAIL][JDBC] ║   ✓ Service call completed in {} ms", (serviceEndTime - startTime));
            logger.info("[DETAIL][JDBC] ║   ✓ Datasets received: {}", datasets.size());
            if (!datasets.isEmpty()) {
                logger.info("[DETAIL][JDBC] ║   → Dataset IDs: {}", 
                    datasets.stream().map(Dataset::getDatasetId).collect(Collectors.joining(", ")));
            }
            logger.info("[DETAIL][JDBC] ║");
            logger.info("[DETAIL][JDBC] ║ CONTROLLER: Preparing HTTP response");
            logger.info("[DETAIL][JDBC] ║   → Converting {} datasets to JSON", datasets.size());
            long serializationStartTime = System.currentTimeMillis();
            ResponseEntity<List<Dataset>> response = ResponseEntity.ok(datasets);
            long serializationEndTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║   ✓ JSON serialization completed in {} ms", 
                (serializationEndTime - serializationStartTime));
            logger.info("[DETAIL][JDBC] ║   ✓ Response entity created");
            logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.info("[DETAIL][JDBC] ║ HTTP RESPONSE READY");
            logger.info("[DETAIL][JDBC] ║   ✓ Status Code: 200 OK");
            logger.info("[DETAIL][JDBC] ║   ✓ Content-Type: application/json");
            logger.info("[DETAIL][JDBC] ║   ✓ Body: List<Dataset> with {} items", datasets.size());
            logger.info("[DETAIL][JDBC] ║   ✓ Method: JDBC/SQL");
            logger.info("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.info("[DETAIL][JDBC] ║ Breakdown:");
            logger.info("[DETAIL][JDBC] ║   • JDBC service layer: {} ms ({}%)", 
                (serviceEndTime - startTime), 
                String.format("%.0f", ((double)(serviceEndTime - startTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ║   • JSON serialization: {} ms ({}%)", 
                (serializationEndTime - serializationStartTime), 
                String.format("%.0f", ((double)(serializationEndTime - serializationStartTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.info("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets completed in {} ms (service: {} ms, serialization: {} ms, datasets: {}) ##########",
                    (endTime - startTime), (serviceEndTime - startTime), 
                    (serializationEndTime - serializationStartTime), datasets.size());
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets failed in {} ms ##########", 
                (endTime - startTime), e);
            logger.error("[DETAIL][JDBC] ║   ✗ Status: FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ HTTP RESPONSE FAILED");
            logger.error("[DETAIL][JDBC] ║   ✓ Status Code: 500 Internal Server Error");
            logger.error("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/datasets/{datasetId}/tables")
    public ResponseEntity<List<Table>> listTables(@PathVariable String datasetId) {
        long startTime = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ########## Received GET /api/bigquery-jdbc/datasets/{}/tables ##########", datasetId);
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ HTTP REQUEST RECEIVED (JDBC)");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Endpoint: GET /api/bigquery-jdbc/datasets/{datasetId}/tables");
        logger.info("[DETAIL][JDBC] ║ Purpose: Retrieve all tables from dataset via JDBC");
        logger.info("[DETAIL][JDBC] ║ Request Time: {}", ZonedDateTime.now().format(FORMATTER));
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Request Parameters:");
        logger.info("[DETAIL][JDBC] ║   → PATH VARIABLE: datasetId = '{}'", datasetId);
        logger.info("[DETAIL][JDBC] ║ CONTROLLER: Processing request");
        logger.info("[DETAIL][JDBC] ║   → Method: JDBC/SQL");
        logger.info("[DETAIL][JDBC] ║   → Extracted datasetId from path: {}", datasetId);
        logger.info("[DETAIL][JDBC] ║   → Validating parameter...");
        if (datasetId == null || datasetId.trim().isEmpty()) {
            logger.warn("[DETAIL][JDBC] ║   ✗ Parameter validation failed: datasetId is empty or null");
            long endTime = System.currentTimeMillis();
            logger.error("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets/{}/tables failed in {} ms (Invalid datasetId) ##########", 
                datasetId, (endTime - startTime));
            return ResponseEntity.badRequest().body(null);
        }
        logger.info("[DETAIL][JDBC] ║   ✓ Parameter validation passed");
        logger.info("[DETAIL][JDBC] ║   → Calling JDBC service layer with datasetId: {}", datasetId);
        logger.info("[DETAIL][JDBC] ║");
        logger.info("[DETAIL][JDBC] ║ CONTROLLER → SERVICE: Delegating to BigQueryJdbcService.listTables()");
        logger.info("[DETAIL][JDBC] ║ ┌────────────────────────────────────────────────────");
        logger.info("[DETAIL][JDBC] ║ │ Entering JDBC SERVICE LAYER...");
        logger.info("[DETAIL][JDBC] ║ │ Parameter: datasetId = '{}'", datasetId);
        logger.info("[DETAIL][JDBC] ║ └────────────────────────────────────────────────────");

        try {
            List<Table> tables = bigQueryJdbcService.listTables(datasetId);
            long serviceEndTime = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║ ┌────────────────────────────────────────────────────");
            logger.info("[DETAIL][JDBC] ║ │ Returned from JDBC SERVICE LAYER");
            logger.info("[DETAIL][JDBC] ║ └────────────────────────────────────────────────────");
            logger.info("[DETAIL][JDBC] ║");
            logger.info("[DETAIL][JDBC] ║ SERVICE → CONTROLLER: Received response");
            logger.info("[DETAIL][JDBC] ║   ✓ Service call completed in {} ms", (serviceEndTime - startTime));
            logger.info("[DETAIL][JDBC] ║   ✓ Tables received: {}", tables.size());
            if (!tables.isEmpty()) {
                logger.info("[DETAIL][JDBC] ║   → Table IDs: {}", 
                    tables.stream().map(Table::getTableId).collect(Collectors.joining(", ")));
            }
            logger.info("[DETAIL][JDBC] ║");
            logger.info("[DETAIL][JDBC] ║ CONTROLLER: Preparing HTTP response");
            logger.info("[DETAIL][JDBC] ║   → Converting {} tables to JSON", tables.size());
            long serializationStartTime = System.currentTimeMillis();
            ResponseEntity<List<Table>> response = ResponseEntity.ok(tables);
            long serializationEndTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║   ✓ JSON serialization completed in {} ms", 
                (serializationEndTime - serializationStartTime));
            logger.info("[DETAIL][JDBC] ║   ✓ Response entity created");
            logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.info("[DETAIL][JDBC] ║ HTTP RESPONSE READY");
            logger.info("[DETAIL][JDBC] ║   ✓ Status Code: 200 OK");
            logger.info("[DETAIL][JDBC] ║   ✓ Content-Type: application/json");
            logger.info("[DETAIL][JDBC] ║   ✓ Body: List<Table> with {} items", tables.size());
            logger.info("[DETAIL][JDBC] ║   ✓ Method: JDBC/SQL");
            logger.info("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.info("[DETAIL][JDBC] ║ Breakdown:");
            logger.info("[DETAIL][JDBC] ║   • JDBC service layer: {} ms ({}%)", 
                (serviceEndTime - startTime), 
                String.format("%.0f", ((double)(serviceEndTime - startTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ║   • JSON serialization: {} ms ({}%)", 
                (serializationEndTime - serializationStartTime), 
                String.format("%.0f", ((double)(serializationEndTime - serializationStartTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.info("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets/{}/tables completed in {} ms (service: {} ms, serialization: {} ms, tables: {}) ##########",
                    datasetId, (endTime - startTime), (serviceEndTime - startTime), 
                    (serializationEndTime - serializationStartTime), tables.size());
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets/{}/tables failed in {} ms ##########", 
                datasetId, (endTime - startTime), e);
            logger.error("[DETAIL][JDBC] ║   ✗ Status: FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ HTTP RESPONSE FAILED");
            logger.error("[DETAIL][JDBC] ║   ✓ Status Code: 500 Internal Server Error");
            logger.error("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/datasets/{datasetId}/tables/{tableId}/schema")
    public ResponseEntity<List<Field>> getTableSchema(
            @PathVariable String datasetId,
            @PathVariable String tableId) {
        long startTime = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ########## Received GET /api/bigquery-jdbc/datasets/{}/tables/{}/schema ##########", 
                datasetId, tableId);
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ HTTP REQUEST RECEIVED");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Endpoint: GET /api/bigquery-jdbc/datasets/{}/tables/{}/schema", datasetId, tableId);
        logger.info("[DETAIL][JDBC] ║ Purpose: Retrieve table schema via JDBC");
        logger.info("[DETAIL][JDBC] ║ Request Time: {}", ZonedDateTime.now().format(FORMATTER));
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Request Parameters:");
        logger.info("[DETAIL][JDBC] ║   → datasetId: {}", datasetId);
        logger.info("[DETAIL][JDBC] ║   → tableId: {}", tableId);
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");

        try {
            // Call service
            long serviceStartTime = System.currentTimeMillis();
            List<Field> fields = bigQueryJdbcService.getTableSchema(datasetId, tableId);
            long serviceEndTime = System.currentTimeMillis();
            
            // Serialize
            long serializationStartTime = System.currentTimeMillis();
            ResponseEntity<List<Field>> response = ResponseEntity.ok(fields);
            long serializationEndTime = System.currentTimeMillis();
            
            long endTime = System.currentTimeMillis();
            
            // Log success
            logger.info("[DETAIL][JDBC] ║   ✓ Status: SUCCESS");
            logger.info("[DETAIL][JDBC] ║   ✓ Fields retrieved: {}", fields.size());
            logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.info("[DETAIL][JDBC] ║ HTTP RESPONSE READY");
            logger.info("[DETAIL][JDBC] ║   ✓ Status Code: 200 OK");
            logger.info("[DETAIL][JDBC] ║   ✓ Content-Type: application/json");
            logger.info("[DETAIL][JDBC] ║   ✓ Body: List<Field> with {} items", fields.size());
            logger.info("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.info("[DETAIL][JDBC] ║ Breakdown:");
            logger.info("[DETAIL][JDBC] ║   • Service layer: {} ms ({}%)", 
                (serviceEndTime - startTime), 
                String.format("%.0f", ((double)(serviceEndTime - startTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ║   • JSON serialization: {} ms ({}%)", 
                (serializationEndTime - serializationStartTime), 
                String.format("%.0f", ((double)(serializationEndTime - serializationStartTime) / (endTime - startTime)) * 100));
            logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.info("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets/{}/tables/{}/schema completed in {} ms (service: {} ms, fields: {}) ##########",
                    datasetId, tableId, (endTime - startTime), (serviceEndTime - startTime), fields.size());
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[TIMING][JDBC] ########## GET /api/bigquery-jdbc/datasets/{}/tables/{}/schema failed in {} ms ##########", 
                datasetId, tableId, (endTime - startTime), e);
            logger.error("[DETAIL][JDBC] ║   ✗ Status: FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ HTTP RESPONSE FAILED");
            logger.error("[DETAIL][JDBC] ║   ✓ Status Code: 500 Internal Server Error");
            logger.error("[DETAIL][JDBC] ║   ✓ Total request time: {} ms", (endTime - startTime));
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            return ResponseEntity.status(500).body(null);
        }
    }
}
