# 🔐 Service Account Management Guide

## Overview

This application provides a comprehensive API to manage BigQuery service account credentials, including viewing current credentials, uploading new ones, and understanding permission requirements.

## 🔍 Current Service Account

### View Current Service Account Information

**Endpoint**: `GET /api/service-account/info`

**Description**: Get information about the currently active service account without exposing sensitive data.

**Example Request**:
```bash
curl http://localhost:8080/api/service-account/info
```

**Example Response**:
```json
{
  "status": "active",
  "serviceAccountEmail": "your-service-account@your-project.iam.gserviceaccount.com",
  "projectId": "your-project-id",
  "keyPath": "./service-account-key.json",
  "keyExists": true,
  "requiredPermissions": {
    "minimum": [
      {
        "role": "roles/bigquery.metadataViewer",
        "description": "View metadata for all datasets and tables",
        "purpose": "Required to list datasets, tables, and schemas",
        "permissions": "bigquery.datasets.get, bigquery.tables.get, bigquery.tables.list"
      },
      {
        "role": "roles/bigquery.jobUser",
        "description": "Run BigQuery jobs (queries)",
        "purpose": "Required for JDBC driver to execute SQL queries",
        "permissions": "bigquery.jobs.create"
      }
    ],
    "optional": [...],
    "notRecommended": [...]
  },
  "permissionGuide": {
    "summary": "Your service account needs these permissions to work with this application",
    "minimumRoles": [
      "roles/bigquery.metadataViewer - View datasets, tables, and schemas",
      "roles/bigquery.jobUser - Run queries via JDBC driver"
    ],
    "howToAssign": [...],
    "verification": [...]
  }
}
```

## 📤 Upload New Service Account

### Method 1: Upload JSON File

**Endpoint**: `POST /api/service-account/upload`

**Description**: Upload a new service account key file (JSON format).

**Content-Type**: `multipart/form-data`

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/service-account/upload \
  -F "file=@/path/to/new-service-account-key.json"
```

**Example Response**:
```json
{
  "status": "uploaded",
  "message": "Service account key uploaded successfully",
  "serviceAccountEmail": "new-service-account@project.iam.gserviceaccount.com",
  "projectId": "your-project-id",
  "savedTo": "./service-account-key-new.json",
  "nextSteps": [
    "Verify the service account has the required BigQuery permissions",
    "Backup your current service-account-key.json file",
    "Rename service-account-key-new.json to service-account-key.json",
    "Restart the application or update GOOGLE_APPLICATION_CREDENTIALS"
  ],
  "requiredPermissions": { ... }
}
```

### Method 2: Update from JSON String

**Endpoint**: `POST /api/service-account/update`

**Description**: Update service account key from raw JSON content.

**Content-Type**: `application/json`

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/service-account/update \
  -H "Content-Type: application/json" \
  -d '{
    "serviceAccountJson": "{\"type\":\"service_account\",\"project_id\":\"your-project\",...}"
  }'
```

Or with file content:
```bash
curl -X POST http://localhost:8080/api/service-account/update \
  -H "Content-Type: application/json" \
  -d @- <<EOF
{
  "serviceAccountJson": $(cat new-service-account-key.json | jq -c .)
}
EOF
```

**Example Response**:
```json
{
  "status": "uploaded",
  "message": "Service account key updated successfully",
  "serviceAccountEmail": "new-service-account@project.iam.gserviceaccount.com",
  "projectId": "your-project-id",
  "savedTo": "./service-account-key-new.json",
  "nextSteps": [...],
  "requiredPermissions": { ... }
}
```

## 🔐 Required Permissions

### View Permission Requirements

**Endpoint**: `GET /api/service-account/permissions`

**Description**: Get detailed information about required BigQuery permissions and how to assign them.

**Example Request**:
```bash
curl http://localhost:8080/api/service-account/permissions
```

