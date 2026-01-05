# BigQuery Permissions Guide - Minimal Access for Metadata Browsing

## The Problem
You gave your service account `roles/bigquery.admin` and it worked, but that's too permissive. You only need to **list datasets and tables**, not manage them.

## Recommended Permission Solution

### Option 1: Metadata Viewer (RECOMMENDED - Most Restrictive)

This is the **minimum permission** needed to browse metadata without accessing data:

```bash
# Grant Metadata Viewer role
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.metadataViewer"

# Grant Job User role (needed to run INFORMATION_SCHEMA queries)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

**What this allows:**
- ✅ List all datasets
- ✅ List all tables in datasets
- ✅ View table schemas
- ✅ Run INFORMATION_SCHEMA queries
- ❌ Read actual data from tables
- ❌ Create/modify/delete anything

### Option 2: Data Viewer (If you might want to preview data later)

If you also want to **view the actual data** (not just metadata):

```bash
# Grant Data Viewer role (includes metadata + data reading)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.dataViewer"

# Still need Job User to run queries
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

**What this allows:**
- ✅ List all datasets
- ✅ List all tables in datasets
- ✅ View table schemas
- ✅ Run INFORMATION_SCHEMA queries
- ✅ Read actual data from tables
- ❌ Create/modify/delete anything

## Role Comparison

| Role | List Datasets | List Tables | View Schema | Read Data | Modify/Delete |
|------|---------------|-------------|-------------|-----------|---------------|
| **bigquery.admin** | ✅ | ✅ | ✅ | ✅ | ✅ (TOO MUCH!) |
| **bigquery.dataViewer** | ✅ | ✅ | ✅ | ✅ | ❌ |
| **bigquery.metadataViewer** | ✅ | ✅ | ✅ | ❌ | ❌ |
| **bigquery.jobUser** | ❌ | ❌ | ❌ | ❌ | ❌ (but needed to run queries) |

## Why You Need Two Roles

### bigquery.metadataViewer or bigquery.dataViewer
Provides **access to see** the metadata (datasets, tables, schemas)

### bigquery.jobUser
Provides **permission to run queries** - Since INFORMATION_SCHEMA queries are actual BigQuery queries, you need this to execute them.

## Implementation Steps

### Step 1: Remove Admin Role
```bash
gcloud projects remove-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.admin"
```

### Step 2: Add Minimal Roles

**For metadata browsing only:**
```bash
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.metadataViewer"

gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

**OR for metadata + data reading:**
```bash
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.dataViewer"

gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

### Step 3: Verify Permissions
```bash
gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
  --flatten="bindings[].members" \
  --filter="bindings.members:datacloud2sa@" \
  --format="table(bindings.role)"
```

You should see:
```
ROLE
roles/bigquery.metadataViewer
roles/bigquery.jobUser
```

### Step 4: Test Your Application
```bash
./run.sh
```

Access http://localhost:8080/jdbc-metadata.html and verify you can still list datasets and tables.

## Specific Permissions Included

### bigquery.metadataViewer includes:
- `bigquery.datasets.get` - View dataset properties
- `bigquery.datasets.getIamPolicy` - View dataset access policies
- `bigquery.models.getMetadata` - View ML model metadata
- `bigquery.models.list` - List ML models
- `bigquery.routines.get` - View function/procedure metadata
- `bigquery.routines.list` - List functions/procedures
- `bigquery.tables.get` - View table metadata/schema
- `bigquery.tables.getData` - **NOT INCLUDED** (can't read actual data)
- `bigquery.tables.list` - List tables

### bigquery.jobUser includes:
- `bigquery.jobs.create` - Create jobs (run queries)
- Limited to jobs created by this user only

### bigquery.dataViewer includes:
- Everything in `metadataViewer` PLUS:
- `bigquery.tables.getData` - Read actual table data
- `bigquery.tables.export` - Export table data

## Alternative: Dataset-Level Permissions

If you want even more granular control (only specific datasets), you can grant permissions at the dataset level:

```bash
# For a specific dataset only
bq show --format=prettyjson PROJECT_ID:DATASET_ID | \
  jq '.access += [{"role": "READER", "userByEmail": "datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"}]' | \
  bq update --source /dev/stdin PROJECT_ID:DATASET_ID
```

But this is more complex to manage if you have many datasets.

## Troubleshooting After Permission Change

### If you get 403 errors after changing permissions:

1. **Wait a few minutes** - IAM changes can take up to 2 minutes to propagate

2. **Verify the permissions are set:**
   ```bash
   gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
     --flatten="bindings[].members" \
     --filter="bindings.members:datacloud2sa@"
   ```

3. **Restart your application:**
   ```bash
   # Stop the application (Ctrl+C)
   ./run.sh
   ```

4. **Check logs for new errors:**
   ```bash
   tail -50 logs/incidencia-bq.log
   ```

## Security Best Practices

### ✅ DO:
- Use **metadataViewer + jobUser** for metadata browsing only
- Use **dataViewer + jobUser** if you need to read data
- Apply **least privilege principle**
- Use **dataset-level permissions** for production environments
- **Rotate service account keys** regularly

### ❌ DON'T:
- Use **bigquery.admin** for read-only applications
- Grant **project-level owner** role
- Share **service account keys** in version control
- Use the **same service account** for multiple applications with different needs

## Recommended Setup for Your Application

Based on your use case (browsing datasets and tables):

```bash
# 1. Remove admin role
gcloud projects remove-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.admin"

# 2. Add minimal roles
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.metadataViewer"

gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"

# 3. Wait 2 minutes for propagation
sleep 120

# 4. Test
./run.sh
```

## Quick Command Reference

```bash
# Check current permissions
gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
  --flatten="bindings[].members" \
  --filter="bindings.members:datacloud2sa@"

# Remove admin
gcloud projects remove-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.admin"

# Add metadata viewer (recommended)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.metadataViewer"

# Add job user (required for queries)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

---

**TL;DR**: Replace `roles/bigquery.admin` with `roles/bigquery.metadataViewer` + `roles/bigquery.jobUser` for secure metadata browsing without excessive permissions.



