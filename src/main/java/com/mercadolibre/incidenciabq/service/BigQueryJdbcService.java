package com.mercadolibre.incidenciabq.service;

import com.mercadolibre.incidenciabq.model.Dataset;
import com.mercadolibre.incidenciabq.model.Table;
import com.mercadolibre.incidenciabq.model.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for BigQuery metadata operations using JDBC
 * 
 * This service uses SIMBA BigQuery JDBC driver to fetch metadata
 * using standard SQL INFORMATION_SCHEMA queries.
 * 
 * Note: This service will throw exceptions if SIMBA driver is not installed.
 * See SIMBA_INSTALLATION.md for installation instructions.
 */
@Service
public class BigQueryJdbcService {

    private static final Logger logger = LoggerFactory.getLogger(BigQueryJdbcService.class);

    @Value("${bigquery.project.id}")
    private String projectId;

    @Value("${bigquery.service.account.email:}")
    private String serviceAccountEmail;

    @Value("${bigquery.service.account.key.path}")
    private String serviceAccountKeyPath;

    /**
     * Get JDBC connection to BigQuery
     * Note: This will work once SIMBA driver is installed
     */
    private Connection getConnection() throws SQLException {
        long startTime = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] Starting JDBC connection acquisition");
        logger.info("[DETAIL][JDBC] ┌─────────────────────────────────────────────────────");
        logger.info("[DETAIL][JDBC] │ BACKEND: Getting JDBC Connection");
        logger.info("[DETAIL][JDBC] ├─────────────────────────────────────────────────────");
        