**Example Response**:
```json
{
  "requiredPermissions": {
    "minimum": [
      {
        "role": "roles/bigquery.metadataViewer",
        "description": "View metadata for all datasets and tables",
        "purpose": "Required to list datasets, tables, and schemas",
        "permissions": "bigquery.datasets.get, bigquery.tables.get, bigquery.tables.list"
      },
      {
        "role": "roles/bigquery.jobUser",
        "description": "Run BigQuery jobs (queries)",
        "purpose": "Required for JDBC driver to execute SQL queries",
        "permissions": "bigquery.jobs.create"
      }
    ],
    "optional": [
      {
        "role": "roles/bigquery.dataViewer",
        "description": "Read table data and metadata",
        "purpose": "Optional: If you need to query actual table data",
        "permissions": "bigquery.tables.getData"
      }
    ],
    "notRecommended": [
      {
        "role": "roles/bigquery.admin",
        "description": "Full BigQuery access",
        "purpose": "⚠️ NOT RECOMMENDED - Too permissive for metadata browsing",
        "permissions": "All BigQuery permissions"
      }
    ]
  },
  "permissionGuide": {
    "summary": "Your service account needs these permissions to work with this application",
    "minimumRoles": [
      "roles/bigquery.metadataViewer - View datasets, tables, and schemas",
      "roles/bigquery.jobUser - Run queries via JDBC driver"
    ],
    "howToAssign": [
      "Option 1: Use Google Cloud Console (IAM & Admin > Service Accounts)",
      "Option 2: Use gcloud CLI commands (see gcloudCommands)"
    ],
    "verification": [
      "Test with: GET /api/metadata/datasets (REST API method)",
      "Test with: GET /api/jdbc/datasets (JDBC method)"
    ]
  },
  "gcloudCommands": {
    "description": "Use these commands to assign the required permissions",
    "step1": {
      "description": "Set your service account email and project ID",
      "command": "export SERVICE_ACCOUNT_EMAIL=\"your-service-account@your-project.iam.gserviceaccount.com\"\nexport PROJECT_ID=\"your-project-id\""
    },
    "step2": {
      "description": "Assign BigQuery Metadata Viewer role",
      "command": "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.metadataViewer\""
    },
    "step3": {
      "description": "Assign BigQuery Job User role",
      "command": "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.jobUser\""
    },
    "step4": {
      "description": "Verify permissions",
      "command": "gcloud projects get-iam-policy $PROJECT_ID \\\n  --flatten=\"bindings[].members\" \\\n  --filter=\"bindings.members:serviceAccount:$SERVICE_ACCOUNT_EMAIL\""
    },
    "optional": {
      "description": "If you need to read actual table data",
      "command": "gcloud projects add-iam-policy-binding $PROJECT_ID \\\n  --member=\"serviceAccount:$SERVICE_ACCOUNT_EMAIL\" \\\n  --role=\"roles/bigquery.dataViewer\""
    }
  }
}
```

## 📋 Permission Details

### Minimum Required Roles

#### 1. **BigQuery Metadata Viewer** (`roles/bigquery.metadataViewer`)
- **Purpose**: View metadata for all datasets and tables
- **Required For**:
  - Listing datasets
  - Listing tables in datasets
  - Viewing table schemas
  - Accessing INFORMATION_SCHEMA
- **Permissions**:
  - `bigquery.datasets.get`
  - `bigquery.tables.get`
  - `bigquery.tables.list`

#### 2. **BigQuery Job User** (`roles/bigquery.jobUser`)
- **Purpose**: Run BigQuery jobs (queries)
- **Required For**:
  - JDBC driver SQL query execution
  - INFORMATION_SCHEMA queries
  - Any BigQuery job creation
- **Permissions**:
  - `bigquery.jobs.create`

### Optional Roles

#### 3. **BigQuery Data Viewer** (`roles/bigquery.dataViewer`)
- **Purpose**: Read actual table data (not just metadata)
- **When Needed**: If you plan to query table contents
- **Permissions**:
  - `bigquery.tables.getData`
  - All permissions from Metadata Viewer

### ⚠️ Not Recommended

#### **BigQuery Admin** (`roles/bigquery.admin`)
- **Why Not**: Too permissive - includes permissions to delete, modify, and manage billing
- **Alternative**: Use the minimum required roles above

## 🛠️ How to Assign Permissions

### Option 1: Google Cloud Console (Web UI)

1. **Go to IAM & Admin**:
   - Navigate to https://console.cloud.google.com/iam-admin/iam
   - Select your project

2. **Find Your Service Account**:
   - Look for your service account email
   - Click the edit (pencil) icon

3. **Add Roles**:
   - Click "ADD ANOTHER ROLE"
   - Search for "BigQuery Metadata Viewer" and add it
   - Click "ADD ANOTHER ROLE" again
   - Search for "BigQuery Job User" and add it
   - Click "SAVE"

### Option 2: gcloud CLI Commands

```bash
# Set your variables
export SERVICE_ACCOUNT_EMAIL="your-service-account@your-project.iam.gserviceaccount.com"
export PROJECT_ID="your-project-id"

# Assign BigQuery Metadata Viewer
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.metadataViewer"

# Assign BigQuery Job User
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.jobUser"

# Verify permissions
gcloud projects get-iam-policy $PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:$SERVICE_ACCOUNT_EMAIL"
```

### Option 3: Terraform

```hcl
resource "google_project_iam_member" "bigquery_metadata_viewer" {
  project = var.project_id
  role    = "roles/bigquery.metadataViewer"
  member  = "serviceAccount:${var.service_account_email}"
}

resource "google_project_iam_member" "bigquery_job_user" {
  project = var.project_id
  role    = "roles/bigquery.jobUser"
  member  = "serviceAccount:${var.service_account_email}"
}
```

