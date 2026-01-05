# BigQuery REST API Calls - Complete Reference

## Overview

This document lists all Google BigQuery REST API calls made by the **API/REST method** (not JDBC) in the IncidenciaBQ application. These calls use the Google Cloud BigQuery Java Client Library, which internally makes REST HTTP calls to the BigQuery API v2.

## API Endpoint Base

All calls are made to the Google BigQuery API v2:
```
https://www.googleapis.com/bigquery/v2
```

---

## 1. List Datasets

**Java Method**: `BigQueryService.listDatasets()`

### API Call Details

**Endpoint**:
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets
```

**Java Client Library Call**:
```java
Page<Dataset> datasetPage = bigQueryClient.listDatasets(projectId);
```

**HTTP Method**: `GET`

**Path Parameters**:
- `projectId`: Your Google Cloud project ID (e.g., `ehc-alberto-diazraya-35c897`)

**Query Parameters** (optional):
- `maxResults`: Maximum number of results per page (handled by client library)
- `pageToken`: Token for pagination
- `all`: Whether to list all datasets or only visible ones
- `filter`: Label filter expression

**Response**: Paginated list of datasets with:
- `datasetReference`: Dataset ID and project ID
- `friendlyName`: Human-readable name
- `description`: Dataset description
- `location`: Geographic location (e.g., US, EU)
- `creationTime`: Timestamp when dataset was created
- `labels`: Custom labels
- `access`: Access control settings

**Permissions Required**:
- `bigquery.datasets.get` (included in `bigquery.metadataViewer`)

**Documentation**:
https://cloud.google.com/bigquery/docs/reference/rest/v2/datasets/list

---

## 2. List Tables in Dataset

**Java Method**: `BigQueryService.listTables(String datasetId)`

### API Call Details

**Endpoint**:
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets/{datasetId}/tables
```

**Java Client Library Call**:
```java
DatasetId dataset = DatasetId.of(projectId, datasetId);
Page<Table> tablePage = bigQueryClient.listTables(dataset);
```

**HTTP Method**: `GET`

**Path Parameters**:
- `projectId`: Google Cloud project ID
- `datasetId`: Dataset identifier

**Query Parameters** (optional):
- `maxResults`: Maximum number of results per page
- `pageToken`: Token for pagination

**Response**: Paginated list of tables with:
- `tableReference`: Project, dataset, and table IDs
- `type`: TABLE, VIEW, EXTERNAL, or MATERIALIZED_VIEW
- `creationTime`: When the table was created
- `expirationTime`: When the table will expire (if set)
- `friendlyName`: Human-readable name
- `labels`: Custom labels

**Permissions Required**:
- `bigquery.tables.list` (included in `bigquery.metadataViewer`)

**Documentation**:
https://cloud.google.com/bigquery/docs/reference/rest/v2/tables/list

**Special Notes**:
- This endpoint returns **paginated results**
- The `iterateAll()` method automatically fetches additional pages
- Each page fetch adds latency (typically 100-500ms per page)
- Large datasets (1000+ tables) will trigger multiple API calls

---

## 3. Get Table Metadata (Schema)

**Java Method**: `BigQueryService.getTableSchema(String datasetId, String tableId)`

### API Call Details

**Endpoint**:
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets/{datasetId}/tables/{tableId}
```

**Java Client Library Call**:
```java
TableId bqTableId = TableId.of(projectId, datasetId, tableId);
Table table = bigQueryClient.getTable(bqTableId);
Schema schema = table.getDefinition().getSchema();
```

**HTTP Method**: `GET`

**Path Parameters**:
- `projectId`: Google Cloud project ID
- `datasetId`: Dataset identifier
- `tableId`: Table identifier

**Query Parameters** (optional):
- `selectedFields`: Comma-separated list of fields to return
- `view`: BASIC, FULL, or STORAGE_STATS

**Response**: Complete table metadata including:
- `tableReference`: Table identification
- `schema`: Full schema definition with:
  - `fields[]`: Array of field definitions
    - `name`: Field name
    - `type`: Data type (STRING, INTEGER, FLOAT, BOOLEAN, etc.)
    - `mode`: NULLABLE, REQUIRED, or REPEATED
    - `description`: Field description
    - `fields[]`: Nested fields for RECORD types
- `type`: Table type
- `creationTime`: Creation timestamp
- `lastModifiedTime`: Last modification timestamp
- `numBytes`: Size in bytes
- `numRows`: Number of rows
- `description`: Table description
- `labels`: Custom labels
- `location`: Geographic location

**Permissions Required**:
- `bigquery.tables.get` (included in `bigquery.metadataViewer`)

**Documentation**:
https://cloud.google.com/bigquery/docs/reference/rest/v2/tables/get

---

## 4. Test Connection

**Java Method**: `BigQueryService.testConnection()`

### API Call Details

**Endpoint**: Same as List Datasets
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets
```