        try {
            String jdbcUrl = String.format(
                "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;" +
                "ProjectId=%s;" +
                "OAuthType=0;" +
                "OAuthServiceAcctEmail=%s;" +
                "OAuthPvtKeyPath=%s;",
                projectId,
                serviceAccountEmail,
                serviceAccountKeyPath
            );
            
            logger.info("[DETAIL][JDBC] │ Step 1: Creating JDBC connection");
            logger.info("[DETAIL][JDBC] │   → Driver: Simba BigQuery JDBC");
            logger.info("[DETAIL][JDBC] │   → Project: {}", projectId);
            
            Connection conn = DriverManager.getConnection(jdbcUrl);
            
            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("[DETAIL][JDBC] │   ✓ Connection acquired successfully");
            logger.info("[DETAIL][JDBC] └─────────────────────────────────────────────────────");
            logger.info("[TIMING][JDBC] Connection acquired in {} ms", totalTime);
            
            return conn;
            
        } catch (SQLException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("[DETAIL][JDBC] │   ✗ Failed to acquire connection");
            logger.error("[DETAIL][JDBC] │   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] └─────────────────────────────────────────────────────");
            logger.error("[TIMING][JDBC] Connection failed after {} ms", totalTime, e);
            throw e;
        }
    }

    /**
     * List all datasets using JDBC INFORMATION_SCHEMA
     */
    public List<Dataset> listDatasets() {
        long operationStart = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ========== Starting listDatasets operation ==========");
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ BACKEND PROCESSING: List All Datasets (JDBC)");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        
        List<Dataset> datasets = new ArrayList<>();
        
        try {
            // Check if JDBC driver is available
            try {
                Class.forName("com.simba.googlebigquery.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                logger.error("[DETAIL][JDBC] ║   ✗ SIMBA JDBC Driver NOT available");
                logger.error("[DETAIL][JDBC] ║   ✗ Please install the driver first");
                logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                throw new RuntimeException("SIMBA JDBC Driver not installed. Please see SIMBA_INSTALLATION.md", e);
            }
            
            // Step 1: Get JDBC connection
            long connStart = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║ Step 1/3: Acquiring JDBC Connection");
            
            try (Connection conn = getConnection()) {
                long connTime = System.currentTimeMillis() - connStart;
                logger.info("[TIMING][JDBC] Step 1/3: Connection acquired in {} ms", connTime);
                
                // Step 2: Execute SQL query
                long queryStart = System.currentTimeMillis();
                logger.info("[DETAIL][JDBC] ║ Step 2/3: Executing SQL Query");
                logger.info("[DETAIL][JDBC] ║   ┌─ SQL QUERY DETAILS ─────────────────────────────");
                logger.info("[DETAIL][JDBC] ║   │ Query Type: SELECT");
                logger.info("[DETAIL][JDBC] ║   │ Target: INFORMATION_SCHEMA.SCHEMATA");
                logger.info("[DETAIL][JDBC] ║   │ Project: {}", projectId);
                logger.info("[DETAIL][JDBC] ║   └────────────────────────────────────────────────");
                logger.info("[DETAIL][JDBC] ║   → Executing SQL query...");
                
                String sql = String.format(
                    "SELECT schema_name, catalog_name " +
                    "FROM `%s.INFORMATION_SCHEMA.SCHEMATA` " +
                    "ORDER BY schema_name",
                    projectId
                );
                
                logger.info("[DETAIL][JDBC] ║   → SQL: {}", sql);
                
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    long queryTime = System.currentTimeMillis() - queryStart;
                    logger.info("[TIMING][JDBC] Step 2/3: Query executed in {} ms", queryTime);
                    logger.info("[DETAIL][JDBC] ║   ✓ SQL query executed successfully");
                    logger.info("[DETAIL][JDBC] ║   ✓ ResultSet received");
                    
                    // Step 3: Process results
                    long processStart = System.currentTimeMillis();
                    logger.info("[DETAIL][JDBC] ║ Step 3/3: Processing ResultSet");
                    logger.info("[DETAIL][JDBC] ║   → Iterating through result rows...");
                    
                    int count = 0;
                    boolean verboseLogging = false;
                    while (rs.next()) {
                        count++;
                        String schemaName = rs.getString("schema_name");
                        String catalogName = rs.getString("catalog_name");
                        
                        // Only log details for first 10 datasets
                        verboseLogging = (count <= 10);
                        
                        if (verboseLogging) {
                            logger.info("[DETAIL][JDBC] ║   ┌─ DATASET #{} ──────────────────────────────────", count);
                            logger.info("[DETAIL][JDBC] ║   │ Processing row from ResultSet");
                            logger.info("[DETAIL][JDBC] ║   │ Schema Name: {}", schemaName);
                            logger.info("[DETAIL][JDBC] ║   │ Catalog: {}", catalogName);
                        } else if (count % 100 == 0) {
                            logger.info("[DETAIL][JDBC] ║   → Processed {} datasets so far...", count);
                        }
                        
                        Dataset dataset = new Dataset();
                        dataset.setDatasetId(schemaName);
                        dataset.setProjectId(projectId);
                        dataset.setFriendlyName(schemaName);
                        
                        if (verboseLogging) {
                            logger.info("[DETAIL][JDBC] ║   │ → Extracted Fields:");
                            logger.info("[DETAIL][JDBC] ║   │   • datasetId: {}", dataset.getDatasetId());
                            logger.info("[DETAIL][JDBC] ║   │   • projectId: {}", dataset.getProjectId());
                            logger.info("[DETAIL][JDBC] ║   │ → Method: JDBC/SQL");
                            logger.info("[DETAIL][JDBC] ║   │ ✓ Dataset object created and added to list");
                            logger.info("[DETAIL][JDBC] ║   └────────────────────────────────────────────────");
                        }
                        
                        datasets.add(dataset);
                    }
                    
                    long processTime = System.currentTimeMillis() - processStart;
                    logger.info("[TIMING][JDBC] Step 3/3: Processed {} datasets in {} ms", count, processTime);
                }
                
                long totalTime = System.currentTimeMillis() - operationStart;
                logger.info("[DETAIL][JDBC] ║");
                logger.info("[DETAIL][JDBC] ║ Processing Summary:");
                logger.info("[DETAIL][JDBC] ║   • Total datasets found: {}", datasets.size());
                logger.info("[DETAIL][JDBC] ║   • Method: JDBC/SQL INFORMATION_SCHEMA");
                logger.info("[DETAIL][JDBC] ║   • Ready to serialize to JSON for response");
                logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
                logger.info("[DETAIL][JDBC] ║ OPERATION COMPLETE");
                logger.info("[DETAIL][JDBC] ║   ✓ Total operation time: {} ms", totalTime);
                logger.info("[DETAIL][JDBC] ║   ✓ Datasets returned: {}", datasets.size());
                logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                logger.info("[TIMING][JDBC] ========== listDatasets completed in {} ms ==========", totalTime);
            }
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ OPERATION FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ║   ✗ Time until failure: {} ms", totalTime);
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.error("[TIMING][JDBC] Error listing datasets after {} ms", totalTime, e);
            throw new RuntimeException("Failed to list datasets via JDBC", e);
        }
        
        return datasets;
    }

    /**
     * List tables in a dataset using JDBC INFORMATION_SCHEMA
     */
    public List<Table> listTables(String datasetId) {
        long operationStart = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ========== Starting listTables operation for dataset: {} ==========", datasetId);
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ BACKEND PROCESSING: List Tables in Dataset (JDBC)");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Input Parameter:");
        logger.info("[DETAIL][JDBC] ║   → datasetId: {}", datasetId);
        
        List<Table> tables = new ArrayList<>();
        
        try {
            // Check if JDBC driver is available
            try {
                Class.forName("com.simba.googlebigquery.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                logger.error("[DETAIL][JDBC] ║   ✗ SIMBA JDBC Driver NOT available");
                logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                throw new RuntimeException("SIMBA JDBC Driver not installed", e);
            }
            
            // Step 1: Get JDBC connection
            long connStart = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║ Step 1/3: Acquiring JDBC Connection");
            
            try (Connection conn = getConnection()) {
                long connTime = System.currentTimeMillis() - connStart;
                logger.info("[TIMING][JDBC] Step 1/3: Connection acquired in {} ms", connTime);
                
                // Step 2: Execute SQL query
                long queryStart = System.currentTimeMillis();
                logger.info("[DETAIL][JDBC] ║ Step 2/3: Executing SQL Query");
                logger.info("[DETAIL][JDBC] ║   ┌─ SQL QUERY DETAILS ─────────────────────────────");
                logger.info("[DETAIL][JDBC] ║   │ Query Type: SELECT");
                logger.info("[DETAIL][JDBC] ║   │ Target: INFORMATION_SCHEMA.TABLES");
                logger.info("[DETAIL][JDBC] ║   │ Dataset: {}", datasetId);
                logger.info("[DETAIL][JDBC] ║   └────────────────────────────────────────────────");
                
                String sql = String.format(
                    "SELECT table_name, table_type, creation_time " +
                    "FROM `%s.%s.INFORMATION_SCHEMA.TABLES` " +
                    "ORDER BY table_name",
                    projectId, datasetId
                );
                
                logger.info("[DETAIL][JDBC] ║   → SQL: {}", sql);
                logger.info("[DETAIL][JDBC] ║   → Executing SQL query...");
                
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    long queryTime = System.currentTimeMillis() - queryStart;
                    logger.info("[TIMING][JDBC] Step 2/3: Query executed in {} ms", queryTime);
                    logger.info("[DETAIL][JDBC] ║   ✓ SQL query executed successfully");
                    
                    // Step 3: Process results
                    long processStart = System.currentTimeMillis();
                    logger.info("[DETAIL][JDBC] ║ Step 3/3: Processing ResultSet");
                    
                    int count = 0;
                    boolean verboseLogging = false;
                    while (rs.next()) {
                        count++;
                        String tableName = rs.getString("table_name");
                        String tableType = rs.getString("table_type");
                        Timestamp creationTime = rs.getTimestamp("creation_time");
                        
                        // Only log details for first 10 tables
                        verboseLogging = (count <= 10);
                        
                        if (verboseLogging) {
                            logger.info("[DETAIL][JDBC] ║   ┌─ TABLE #{} ─────────────────────────────────────", count);
                            logger.info("[DETAIL][JDBC] ║   │ Table Name: {}", tableName);
                            logger.info("[DETAIL][JDBC] ║   │ Table Type: {}", tableType);
                            logger.info("[DETAIL][JDBC] ║   │ → Extracted Fields:");
                        } else if (count % 1000 == 0) {
                            logger.info("[DETAIL][JDBC] ║   → Processed {} tables so far...", count);
                        }
                        
                        if (verboseLogging) {
                            logger.info("[DETAIL][JDBC] ║   │   • tableId: {}", tableName);
                            logger.info("[DETAIL][JDBC] ║   │   • datasetId: {}", datasetId);
                            logger.info("[DETAIL][JDBC] ║   │   • type: {}", tableType);
                            logger.info("[DETAIL][JDBC] ║   │   • method: JDBC/SQL");
                            logger.info("[DETAIL][JDBC] ║   │ ✓ Table object created");
                            logger.info("[DETAIL][JDBC] ║   └────────────────────────────────────────────────");
                        }
                        
                        Table table = new Table();
                        table.setTableId(tableName);
                        table.setDatasetId(datasetId);
                        table.setProjectId(projectId);
                        table.setType(tableType);
                        if (creationTime != null) {
                            table.setCreationTime(creationTime.getTime());
                        }
                        
                        tables.add(table);
                    }
                    
                    long processTime = System.currentTimeMillis() - processStart;
                    logger.info("[TIMING][JDBC] Step 3/3: Processed {} tables in {} ms", count, processTime);
                }
                
                long totalTime = System.currentTimeMillis() - operationStart;
                logger.info("[DETAIL][JDBC] ║");
                logger.info("[DETAIL][JDBC] ║ Processing Summary:");
                logger.info("[DETAIL][JDBC] ║   • Total tables found: {}", tables.size());
                logger.info("[DETAIL][JDBC] ║   • Dataset: {}", datasetId);
                logger.info("[DETAIL][JDBC] ║   • Method: JDBC/SQL INFORMATION_SCHEMA");
                logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
                logger.info("[DETAIL][JDBC] ║ OPERATION COMPLETE");
                logger.info("[DETAIL][JDBC] ║   ✓ Total operation time: {} ms", totalTime);
                logger.info("[DETAIL][JDBC] ║   ✓ Tables returned: {}", tables.size());
                logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                logger.info("[TIMING][JDBC] ========== listTables completed in {} ms ==========", totalTime);
            }
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ OPERATION FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Dataset: {}", datasetId);
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ║   ✗ Time until failure: {} ms", totalTime);
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.error("[TIMING][JDBC] Error listing tables after {} ms", totalTime, e);
            throw new RuntimeException("Failed to list tables via JDBC for dataset: " + datasetId, e);
        }
        
        return tables;
    }

    /**
     * Get table schema using JDBC INFORMATION_SCHEMA
     */
    public List<Field> getTableSchema(String datasetId, String tableId) {
        long operationStart = System.currentTimeMillis();
        logger.info("[TIMING][JDBC] ========== Starting getTableSchema operation for {}.{} ==========", datasetId, tableId);
        logger.info("[DETAIL][JDBC] ╔══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ BACKEND PROCESSING: Get Table Schema (JDBC)");
        logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
        logger.info("[DETAIL][JDBC] ║ Input Parameters:");
        logger.info("[DETAIL][JDBC] ║   → datasetId: {}", datasetId);
        logger.info("[DETAIL][JDBC] ║   → tableId: {}", tableId);
        
        List<Field> fields = new ArrayList<>();
        
        try {
            // Check if JDBC driver is available
            try {
                Class.forName("com.simba.googlebigquery.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                logger.error("[DETAIL][JDBC] ║   ✗ SIMBA JDBC Driver NOT available");
                logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                throw new RuntimeException("SIMBA JDBC Driver not installed", e);
            }
            
            // Step 1: Get JDBC connection
            long connStart = System.currentTimeMillis();
            logger.info("[DETAIL][JDBC] ║ Step 1/3: Acquiring JDBC Connection");
            
            try (Connection conn = getConnection()) {
                long connTime = System.currentTimeMillis() - connStart;
                logger.info("[TIMING][JDBC] Step 1/3: Connection acquired in {} ms", connTime);
                
                // Step 2: Execute SQL query
                long queryStart = System.currentTimeMillis();
                logger.info("[DETAIL][JDBC] ║ Step 2/3: Executing SQL Query");
                logger.info("[DETAIL][JDBC] ║   ┌─ SQL QUERY DETAILS ─────────────────────────────");
                logger.info("[DETAIL][JDBC] ║   │ Query Type: SELECT");
                logger.info("[DETAIL][JDBC] ║   │ Target: INFORMATION_SCHEMA.COLUMNS");
                logger.info("[DETAIL][JDBC] ║   │ Table: {}.{}", datasetId, tableId);
                logger.info("[DETAIL][JDBC] ║   └────────────────────────────────────────────────");
                
                String sql = String.format(
                    "SELECT column_name, data_type, is_nullable, column_default " +
                    "FROM `%s.%s.INFORMATION_SCHEMA.COLUMNS` " +
                    "WHERE table_name = '%s' " +
                    "ORDER BY ordinal_position",
                    projectId, datasetId, tableId
                );
                
                logger.info("[DETAIL][JDBC] ║   → SQL: {}", sql);
                logger.info("[DETAIL][JDBC] ║   → Executing SQL query...");
                
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    long queryTime = System.currentTimeMillis() - queryStart;
                    logger.info("[TIMING][JDBC] Step 2/3: Query executed in {} ms", queryTime);
                    logger.info("[DETAIL][JDBC] ║   ✓ SQL query executed successfully");
                    
                    // Step 3: Process results
                    long processStart = System.currentTimeMillis();
                    logger.info("[DETAIL][JDBC] ║ Step 3/3: Processing ResultSet");
                    
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        String columnName = rs.getString("column_name");
                        String dataType = rs.getString("data_type");
                        String isNullable = rs.getString("is_nullable");
                        
                        Field field = new Field();
                        field.setName(columnName);
                        field.setType(dataType);
                        field.setMode("YES".equals(isNullable) ? "NULLABLE" : "REQUIRED");
                        field.setDescription(null); // INFORMATION_SCHEMA.COLUMNS doesn't have description
                        
                        fields.add(field);
                        
                        if (count <= 5) {
                            logger.info("[DETAIL][JDBC] ║   → Field #{}: {} ({}, {})", 
                                    count, field.getName(), field.getType(), field.getMode());
                        }
                    }
                    
                    if (count > 5) {
                        logger.info("[DETAIL][JDBC] ║   → ... and {} more fields", count - 5);
                    }
                    
                    long processTime = System.currentTimeMillis() - processStart;
                    logger.info("[TIMING][JDBC] Step 3/3: Processed {} fields in {} ms", count, processTime);
                }
                
                long totalTime = System.currentTimeMillis() - operationStart;
                logger.info("[DETAIL][JDBC] ║");
                logger.info("[DETAIL][JDBC] ║ Processing Summary:");
                logger.info("[DETAIL][JDBC] ║   • Total fields found: {}", fields.size());
                logger.info("[DETAIL][JDBC] ║   • Table: {}.{}", datasetId, tableId);
                logger.info("[DETAIL][JDBC] ║   • Method: JDBC/SQL INFORMATION_SCHEMA");
                logger.info("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
                logger.info("[DETAIL][JDBC] ║ OPERATION COMPLETE");
                logger.info("[DETAIL][JDBC] ║   ✓ Total operation time: {} ms", totalTime);
                logger.info("[DETAIL][JDBC] ║   ✓ Fields returned: {}", fields.size());
                logger.info("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
                logger.info("[TIMING][JDBC] ========== getTableSchema completed in {} ms ==========", totalTime);
            }
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - operationStart;
            logger.error("[DETAIL][JDBC] ╠══════════════════════════════════════════════════════════");
            logger.error("[DETAIL][JDBC] ║ OPERATION FAILED");
            logger.error("[DETAIL][JDBC] ║   ✗ Table: {}.{}", datasetId, tableId);
            logger.error("[DETAIL][JDBC] ║   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL][JDBC] ║   ✗ Time until failure: {} ms", totalTime);
            logger.error("[DETAIL][JDBC] ╚══════════════════════════════════════════════════════════");
            logger.error("[TIMING][JDBC] Error getting table schema after {} ms", totalTime, e);
            throw new RuntimeException("Failed to get table schema via JDBC for: " + datasetId + "." + tableId, e);
        }
        
        return fields;
    }
}
