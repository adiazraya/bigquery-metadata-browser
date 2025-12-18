package com.mercadolibre.incidenciabq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Configuration for BigQuery JDBC Connection using SIMBA driver
 * 
 * IMPORTANT: This requires the SIMBA BigQuery JDBC driver to be installed.
 * Download from: https://storage.googleapis.com/simba-bq-release/jdbc/
 * 
 * Install to local Maven repository:
 * mvn install:install-file \
 *   -Dfile=/path/to/GoogleBigQueryJDBC42.jar \
 *   -DgroupId=com.simba.googlebigquery \
 *   -DartifactId=jdbc \
 *   -Dversion=1.6.5.1001 \
 *   -Dpackaging=jar
 */
@Configuration
public class BigQueryJdbcConfig {

    private static final Logger logger = LoggerFactory.getLogger(BigQueryJdbcConfig.class);

    @Value("${bigquery.project.id}")
    private String projectId;

    @Value("${bigquery.service.account.key.path}")
    private String serviceAccountKeyPath;

    /**
     * Note: This bean creation is commented out until SIMBA driver is installed.
     * Uncomment after installing the SIMBA JDBC driver.
     */
    /*
    @Bean(name = "bigQueryJdbcDataSource")
    public DataSource bigQueryJdbcDataSource() {
        long startTime = System.currentTimeMillis();
        logger.info("[TIMING] Starting JDBC DataSource initialization");
        logger.info("[DETAIL] ┌─────────────────────────────────────────────────────");
        logger.info("[DETAIL] │ BACKEND: Initializing BigQuery JDBC DataSource");
        logger.info("[DETAIL] ├─────────────────────────────────────────────────────");
        
        try {
            // JDBC URL format for SIMBA BigQuery driver
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
            
            logger.info("[DETAIL] │ Step 1: Creating JDBC DataSource");
            logger.info("[DETAIL] │   → JDBC URL: {}", jdbcUrl.replaceAll("OAuthPvtKeyPath=[^;]+", "OAuthPvtKeyPath=***"));
            logger.info("[DETAIL] │   → Project ID: {}", projectId);
            logger.info("[DETAIL] │   → Driver: Simba BigQuery JDBC");
            
            // Create BasicDataSource (requires commons-dbcp2 dependency)
            org.apache.commons.dbcp2.BasicDataSource dataSource = new org.apache.commons.dbcp2.BasicDataSource();
            dataSource.setDriverClassName("com.simba.googlebigquery.jdbc.Driver");
            dataSource.setUrl(jdbcUrl);
            dataSource.setInitialSize(1);
            dataSource.setMaxTotal(5);
            
            logger.info("[DETAIL] │   ✓ DataSource configured successfully");
            
            // Test connection
            long testStart = System.currentTimeMillis();
            logger.info("[DETAIL] │ Step 2: Testing JDBC connection");
            try (Connection conn = dataSource.getConnection()) {
                logger.info("[DETAIL] │   ✓ Connection test successful");
                logger.info("[DETAIL] │   ✓ Database: {}", conn.getCatalog());
                long testTime = System.currentTimeMillis() - testStart;
                logger.info("[TIMING] Connection test completed in {} ms", testTime);
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("[DETAIL] │   ✓ Total initialization time: {} ms", totalTime);
            logger.info("[DETAIL] └─────────────────────────────────────────────────────");
            logger.info("[TIMING] JDBC DataSource initialized in {} ms", totalTime);
            
            return dataSource;
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("[DETAIL] │   ✗ FAILED to initialize JDBC DataSource");
            logger.error("[DETAIL] │   ✗ Error: {}", e.getMessage());
            logger.error("[DETAIL] └─────────────────────────────────────────────────────");
            logger.error("[TIMING] JDBC DataSource initialization failed after {} ms", totalTime, e);
            throw new RuntimeException("Failed to initialize BigQuery JDBC DataSource", e);
        }
    }
    */
    
    /**
     * Temporary method to check if JDBC driver is available
     */
    @Bean(name = "jdbcDriverAvailable")
    public boolean isJdbcDriverAvailable() {
        try {
            Class.forName("com.simba.googlebigquery.jdbc.Driver");
            logger.info("[JDBC] ✓ SIMBA BigQuery JDBC Driver is available");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("[JDBC] ✗ SIMBA BigQuery JDBC Driver NOT found");
            logger.warn("[JDBC] Please install the driver. See SIMBA_INSTALLATION.md for instructions.");
            return false;
        }
    }
}