**Java Client Library Call**:
```java
Page<Dataset> page = bigQueryClient.listDatasets(projectId);
page.getValues().iterator().hasNext(); // Just test first result
```

**Purpose**: Verify BigQuery API connectivity and authentication

**HTTP Method**: `GET`

**Note**: This makes a minimal list datasets call just to test connectivity

---

## Authentication & Authorization

All API calls use **Service Account authentication** via OAuth 2.0:

### Authentication Flow

1. **Load Service Account Key**:
   ```java
   GoogleCredentials credentials = GoogleCredentials.fromStream(
       new FileInputStream(serviceAccountKeyPath)
   );
   ```

2. **Build Client with Credentials**:
   ```java
   BigQuery bigQuery = BigQueryOptions.newBuilder()
       .setProjectId(projectId)
       .setCredentials(credentials)
       .build()
       .getService();
   ```

3. **Client Library Handles**:
   - Automatic token acquisition from Google OAuth server
   - Token refresh when expired
   - Adding `Authorization: Bearer {token}` header to all requests

### Required Headers (Added Automatically)

All API calls include:
```http
Authorization: Bearer {access_token}
Content-Type: application/json
User-Agent: google-cloud-bigquery-java/{version}
```

---

## Pagination

APIs that return lists use **cursor-based pagination**:

### How It Works

1. **First Request**: No `pageToken` parameter
   ```
   GET /projects/{projectId}/datasets?maxResults=100
   ```

2. **Response Contains**:
   ```json
   {
     "datasets": [...],
     "nextPageToken": "CgkIAxICCAESAmQB"
   }
   ```

3. **Subsequent Requests**: Include `pageToken`
   ```
   GET /projects/{projectId}/datasets?maxResults=100&pageToken=CgkIAxICCAESAmQB
   ```

4. **Last Page**: No `nextPageToken` in response

### In the Application

The Java client library's `iterateAll()` method automatically handles pagination:

```java
Page<Table> tablePage = bigQueryClient.listTables(dataset);
for (Table table : tablePage.iterateAll()) {  // Fetches all pages automatically
    // Process each table
}
```

**Performance Impact**:
- Each page fetch is a separate API call
- Typical page size: 50-100 items
- Each additional page adds ~100-500ms latency

---

## API Call Summary

| Operation | Endpoint | Method | Typical Latency | Pagination |
|-----------|----------|--------|-----------------|------------|
| **List Datasets** | `/projects/{projectId}/datasets` | GET | 200-500ms | Yes |
| **List Tables** | `/projects/{projectId}/datasets/{datasetId}/tables` | GET | 200-800ms | Yes |
| **Get Table** | `/projects/{projectId}/datasets/{datasetId}/tables/{tableId}` | GET | 100-300ms | No |

---

## Rate Limits & Quotas

BigQuery API v2 has the following limits:

### Default Quotas

- **API requests per day**: 2,000,000
- **API requests per second**: 100
- **API requests per user per second**: 10
- **Concurrent API requests**: 1,500

### Specific Endpoint Limits

- **List datasets**: 100 requests per second
- **List tables**: 100 requests per second per dataset
- **Get table**: 100 requests per second per table

### Handling Rate Limits

The client library automatically implements:
- **Exponential backoff** on rate limit errors (429)
- **Retry logic** with jitter
- **Connection pooling** for efficiency

If you hit rate limits, you'll see:
```
HTTP 429 Too Many Requests
{
  "error": {
    "code": 429,
    "message": "Quota exceeded: too many API requests",
    "status": "RESOURCE_EXHAUSTED"
  }
}
```

---

## Error Responses

Common error codes:

| Code | Status | Meaning | Solution |
|------|--------|---------|----------|
| **400** | INVALID_ARGUMENT | Bad request format | Check parameters |
| **401** | UNAUTHENTICATED | Invalid credentials | Verify service account key |
| **403** | PERMISSION_DENIED | Insufficient permissions | Add required IAM roles |
| **404** | NOT_FOUND | Dataset/table doesn't exist | Verify resource exists |
| **429** | RESOURCE_EXHAUSTED | Rate limit exceeded | Implement backoff |
| **500** | INTERNAL | BigQuery internal error | Retry with backoff |
| **503** | UNAVAILABLE | Service temporarily unavailable | Retry with backoff |

