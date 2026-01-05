# REST API Calls - Quick Reference

## Summary

The REST/HTTP API method (not JDBC) makes **3 types of API calls** to BigQuery:

### 1. List Datasets
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets
```
- Returns all datasets in project
- Automatically paginated
- ~200-500ms latency

### 2. List Tables
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets/{datasetId}/tables
```
- Returns all tables in a dataset
- Automatically paginated (each page ~100-500ms)
- ~200-800ms base latency

### 3. Get Table Schema
```
GET https://www.googleapis.com/bigquery/v2/projects/{projectId}/datasets/{datasetId}/tables/{tableId}
```
- Returns full table metadata including schema
- Not paginated
- ~100-300ms latency

## How It Works

The Google Cloud BigQuery Java Client Library wraps these REST calls:

```java
// Your code
BigQuery bigQuery = BigQueryOptions.newBuilder()
    .setProjectId(projectId)
    .setCredentials(credentials)
    .build()
    .getService();

// These methods make REST API calls under the hood:
bigQuery.listDatasets(projectId);           // → GET /datasets
bigQuery.listTables(datasetId);             // → GET /datasets/{id}/tables  
bigQuery.getTable(tableId);                 // → GET /tables/{id}
```

## Authentication

All calls use **OAuth 2.0 Service Account** authentication:
- Service account key loaded from JSON file
- Client library automatically handles token acquisition and refresh
- Each request includes `Authorization: Bearer {token}` header

## Permissions Required

```
roles/bigquery.metadataViewer  (to view metadata)
roles/bigquery.jobUser         (to run queries)
```

## Monitoring

### In Application Logs
```bash
tail -f logs/incidencia-bq.log | grep "API CALL DETAILS"
```

### In Google Cloud Console
https://console.cloud.google.com/apis/dashboard → BigQuery API

## Rate Limits

- **100 requests/second** per endpoint
- **2M requests/day** per project
- Client library automatically handles rate limiting with retries

## Complete Documentation

See `REST_API_CALLS.md` for:
- Detailed endpoint documentation
- Request/response examples
- Pagination handling
- Error codes
- Full comparison with JDBC method

---

**TL;DR**: The REST API method makes direct HTTP calls to BigQuery API v2 using the Google Cloud Java client library. It makes 3 types of GET requests: list datasets, list tables, and get table schema.



