# REST API Java Library - Complete Details

## The Answer

When you make REST API calls using this code:

```java
BigQuery bigQuery = BigQueryOptions.newBuilder()
    .setProjectId(projectId)
    .setCredentials(credentials)
    .build()
    .getService();
```

You are using the **Google Cloud BigQuery Java Client Library**:

```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-bigquery</artifactId>
    <version>2.38.2</version>
</dependency>
```

## Library Details

### Official Name
**Google Cloud BigQuery Client for Java**

### Maven Coordinates
- **Group ID**: `com.google.cloud`
- **Artifact ID**: `google-cloud-bigquery`
- **Version**: `2.38.2` (in your project)
- **Latest Version**: 2.40.x+ (as of late 2024)

### Official Links
- **GitHub**: https://github.com/googleapis/java-bigquery
- **Maven Central**: https://mvnrepository.com/artifact/com.google.cloud/google-cloud-bigquery
- **JavaDoc**: https://cloud.google.com/java/docs/reference/google-cloud-bigquery/latest/overview
- **Documentation**: https://cloud.google.com/bigquery/docs/reference/libraries#client-libraries-install-java

## What This Library Does

This is an **idiomatic Java client library** that:

1. **Wraps BigQuery REST API v2**
   - Makes HTTP/REST calls to `https://www.googleapis.com/bigquery/v2`
   - Handles all low-level HTTP operations

2. **Provides High-Level Java API**
   - Object-oriented interface (no raw HTTP)
   - Type-safe methods
   - Automatic serialization/deserialization

3. **Handles Authentication**
   - OAuth 2.0 token management
   - Automatic token refresh
   - Service account integration

4. **Manages Complexity**
   - Automatic pagination
   - Retry logic with exponential backoff
   - Error handling and translation
   - Connection pooling

## Library Architecture

```
Your Application Code
         ↓
BigQuery Interface (com.google.cloud.bigquery.BigQuery)
         ↓
BigQueryImpl (implementation)
         ↓
BigQueryRpc Interface (remote procedure call abstraction)
         ↓
HttpBigQueryRpc (HTTP implementation)
         ↓
HTTP Client (Apache HttpClient / OkHttp)
         ↓
REST HTTP Calls to BigQuery API v2
         ↓
https://www.googleapis.com/bigquery/v2
```

## Key Classes You're Using

### 1. BigQueryOptions
**Package**: `com.google.cloud.bigquery.BigQueryOptions`

**Purpose**: Configuration builder for BigQuery client

**Your Usage**:
```java
BigQuery bigQuery = BigQueryOptions.newBuilder()
    .setProjectId(projectId)           // Set GCP project
    .setCredentials(credentials)       // Set auth credentials
    .build()
    .getService();                     // Get BigQuery client instance
```

**What It Does**:
- Creates configuration for BigQuery client
- Sets up authentication
- Initializes connection to BigQuery API

### 2. BigQuery Interface
**Package**: `com.google.cloud.bigquery.BigQuery`

**Purpose**: Main interface for interacting with BigQuery

**Methods You Use**:
```java
// List all datasets in project
Page<Dataset> listDatasets(String projectId)

// List tables in a dataset
Page<Table> listTables(DatasetId datasetId)

// Get table metadata and schema
Table getTable(TableId tableId)
```

### 3. GoogleCredentials
**Package**: `com.google.auth.oauth2.GoogleCredentials`

**Purpose**: OAuth 2.0 authentication

**Your Usage**:
```java
GoogleCredentials credentials = GoogleCredentials.fromStream(
    new FileInputStream(serviceAccountKeyPath)
);
```

**What It Does**:
- Loads service account key from JSON file
- Manages OAuth 2.0 access tokens
- Automatically refreshes expired tokens

### 4. Supporting Classes

**DatasetId**: Identifies a dataset
```java
DatasetId.of(projectId, datasetId)
```

**TableId**: Identifies a table
```java
TableId.of(projectId, datasetId, tableId)
```

**Page<T>**: Paginated results
```java
Page<Table> tablePage = bigQuery.listTables(dataset);
for (Table table : tablePage.iterateAll()) { ... }
```

## Dependency Tree

The `google-cloud-bigquery` library pulls in many dependencies:

```
google-cloud-bigquery (2.38.2)
├── google-cloud-core (2.57.0)
│   ├── google-auth-library-oauth2-http (1.38.0)
│   │   └── google-auth-library-credentials (1.36.0)
│   └── google-http-client (1.47.0)
├── google-api-services-bigquery (v2-rev20240919-2.0.0)
│   └── google-api-client (2.7.2)
├── gax (2.67.0)
│   └── gax-grpc (2.67.0)
├── protobuf-java (3.25.8)
└── guava (33.4.0-jre)
```

**Total Dependencies**: ~60+ transitive dependencies

## How It Makes REST Calls

Under the hood, here's what happens:

### Step 1: Your Code
```java
Page<Table> tables = bigQuery.listTables(datasetId);
```

### Step 2: BigQuery Client
The client library method translates this to:

### Step 3: HTTP Request Builder
```java
// Pseudo-code of what the library does internally
HttpRequest request = new HttpRequest.Builder()
    .setUrl("https://www.googleapis.com/bigquery/v2/projects/" + 
            projectId + "/datasets/" + datasetId + "/tables")
    .setMethod("GET")
    .setHeader("Authorization", "Bearer " + getAccessToken())
    .setHeader("Content-Type", "application/json")
    .build();
```

### Step 4: Execute HTTP Request
```java
HttpResponse response = httpClient.execute(request);
```

### Step 5: Parse Response
```java
// Parse JSON response into Java objects
TableList tableList = parseJson(response.getBody(), TableList.class);
List<Table> tables = tableList.getTables();
```