## 🔄 Workflow for Changing Service Account

### Step 1: Get Current Service Account Info
```bash
curl http://localhost:8080/api/service-account/info
```

### Step 2: Check Permission Requirements
```bash
curl http://localhost:8080/api/service-account/permissions
```

### Step 3: Create New Service Account (Google Cloud)

**Via Console**:
1. Go to IAM & Admin > Service Accounts
2. Click "CREATE SERVICE ACCOUNT"
3. Name it descriptively (e.g., "bigquery-metadata-browser")
4. Click "CREATE AND CONTINUE"
5. Add roles: `BigQuery Metadata Viewer` and `BigQuery Job User`
6. Click "DONE"
7. Click on the service account > Keys > Add Key > Create new key
8. Choose JSON format and download

**Via gcloud**:
```bash
# Create service account
gcloud iam service-accounts create bigquery-metadata-browser \
  --display-name="BigQuery Metadata Browser"

# Get the email
export SERVICE_ACCOUNT_EMAIL="bigquery-metadata-browser@$PROJECT_ID.iam.gserviceaccount.com"

# Assign roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.metadataViewer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.jobUser"

# Create and download key
gcloud iam service-accounts keys create ~/new-service-account-key.json \
  --iam-account=$SERVICE_ACCOUNT_EMAIL
```

### Step 4: Upload New Service Account

**Option A: Upload file**:
```bash
curl -X POST http://localhost:8080/api/service-account/upload \
  -F "file=@/path/to/new-service-account-key.json"
```

**Option B: Update from JSON**:
```bash
curl -X POST http://localhost:8080/api/service-account/update \
  -H "Content-Type: application/json" \
  -d "{\"serviceAccountJson\": $(cat new-service-account-key.json | jq -c .)}"
```

### Step 5: Activate New Service Account

```bash
# Backup current key
mv service-account-key.json service-account-key-backup.json

# Activate new key
mv service-account-key-new.json service-account-key.json

# Restart application
# Local: Ctrl+C and restart
# Heroku: heroku restart -a your-app-name
```

### Step 6: Verify It Works

```bash
# Test REST API method
curl http://localhost:8080/api/metadata/datasets

# Test JDBC method
curl http://localhost:8080/api/jdbc/datasets

# Check service account info
curl http://localhost:8080/api/service-account/info
```

## ⚠️ Security Best Practices

1. **Never Commit Service Account Keys to Git**
   - Keep `*.json` in `.gitignore`
   - Use environment variables for production

2. **Rotate Keys Regularly**
   - Create new keys every 90 days
   - Delete old keys after rotation

3. **Use Minimal Permissions**
   - Only assign necessary roles
   - Avoid `roles/bigquery.admin` unless absolutely needed

4. **Monitor Access**
   - Review Cloud Audit Logs regularly
   - Set up alerts for suspicious activity

5. **Separate Service Accounts for Different Environments**
   - Use different service accounts for dev/staging/production
   - Limit production service account to production resources

## 🧪 Testing Permissions

After assigning permissions, test with these endpoints:

```bash
# Test metadata viewing (needs metadataViewer)
curl http://localhost:8080/api/metadata/datasets

# Test JDBC query execution (needs jobUser)
curl http://localhost:8080/api/jdbc/datasets

# Test schema retrieval (needs both)
curl http://localhost:8080/api/jdbc/datasets/YOUR_DATASET/tables/YOUR_TABLE/schema
```

**Expected Results**:
- ✅ **200 OK**: Permissions are correct
- ❌ **403 Forbidden**: Missing permissions - review and assign required roles
- ❌ **401 Unauthorized**: Service account key is invalid or expired

## 📚 Additional Resources

- [BigQuery IAM Roles](https://cloud.google.com/bigquery/docs/access-control)
- [Service Account Best Practices](https://cloud.google.com/iam/docs/best-practices-service-accounts)
- [BigQuery Permissions Reference](https://cloud.google.com/bigquery/docs/access-control#bigquery)

## 🆘 Troubleshooting

### Error: "403 Forbidden - User does not have permission"
**Solution**: Assign `roles/bigquery.metadataViewer` and `roles/bigquery.jobUser` to your service account

### Error: "Invalid service account key"
**Solution**: Ensure your JSON file is valid and contains all required fields (type, project_id, private_key, etc.)

### Error: "Service account not found"
**Solution**: Verify the service account exists in your GCP project and the JSON file is for the correct project

### New service account uploaded but not working
**Solution**: Remember to:
1. Backup old key
2. Rename `service-account-key-new.json` to `service-account-key.json`
3. Restart the application

