# BigQuery Permissions - Quick Reference

## The Answer to Your Question

**For listing tables, you need TWO roles:**

1. **`roles/bigquery.metadataViewer`** - View datasets, tables, and schemas
2. **`roles/bigquery.jobUser`** - Run INFORMATION_SCHEMA queries

These replace `roles/bigquery.admin` with minimal necessary permissions.

## Quick Commands

### Option A: Use the automated script (EASIEST)
```bash
./downgrade_permissions.sh
```
This will:
- Check current permissions
- Remove bigquery.admin
- Add metadataViewer + jobUser
- Verify changes

### Option B: Manual commands
```bash
# Remove admin role
gcloud projects remove-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.admin"

# Add metadata viewer (to list tables)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.metadataViewer"

# Add job user (to run queries)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"

# Wait for propagation (2 minutes)
sleep 120

# Test
./run.sh
```

## What Each Role Does

| Role | Can List Tables? | Can Read Data? | Can Modify? |
|------|------------------|----------------|-------------|
| **metadataViewer** | ✅ Yes | ❌ No | ❌ No |
| **jobUser** | ❌ No (but needed for queries) | ❌ No | ❌ No |
| **dataViewer** | ✅ Yes | ✅ Yes | ❌ No |
| **admin** | ✅ Yes | ✅ Yes | ✅ Yes (TOO MUCH!) |

## Why Two Roles?

- **metadataViewer** = Permission to SEE the tables
- **jobUser** = Permission to RUN queries (INFORMATION_SCHEMA queries are actual BigQuery queries)

Both are needed together!

## If You Want to Read Data Too

Replace `metadataViewer` with `dataViewer`:

```bash
# Use dataViewer instead (includes metadata + data reading)
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.dataViewer"

# Still need jobUser
gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
  --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
  --role="roles/bigquery.jobUser"
```

## Verify Current Permissions

```bash
gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
  --flatten="bindings[].members" \
  --filter="bindings.members:datacloud2sa@" \
  --format="table(bindings.role)"
```

Should show:
```
ROLE
roles/bigquery.metadataViewer
roles/bigquery.jobUser
```

## After Changing Permissions

1. **Wait 2 minutes** for IAM changes to propagate
2. **Restart application**: `./run.sh`
3. **Test**: http://localhost:8080/jdbc-metadata.html

## Complete Documentation

See `BIGQUERY_PERMISSIONS_GUIDE.md` for full details including:
- Detailed role comparison
- Security best practices
- Dataset-level permissions
- Troubleshooting

---

**TL;DR**: Run `./downgrade_permissions.sh` to safely replace `bigquery.admin` with minimal permissions (`metadataViewer` + `jobUser`).