### Step 6: Return to Your Code
```java
// You get nice Java objects, not raw JSON
for (Table table : tables) {
    String tableId = table.getTableId().getTable();
}
```

## Version Information

### Your Current Version: 2.38.2
Released: ~September 2024

**Features**:
- Full BigQuery API v2 support
- Automatic retry and backoff
- Comprehensive error handling
- Support for all BigQuery table types

### Checking for Updates

To see if there's a newer version:
```xml
<!-- Check Maven Central -->
https://mvnrepository.com/artifact/com.google.cloud/google-cloud-bigquery

<!-- Or use Maven versions plugin -->
mvn versions:display-dependency-updates
```

To update:
```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-bigquery</artifactId>
    <version>2.40.0</version>  <!-- Update version here -->
</dependency>
```

## Where You Can See This Library

### 1. In Your pom.xml
```xml
<!-- Google Cloud BigQuery -->
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-bigquery</artifactId>
    <version>2.38.2</version>
</dependency>
```

### 2. In Your Java Code
```java
package com.mercadolibre.incidenciabq.service;

import com.google.cloud.bigquery.BigQuery;           // Main interface
import com.google.cloud.bigquery.BigQueryOptions;    // Configuration
import com.google.cloud.bigquery.DatasetId;          // Dataset identifier
import com.google.cloud.bigquery.TableId;            // Table identifier
import com.google.auth.oauth2.GoogleCredentials;     // Authentication
```

### 3. In Your JAR File
After building:
```bash
jar tf target/incidencia-bq-1.0.0.jar | grep google-cloud-bigquery
```

Output:
```
BOOT-INF/lib/google-cloud-bigquery-2.38.2.jar
BOOT-INF/lib/google-cloud-core-2.57.0.jar
BOOT-INF/lib/google-api-services-bigquery-v2-rev20240919-2.0.0.jar
... (many more dependencies)
```

## Library vs JDBC Driver

You're actually using **TWO different libraries** in your project:

| Aspect | Google Cloud BigQuery Client | Simba JDBC Driver |
|--------|------------------------------|-------------------|
| **Maven Artifact** | `com.google.cloud:google-cloud-bigquery` | `googlebigquery-jdbc` (system dependency) |
| **Version** | 2.38.2 | 1.6.1 / 1.6.5 (you have both) |
| **Protocol** | REST API v2 | JDBC over HTTP |
| **Usage** | `BigQueryService` class | `BigQueryJdbcService` class |
| **Import Pattern** | `com.google.cloud.bigquery.*` | `java.sql.*` + Simba driver |
| **Access Pattern** | Java API (listDatasets, listTables) | SQL queries (SELECT from INFORMATION_SCHEMA) |
| **Dependencies** | ~60 transitive dependencies | Single JAR file |
| **Maintenance** | Google-maintained, frequent updates | Third-party, less frequent updates |

## How to Verify Library Version

### Method 1: Check pom.xml
```bash
grep -A 3 "google-cloud-bigquery" pom.xml
```

### Method 2: Maven Dependency Tree
```bash
mvn dependency:tree | grep google-cloud-bigquery
```

Output:
```
[INFO] +- com.google.cloud:google-cloud-bigquery:jar:2.38.2:compile
```

### Method 3: Check Built JAR
```bash
jar tf target/incidencia-bq-1.0.0.jar | grep google-cloud-bigquery | head -1
```

### Method 4: At Runtime (in your code)
```java
String version = BigQuery.class.getPackage().getImplementationVersion();
log.info("Using google-cloud-bigquery version: {}", version);
```

## Performance Characteristics

### Initialization
```java
BigQuery bigQuery = BigQueryOptions.newBuilder()...
```
- **First call**: ~200-500ms (loads classes, initializes HTTP client)
- **Subsequent calls**: <1ms (cached instance)

### API Calls
- **listDatasets()**: 200-500ms
- **listTables()**: 200-800ms (+ pagination overhead)
- **getTable()**: 100-300ms

### Memory Footprint
- Library classes: ~5-10 MB
- Runtime heap usage: ~20-50 MB (depending on result size)

## Alternative Libraries

Other Java libraries for BigQuery (NOT used in your project):

1. **JDBC Driver (you use this too)**
   - Artifact: Simba JDBC Driver
   - SQL-based access

2. **BigQuery Storage API Client**
   - Artifact: `google-cloud-bigquerystorage`
   - For reading large amounts of data efficiently

3. **BigQuery Data Transfer API Client**
   - Artifact: `google-cloud-bigquerydatatransfer`
   - For managing data transfers

## Summary

**You are using**:
```
Library: Google Cloud BigQuery Client for Java
Artifact: com.google.cloud:google-cloud-bigquery
Version: 2.38.2
Purpose: High-level Java client that makes REST API calls to BigQuery API v2
Source: https://github.com/googleapis/java-bigquery
```

**What it does**:
- Wraps BigQuery REST API v2 in idiomatic Java
- Handles HTTP, authentication, pagination, retries automatically
- Provides type-safe, object-oriented interface
- Makes GET requests to `https://www.googleapis.com/bigquery/v2`

**Classes you use**:
- `BigQueryOptions` - Configuration builder
- `BigQuery` - Main API interface
- `GoogleCredentials` - OAuth 2.0 authentication
- `DatasetId`, `TableId` - Resource identifiers
- `Page<T>` - Paginated results

---

**TL;DR**: You're using **`com.google.cloud:google-cloud-bigquery:2.38.2`**, which is Google's official Java client library that wraps the BigQuery REST API v2 in a high-level, easy-to-use Java interface.


