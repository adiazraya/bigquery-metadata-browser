# Monitoring BigQuery Access from Google Cloud

This guide explains how to view and monitor your application's BigQuery operations from Google Cloud's perspective.

---

## 1. BigQuery Console - Query History

### View All Queries Executed

**Location:** [BigQuery Console > Query History](https://console.cloud.google.com/bigquery)

**Steps:**
1. Go to [https://console.cloud.google.com/bigquery](https://console.cloud.google.com/bigquery)
2. Select your project: `ehc-alberto-diazraya-35c897`
3. Click on **"Query history"** in the left navigation panel
4. Or click the **clock icon** (⏰) in the top navigation bar

**What You'll See:**
- All SQL queries executed against BigQuery
- **JDBC queries** will appear here (e.g., `SELECT * FROM INFORMATION_SCHEMA.TABLES`)
- Execution time, bytes processed, and cost
- User/service account that ran the query

**Filter by:**
- Date range
- User: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- Status (Success, Failed)
- Project, Dataset

**Example JDBC Query in History:**
```sql
SELECT table_name, table_type, creation_time 
FROM `ehc-alberto-diazraya-35c897.GeneralItems.INFORMATION_SCHEMA.TABLES`
WHERE table_schema = 'GeneralItems' 
  AND table_catalog = 'ehc-alberto-diazraya-35c897'
```

---

## 2. BigQuery Jobs - Detailed Job Information

### View All BigQuery Jobs (Including API Calls)

**Location:** [BigQuery Console > Jobs](https://console.cloud.google.com/bigquery)

**Steps:**
1. In BigQuery Console, click **"Jobs"** in the left navigation
2. Or go to: `https://console.cloud.google.com/bigquery?project=ehc-alberto-diazraya-35c897&page=jobs`

**What You'll See:**
- **All BigQuery jobs**, including:
  - SQL queries (JDBC)
  - Metadata API calls (listTables, listDatasets)
  - Load jobs, export jobs, etc.
- Job ID, Type, Status, Duration
- User/service account
- Bytes processed, Billed bytes

**Filter by:**
- All jobs / Your jobs / Service account jobs
- Type: Query, Load, Extract, Copy
- State: Running, Success, Error

**Click on a job to see:**
- **Execution details**: Start time, end time, duration
- **Job information**: Configuration, statistics
- **Results preview** (for queries)
- **Errors** (if any)

---

## 3. Cloud Logging - API Call Logs

### View BigQuery API Calls in Real-Time

**Location:** [Cloud Console > Logging](https://console.cloud.google.com/logs)

**Steps:**
1. Go to [https://console.cloud.google.com/logs](https://console.cloud.google.com/logs)
2. Select project: `ehc-alberto-diazraya-35c897`
3. Use the following filters:

#### Filter for ALL BigQuery API Calls:
```
resource.type="bigquery_resource"
```

#### Filter for listTables API Calls:
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.TableService.ListTables"
```

#### Filter for listDatasets API Calls:
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.DatasetService.ListDatasets"
```

#### Filter for Your Service Account:
```
resource.type="bigquery_resource"
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
```

#### Filter for Query Jobs (JDBC):
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.JobService.InsertJob"
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
```

**What You'll See in Logs:**
- **Request details**: Method, resource name, parameters
- **Response details**: Status, errors
- **Timing**: Request timestamp, latency
- **Authentication**: Who made the call
- **Pagination**: You can see individual page requests for listTables!

---

## 4. Viewing Pagination in Action (API Approach)

### See the Multiple API Calls for Large Result Sets

When you use the **REST API** approach (api-metadata.html), you can observe:

**In Cloud Logging:**
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.TableService.ListTables"
protoPayload.request.projectId="ehc-alberto-diazraya-35c897"
protoPayload.request.datasetId="GeneralItems"
```

**You'll see MULTIPLE log entries** like:
```
[Log Entry 1] ListTables - pageToken: null (First page)
[Log Entry 2] ListTables - pageToken: "Cg8IARoLc29tZV90b2tlbg" (Page 2)
[Log Entry 3] ListTables - pageToken: "Cg8IAhoLYW5vdGhlcl90b2tlbg" (Page 3)
...
[Log Entry 599] ListTables - pageToken: "..." (Last page)
```

**This confirms the 599+ API calls we detected in our logs!**

---

## 5. Comparing API vs JDBC in BigQuery

### API Approach (REST - api-metadata.html)

**In BigQuery Console:**
- **Query History**: Will be EMPTY (no SQL queries executed)
- **Jobs**: Will show `list` operations
- **Cloud Logging**: Will show MULTIPLE `ListTables` API calls

**Log Query:**
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.TableService.ListTables"
timestamp>="2025-12-17T12:00:00Z"
```

**Expected:** ~600 log entries for 30,000 tables

---

### JDBC Approach (jdbc-metadata.html)

**In BigQuery Console:**
- **Query History**: Will show SQL query:
  ```sql
  SELECT table_name, table_type, creation_time 
  FROM INFORMATION_SCHEMA.TABLES 
  WHERE table_schema = 'your_dataset'
  ```
- **Jobs**: Will show a single `query` job
- **Cloud Logging**: Will show:
  - 1x `JobService.InsertJob` (query submission)
  - 1x `JobService.Query` (query execution)

**Log Query:**
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.JobService.Query"
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
timestamp>="2025-12-17T12:00:00Z"
```

**Expected:** 1-2 log entries for 30,000 tables

---

## 6. Step-by-Step: Monitor Your Application Right Now

### Option A: Watch API Calls Live (Cloud Logging)

**Terminal Command to Stream Logs:**
```bash
gcloud logging read \
  'resource.type="bigquery_resource" AND 
   protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"' \
  --project=ehc-alberto-diazraya-35c897 \
  --format=json \
  --limit=100 \
  --freshness=5m
```

**Or use the Console:**
1. Open [Cloud Logging](https://console.cloud.google.com/logs?project=ehc-alberto-diazraya-35c897)
2. Click **"Logs Explorer"**
3. Paste this filter:
   ```
   resource.type="bigquery_resource"
   protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
   ```
4. Click **"Run query"**
5. Enable **"Stream logs"** (toggle at the top)
6. Now run your application and watch logs appear in real-time!

---

### Option B: Compare API vs JDBC in Query History

**Test Scenario:**

1. **First**, open a dataset using **API** (http://localhost:8080/api-metadata.html)
2. In BigQuery Console > Query History, note that **NO queries appear**
3. In BigQuery Console > Jobs, you'll see `list` operations

4. **Then**, open the same dataset using **JDBC** (http://localhost:8080/jdbc-metadata.html)
5. In BigQuery Console > Query History, you'll see the **SQL query**:
   ```sql
   SELECT table_name, table_type, creation_time 
   FROM `ehc-alberto-diazraya-35c897.dataset_name.INFORMATION_SCHEMA.TABLES` 
   WHERE table_schema = 'dataset_name' 
     AND table_catalog = 'ehc-alberto-diazraya-35c897'
   ```
6. Click on the query to see:
   - **Duration**: ~1-2 seconds
   - **Bytes processed**: Minimal (INFORMATION_SCHEMA is free)
   - **Job ID**: You can trace this back to your application logs

---

## 7. Understanding Costs and Quotas

### BigQuery API Quotas

**List API Calls (REST - api-metadata.html):**
- **Quota**: 100 requests per 100 seconds per user
- **Your usage**: 600 requests for 30,000 tables
- **Potential issue**: If running frequently, you might hit quota limits!

**Check your quota usage:**
```bash
gcloud monitoring metrics list \
  --filter='metric.type="bigquery.googleapis.com/quota/list_requests/usage"' \
  --project=ehc-alberto-diazraya-35c897
```

**Or in Console:**
[IAM & Admin > Quotas](https://console.cloud.google.com/iam-admin/quotas?project=ehc-alberto-diazraya-35c897)
- Search for "BigQuery API"
- Look for "List requests per 100 seconds"

---

### BigQuery Query Costs

**JDBC Approach:**
- Queries on `INFORMATION_SCHEMA` are **FREE** (no bytes scanned)
- No cost for listing tables via SQL

**Verify in Query History:**
- Click on your query
- Look at **"Bytes processed"**: Should be 0 or minimal
- Look at **"Billed bytes"**: Should be 0

---

## 8. Advanced Monitoring: Set Up Alerts

### Create an Alert for High API Usage

**In Cloud Console:**
1. Go to [Monitoring > Alerting](https://console.cloud.google.com/monitoring/alerting)
2. Click **"Create Policy"**
3. Add condition:
   - **Resource type**: BigQuery API
   - **Metric**: Request count or API quota usage
   - **Filter**: `credential_id="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"`
   - **Threshold**: Alert if > 1000 requests in 10 minutes

4. Add notification channel (email, Slack, etc.)
5. Save policy

**This will alert you if:**
- Your application is making too many API calls
- You're approaching quota limits
- There's unexpected BigQuery usage

---

## 9. Practical Exercise: See the Difference Now

### Step-by-Step to Observe Both Approaches

**Setup:**
1. Open two browser tabs:
   - Tab 1: [BigQuery Console - Query History](https://console.cloud.google.com/bigquery?project=ehc-alberto-diazraya-35c897&page=queryhistory)
   - Tab 2: [Cloud Logging](https://console.cloud.google.com/logs?project=ehc-alberto-diazraya-35c897)

2. In Cloud Logging tab, apply this filter and enable streaming:
   ```
   resource.type="bigquery_resource"
   protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
   timestamp>="2025-12-17T00:00:00Z"
   ```

**Test API Approach:**
3. Open http://localhost:8080/api-metadata.html
4. Click on a dataset with 30,000 tables
5. Watch in Cloud Logging: You'll see **600 ListTables entries** appearing
6. Check Query History: **NO new queries** appear

**Test JDBC Approach:**
7. Open http://localhost:8080/jdbc-metadata.html
8. Click on the same dataset
9. Watch in Cloud Logging: You'll see **1-2 Query entries** appear
10. Check Query History: You'll see **the SQL query** with execution details

**Compare:**
- API: 600 log entries, no queries, ~3 minutes
- JDBC: 2 log entries, 1 query visible, ~1-2 seconds

---

## 10. Export and Analyze Logs

### Download BigQuery Access Logs for Analysis

**Using gcloud:**
```bash
# Export last hour of BigQuery API calls to JSON
gcloud logging read \
  'resource.type="bigquery_resource" AND 
   protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"' \
  --project=ehc-alberto-diazraya-35c897 \
  --limit=10000 \
  --format=json \
  --freshness=1h > bigquery_access_logs.json

# Count ListTables calls
cat bigquery_access_logs.json | \
  jq '[.[] | select(.protoPayload.methodName == "google.cloud.bigquery.v2.TableService.ListTables")] | length'

# Calculate average latency
cat bigquery_access_logs.json | \
  jq '[.[] | select(.protoPayload.methodName == "google.cloud.bigquery.v2.TableService.ListTables") | 
      .protoPayload.metadata.requestMetadata.requestLatency | tonumber] | 
      add / length'
```

**In Console - Export to BigQuery:**
1. Go to Cloud Logging
2. Click **"Log Router"** in left nav
3. Create sink:
   - **Sink name**: `bigquery-api-monitoring`
   - **Sink service**: BigQuery dataset
   - **Filter**: `resource.type="bigquery_resource"`
4. Now all logs are automatically exported to BigQuery for analysis!

---

## 11. Summary: What to Monitor

### For Production Monitoring:

| Metric | Where to Check | Why It Matters |
|--------|----------------|----------------|
| **API call count** | Cloud Logging | Detect quota issues |
| **Query count** | Query History | Monitor JDBC usage |
| **Execution time** | Query History / Jobs | Performance tracking |
| **Error rate** | Cloud Logging / Jobs | Reliability monitoring |
| **Bytes scanned** | Query History | Cost monitoring |
| **Service account usage** | Cloud Logging | Security auditing |

### Key Dashboards to Bookmark:

1. **BigQuery Console**: https://console.cloud.google.com/bigquery?project=ehc-alberto-diazraya-35c897
2. **Query History**: https://console.cloud.google.com/bigquery?project=ehc-alberto-diazraya-35c897&page=queryhistory
3. **Jobs**: https://console.cloud.google.com/bigquery?project=ehc-alberto-diazraya-35c897&page=jobs
4. **Cloud Logging**: https://console.cloud.google.com/logs?project=ehc-alberto-diazraya-35c897
5. **Quotas**: https://console.cloud.google.com/iam-admin/quotas?project=ehc-alberto-diazraya-35c897

---

## 12. Quick Reference: Common Log Queries

### See all operations from your app (last hour):
```
resource.type="bigquery_resource"
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
timestamp>="2025-12-17T12:00:00Z"
```

### Count pagination calls:
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.TableService.ListTables"
protoPayload.request.pageToken!=""
```

### See JDBC queries only:
```
resource.type="bigquery_resource"
protoPayload.methodName="google.cloud.bigquery.v2.JobService.Query"
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
```

### See errors only:
```
resource.type="bigquery_resource"
severity>=ERROR
protoPayload.authenticationInfo.principalEmail="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
```

---

## Next Steps

1. ✅ Open BigQuery Console and explore Query History
2. ✅ Open Cloud Logging and apply filters for your service account
3. ✅ Run both API and JDBC versions of your app
4. ✅ Compare what you see in BigQuery Console
5. ✅ Set up monitoring alerts for production use

This will give you complete visibility into how your application interacts with BigQuery! 🎯






