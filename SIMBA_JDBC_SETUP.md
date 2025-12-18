# SIMBA BigQuery JDBC Driver Setup Guide

## Overview

This application now supports **two different approaches** for connecting to Google BigQuery:

1. **REST API Approach** (Currently Working) - Uses Google Cloud BigQuery Client Library
2. **JDBC Approach** (Requires SIMBA Driver) - Uses traditional JDBC patterns

## Why SIMBA JDBC Driver is NOT in Maven Central

The SIMBA BigQuery JDBC Driver is **proprietary software** from insightsoftware (formerly Magnitude) and is **not available in Maven Central**. It must be **manually downloaded** from Google Cloud.

According to the [official documentation](https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide_1.6.5.1001.pdf), SIMBA is a licensed JDBC 4.2 compliant driver specifically designed for BigQuery connectivity.

## How to Download and Install SIMBA JDBC Driver

### Step 1: Download the Driver

Visit the Google Cloud BigQuery JDBC Drivers page:
- **Official URL**: https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers
- Look for **"JDBC drivers for BigQuery"**
- Download the latest **Simba Google BigQuery JDBC Driver**
- You'll get a ZIP file named something like: `SimbaBigQueryJDBC42-[Version].zip`

### Step 2: Extract the ZIP File

```bash
# Create a lib directory in your project
mkdir -p /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/lib

# Extract the ZIP file
unzip SimbaBigQueryJDBC42-*.zip -d simba-driver

# Copy all JAR files to your project's lib directory
cp simba-driver/*.jar /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/lib/
```

### Step 3: Add JAR Files to Your Project

#### Option A: Manual Classpath Addition

Add the SIMBA JARs to your classpath when running the application:

```bash
java -cp "target/incidencia-bq-1.0.0.jar:lib/*" com.mercadolibre.incidenciabq.IncidenciaBQApplication
```

#### Option B: Maven System Dependency (Recommended)

Add to your `pom.xml`:

```xml
<dependencies>
    <!-- SIMBA BigQuery JDBC Driver (Manual Install Required) -->
    <dependency>
        <groupId>com.simba.googlebigquery.jdbc</groupId>
        <artifactId>googlebigquery-jdbc</artifactId>
        <version>1.6.5</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/GoogleBigQueryJDBC42.jar</systemPath>
    </dependency>
</dependencies>
```

### Step 4: Verify Installation

Once installed, the JDBC approach will work. You can test it by:

1. Start the application: `./run.sh`
2. Open your browser to: http://localhost:8080
3. Click on **"Metadata Info with JDBC"**
4. If properly installed, you'll see datasets loaded via JDBC

## System Requirements

According to the SIMBA documentation:
- **Java Runtime Environment**: JRE 8.0, 11.0, or 21.0
- **JDBC Version**: JDBC 4.2 compliant
- **Operating Systems**: All major OS (Windows, macOS, Linux)

## Connection Configuration

The JDBC connection uses the following configuration:

```
JDBC URL Pattern:
jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;
ProjectId=ehc-alberto-diazraya-35c897;
OAuthType=0;
OAuthServiceAcctEmail=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com;
OAuthPvtKeyPath=/path/to/service-account-key.json
```

### Authentication Parameters

- **OAuthType=0**: Service Account authentication
- **OAuthServiceAcctEmail**: Your service account email
- **OAuthPvtKeyPath**: Path to your service account JSON key file

## Current Application Behavior

### Without SIMBA Driver Installed:
- The **REST API approach** works perfectly ✅
- The **JDBC approach** will gracefully fail with an informative message ⚠️
- Application logs will indicate SIMBA driver is not found
- The application will still start and run (REST API endpoints work)

### With SIMBA Driver Installed:
- Both **REST API** and **JDBC** approaches work ✅✅
- You can compare performance between the two approaches
- Detailed logging shows the different execution paths

## Comparing the Two Approaches

### REST API Approach (Google Cloud Client Library)
**Pros:**
- ✅ No manual driver installation needed
- ✅ Official Google Cloud library
- ✅ Modern async/reactive patterns
- ✅ Better integration with Google Cloud ecosystem
- ✅ Automatic dependency management via Maven

**Cons:**
- ❌ Less familiar to traditional database developers
- ❌ Not standard JDBC

### JDBC Approach (SIMBA Driver)
**Pros:**
- ✅ Standard JDBC patterns
- ✅ Familiar to database developers
- ✅ Works with JDBC-based tools
- ✅ Uses DatabaseMetaData APIs

**Cons:**
- ❌ Requires manual driver installation
- ❌ Additional JAR files to manage
- ❌ May require license for enterprise use
- ❌ Not in Maven Central

## Troubleshooting

### Issue: `ClassNotFoundException: com.simba.googlebigquery.jdbc.Driver`

**Solution**: The SIMBA driver JARs are not in your classpath. Follow the installation steps above.

### Issue: JDBC endpoints return empty results

**Solution**: Check the logs. If you see "JDBC Connection not available", the driver is not properly installed.

### Issue: Authentication errors with JDBC

**Solution**: Ensure your service account key file path is correct and the service account has BigQuery permissions.

## Application Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Main Index Page                          │
│                   (index.html)                              │
└────────────────┬────────────────────────────┬───────────────┘
                 │                            │
                 ▼                            ▼
    ┌────────────────────────┐   ┌────────────────────────┐
    │  Metadata Info with    │   │  Metadata Info with    │
    │  API (REST)            │   │  JDBC (SIMBA)          │
    │  api-metadata.html     │   │  jdbc-metadata.html    │
    └────────┬───────────────┘   └────────┬───────────────┘
             │                            │
             ▼                            ▼
    ┌────────────────────────┐   ┌────────────────────────┐
    │ BigQueryController     │   │ BigQueryJdbcController │
    │ /api/bigquery/*        │   │ /api/bigquery-jdbc/*   │
    └────────┬───────────────┘   └────────┬───────────────┘
             │                            │
             ▼                            ▼
    ┌────────────────────────┐   ┌────────────────────────┐
    │ BigQueryService        │   │ BigQueryJdbcService    │
    │ (REST API Client)      │   │ (JDBC DatabaseMetaData)│
    └────────┬───────────────┘   └────────┬───────────────┘
             │                            │
             ▼                            ▼
    ┌────────────────────────┐   ┌────────────────────────┐
    │ Google Cloud           │   │ SIMBA JDBC Driver      │
    │ BigQuery Client        │   │ Connection             │
    └────────────────────────┘   └────────────────────────┘
```

## Reference Documentation

- **SIMBA Installation Guide**: [PDF Documentation](https://storage.googleapis.com/simba-bq-release/jdbc/Simba%20Google%20BigQuery%20JDBC%20Connector%20Install%20and%20Configuration%20Guide_1.6.5.1001.pdf)
- **Google Cloud JDBC Drivers**: https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers
- **BigQuery JDBC Documentation**: https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers

## Support

For issues specific to:
- **SIMBA Driver**: Contact insightsoftware support
- **This Application**: Check logs in `logs/incidencia-bq.log`
- **BigQuery API**: Check Google Cloud documentation

---

**Note**: The REST API approach is recommended for new development unless you specifically need JDBC compatibility for legacy tools or frameworks.

