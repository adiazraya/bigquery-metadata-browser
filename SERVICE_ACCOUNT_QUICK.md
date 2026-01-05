# 🔐 Service Account Quick Reference

## 📍 Current Service Account Location

**File**: `service-account-key.json` (project root)

## 🚀 Quick Actions

### View Current Service Account
```bash
curl http://localhost:8080/api/service-account/info
```

### View Permission Requirements
```bash
curl http://localhost:8080/api/service-account/permissions | jq
```

### Upload New Service Account
```bash
curl -X POST http://localhost:8080/api/service-account/upload \
  -F "file=@new-service-account-key.json"
```

### Activate New Service Account
```bash
# Backup current
mv service-account-key.json service-account-key-backup.json

# Activate new
mv service-account-key-new.json service-account-key.json

# Restart app (local)
./run.sh

# Or restart on Heroku
heroku restart -a calm-garden-18355
```

## 🔐 Required Permissions

Your service account **MUST** have these BigQuery IAM roles:

1. **`roles/bigquery.metadataViewer`** - View datasets, tables, schemas
2. **`roles/bigquery.jobUser`** - Run queries (JDBC needs this)

## ⚡ Assign Permissions Quickly

```bash
# Set variables
export SERVICE_ACCOUNT_EMAIL="your-sa@project.iam.gserviceaccount.com"
export PROJECT_ID="your-project-id"

# Assign both required roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.metadataViewer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
  --role="roles/bigquery.jobUser"
```

## 📊 API Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/service-account/info` | GET | View current service account |
| `/api/service-account/permissions` | GET | View permission requirements |
| `/api/service-account/upload` | POST | Upload new key (file) |
| `/api/service-account/update` | POST | Update key (JSON) |

## ✅ Test It Works

```bash
# Test REST API
curl http://localhost:8080/api/metadata/datasets

# Test JDBC
curl http://localhost:8080/api/jdbc/datasets
```

**Expected**: `200 OK` with dataset list
**If `403 Forbidden`**: Missing permissions - assign the required roles above

## 📚 Full Documentation

See `SERVICE_ACCOUNT_API.md` for complete details.

---

**Quick Help**: If you see permission errors, run:
```bash
curl http://localhost:8080/api/service-account/permissions | jq '.gcloudCommands'
```