---

## Comparison: REST API vs JDBC

| Aspect | REST API (This) | JDBC (Simba Driver) |
|--------|-----------------|---------------------|
| **Protocol** | REST/HTTP | JDBC over HTTP |
| **Authentication** | Service Account (OAuth 2.0) | Service Account via JDBC config |
| **List Datasets** | `listDatasets()` API call | SQL query to INFORMATION_SCHEMA |
| **List Tables** | `listTables()` API call | SQL query to INFORMATION_SCHEMA |
| **Get Schema** | `getTable()` API call | SQL query to INFORMATION_SCHEMA |
| **Pagination** | Automatic via client library | Handled by SQL query |
| **Performance** | Generally faster | Can be slower due to SQL overhead |
| **Permissions** | metadataViewer + jobUser | Same, but jobUser crucial for SQL |
| **Error Handling** | Direct API errors | SQL execution errors |

---

## Monitoring API Calls

### Enable Detailed Logging

The application logs all API calls with details:

```
[DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────
[DETAIL] ║   │ API Method: BigQuery.listDatasets()
[DETAIL] ║   │ Target Project: ehc-alberto-diazraya-35c897
[DETAIL] ║   │ Endpoint: BigQuery Data API v2
[DETAIL] ║   │ Operation: LIST_DATASETS
[DETAIL] ║   │ Request Type: REST API Call
[DETAIL] ║   └────────────────────────────────────────────────
```

### Check Logs

```bash
tail -f logs/incidencia-bq.log | grep "API CALL DETAILS"
```

### Google Cloud Console

Monitor API usage in real-time:
1. Go to: https://console.cloud.google.com/apis/dashboard
2. Select your project
3. View BigQuery API metrics

---

## API Reference Links

- **BigQuery REST API v2**: https://cloud.google.com/bigquery/docs/reference/rest
- **Datasets API**: https://cloud.google.com/bigquery/docs/reference/rest/v2/datasets
- **Tables API**: https://cloud.google.com/bigquery/docs/reference/rest/v2/tables
- **Java Client Library**: https://cloud.google.com/java/docs/reference/google-cloud-bigquery/latest/overview
- **Authentication**: https://cloud.google.com/docs/authentication

---

## Complete API Call Flow Example

### Example: Listing Tables in a Dataset

1. **Client Initialization** (once per application lifecycle):
   ```
   POST https://oauth2.googleapis.com/token
   → Get OAuth access token
   ```

2. **List Tables Request**:
   ```http
   GET https://www.googleapis.com/bigquery/v2/projects/ehc-alberto-diazraya-35c897/datasets/ProcessEfficiency/tables
   Authorization: Bearer {access_token}
   ```

3. **First Response** (if > 100 tables):
   ```json
   {
     "kind": "bigquery#tableList",
     "tables": [
       {
         "tableReference": {
           "projectId": "ehc-alberto-diazraya-35c897",
           "datasetId": "ProcessEfficiency",
           "tableId": "table_001"
         },
         "type": "TABLE",
         "creationTime": "1639392000000"
       },
       // ... 99 more tables
     ],
     "nextPageToken": "CgkIAxICCAESAmQB",
     "totalItems": 250
   }
   ```

4. **Pagination Requests** (automatic if using `iterateAll()`):
   ```http
   GET https://www.googleapis.com/bigquery/v2/projects/ehc-alberto-diazraya-35c897/datasets/ProcessEfficiency/tables?pageToken=CgkIAxICCAESAmQB
   Authorization: Bearer {access_token}
   ```

5. **Continue** until no `nextPageToken` in response

---

## Summary

The REST API method makes **3 types of API calls**:

1. **`GET /projects/{projectId}/datasets`** - List all datasets
2. **`GET /projects/{projectId}/datasets/{datasetId}/tables`** - List tables in a dataset
3. **`GET /projects/{projectId}/datasets/{datasetId}/tables/{tableId}`** - Get table schema

All calls:
- Use OAuth 2.0 authentication via service account
- Are made over HTTPS
- Support pagination for list operations
- Return JSON responses
- Are logged in detail in `logs/incidencia-bq.log`

For monitoring, check:
- Application logs: `tail -f logs/incidencia-bq.log`
- Google Cloud Console: APIs & Services → Dashboard



