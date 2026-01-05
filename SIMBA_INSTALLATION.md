# SIMBA BigQuery JDBC Driver Installation Guide

This guide explains how to install and configure the SIMBA BigQuery JDBC driver to enable the **Metadata Info with JDBC** functionality.

## Overview

The application currently supports two connection methods:
1. **REST API** (Google Cloud BigQuery Client Library) - ✅ Already working
2. **JDBC** (SIMBA BigQuery JDBC Driver) - ⚠️ Requires manual installation

## Why Manual Installation?

The SIMBA BigQuery JDBC driver is not available in Maven Central repository. You need to download it from Google's storage and install it locally.

## Installation Steps

### Step 1: Download the SIMBA Driver

Download the latest SIMBA BigQuery JDBC driver from Google's storage:

**Official Download Link:**
- Documentation: [SIMBA BigQuery JDBC Connector Install Guide v1.6.5.1001](https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide_1.6.5.1001.pdf)
- Driver Download: https://storage.googleapis.com/simba-bq-release/jdbc/

Look for the file: `GoogleBigQueryJDBC42.jar` or similar.

### Step 2: Install to Local Maven Repository

Once downloaded, install the JAR file to your local Maven repository:

```bash
mvn install:install-file \
  -Dfile=/path/to/GoogleBigQueryJDBC42.jar \
  -DgroupId=com.simba.googlebigquery \
  -DartifactId=jdbc \
  -Dversion=1.6.5.1001 \
  -Dpackaging=jar
```

**Replace `/path/to/GoogleBigQueryJDBC42.jar` with the actual path to your downloaded JAR file.**

### Step 3: Add Dependency to pom.xml

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.simba.googlebigquery</groupId>
    <artifactId>jdbc</artifactId>
    <version>1.6.5.1001</version>
</dependency>
```

### Step 4: Uncomment JDBC Configuration

In `src/main/java/com/mercadolibre/incidenciabq/config/BigQueryJdbcConfig.java`, uncomment the `@Bean` method for `bigQueryJdbcDataSource()`.

Find this section:
```java
/**
 * Note: This bean creation is commented out until SIMBA driver is installed.
 * Uncomment after installing the SIMBA JDBC driver.
 */
/*
@Bean(name = "bigQueryJdbcDataSource")
public DataSource bigQueryJdbcDataSource() {
```

And uncomment the entire method (remove `/*` and `*/`).

### Step 5: Add Required Application Properties

Ensure your `application.properties` has the service account email:

```properties
bigquery.service.account.email=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com
```

### Step 6: Rebuild and Run

```bash
mvn clean install
./run.sh
```

## Verification

Once installed, you should see in the logs:

```
[JDBC] ✓ SIMBA BigQuery JDBC Driver is available
```

Navigate to http://localhost:8080 and click on **"Metadata Info with JDBC"** to test the JDBC connection.

## JDBC Connection Details

The JDBC connection uses the following format:

```
jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;
ProjectId=ehc-alberto-diazraya-35c897;
OAuthType=0;
OAuthServiceAcctEmail=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com;
OAuthPvtKeyPath=./service-account-key.json;
```

## Comparison: REST API vs JDBC

| Feature | REST API | JDBC |
|---------|----------|------|
| **Installation** | Built-in (Maven Central) | Manual download required |
| **Connection Method** | HTTP/REST | Traditional database connection |
| **Query Method** | Java API methods | SQL queries on INFORMATION_SCHEMA |
| **Performance** | Optimized for BigQuery | Standard JDBC overhead |
| **Metadata Access** | Direct API calls | SQL-based queries |
| **Use Case** | Modern applications | Legacy systems, SQL compatibility |

## Troubleshooting

### Driver Not Found

If you see:
```
[JDBC] ✗ SIMBA BigQuery JDBC Driver NOT found
```

- Ensure you've installed the driver to your local Maven repository
- Check that the version numbers match in the install command and pom.xml
- Run `mvn clean install` to refresh dependencies

### ClassNotFoundException

If you see `ClassNotFoundException: com.simba.googlebigquery.jdbc.Driver`:

- The JAR file wasn't properly installed to Maven
- Try the installation command again with the correct path
- Verify the JAR exists in `~/.m2/repository/com/simba/googlebigquery/jdbc/1.6.5.1001/`

### Connection Errors

If JDBC connection fails:

- Verify your service account key file exists at `./service-account-key.json`
- Check that the service account has BigQuery permissions
- Review the detailed logs in `logs/incidencia-bq.log`

## Current Status

Currently, the application can run **without** the SIMBA driver. The JDBC endpoints will return an error message indicating the driver is not installed. The REST API endpoints will work normally.

Once you install the SIMBA driver, both methods will be fully functional and you can compare their performance and behavior.

## Resources

- [SIMBA BigQuery JDBC Documentation](https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide_1.6.5.1001.pdf)
- [Google BigQuery Documentation](https://cloud.google.com/bigquery/docs)
- [BigQuery INFORMATION_SCHEMA](https://cloud.google.com/bigquery/docs/information-schema-intro)

## Support

For issues with SIMBA driver installation, please refer to the official SIMBA documentation or contact your system administrator.

For application-specific issues, check the detailed logs in `logs/incidencia-bq.log`.






