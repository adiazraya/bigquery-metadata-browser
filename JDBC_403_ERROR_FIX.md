# Simba JDBC Driver - Permission Error (403) Troubleshooting Guide

## Issue
You're getting a `403 Forbidden - Access Denied` error when trying to query `INFORMATION_SCHEMA.TABLES`:

```
Access Denied: Table ehc-alberto-diazraya-35c897:ProcessEfficiency.INFORMATION_SCHEMA.TABLES: 
User does not have permission to query table
```

## Root Causes

This error occurs because different Simba JDBC driver versions may:

1. **Handle authentication differently** - Older versions might not properly pass service account credentials
2. **Use different API endpoints** - Causing permission mismatches
3. **Format INFORMATION_SCHEMA queries differently** - Leading to access issues

## Solutions

### Solution 1: Switch Simba JDBC Driver Version

Try the **other** version to see if it resolves the permission issue:

```bash
# If using current version, try older
./switch_simba.sh 1.6.1
./run.sh

# If using older version, try current
./switch_simba.sh current
./run.sh
```

### Solution 2: Verify Service Account Permissions

Check that your service account has the necessary BigQuery permissions:

```bash
# Check current service account
gcloud auth list

# Verify project access
gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
  --flatten="bindings[].members" \
  --format="table(bindings.role)" \
  --filter="bindings.members:serviceAccount:*"
```

Required roles:
- `roles/bigquery.dataViewer` (minimum)
- `roles/bigquery.metadataViewer` (for INFORMATION_SCHEMA)
- `roles/bigquery.user` (to run queries)

### Solution 3: Fix JDBC Connection URL

The connection URL might need adjustment. Check your configuration:

**Current format:**
```java
jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;
ProjectId=PROJECT_ID;
OAuthType=0;
OAuthServiceAcctEmail=EMAIL;
OAuthPvtKeyPath=PATH;
```

**Alternative format (for some versions):**
```java
jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;
ProjectId=PROJECT_ID;
OAuthType=0;
OAuthPvtKey=PATH_TO_KEY;
```

### Solution 4: Update Service Account Key

The service account key might be expired or invalid:

```bash
# Verify key is valid JSON
cat service-account-key.json | jq .

# Check key expiration (if any)
cat service-account-key.json | jq '.private_key_id'

# Re-download key if needed
gcloud iam service-accounts keys create service-account-key.json \
  --iam-account=YOUR_SERVICE_ACCOUNT@PROJECT_ID.iam.gserviceaccount.com
```

### Solution 5: Use Absolute Path for Service Account Key

Some Simba JDBC versions require absolute paths:

Edit `application.properties`:
```properties
# Change from relative path
bigquery.service.account.key.path=./service-account-key.json

# To absolute path
bigquery.service.account.key.path=/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json
```

### Solution 6: Test with Direct BigQuery API (Not JDBC)

Compare results with the API-based service to isolate the issue:

1. Open browser: http://localhost:8080
2. Use the **API Method** tab instead of JDBC
3. If API works but JDBC doesn't → It's a driver-specific issue

### Solution 7: Add Additional JDBC Connection Properties

Try adding these properties to improve authentication:

```java
// In BigQueryJdbcService.java, modify getConnection():
String jdbcUrl = String.format(
    "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;" +
    "ProjectId=%s;" +
    "OAuthType=0;" +
    "OAuthServiceAcctEmail=%s;" +
    "OAuthPvtKeyPath=%s;" +
    "Timeout=60;" +                    // Add timeout
    "LogLevel=6;" +                    // Enable detailed logging
    "UseQueryCache=0;",                // Disable query cache
    projectId,
    serviceAccountEmail,
    serviceAccountKeyPath
);
```

## Quick Diagnostic Steps

### Step 1: Check which version is active
```bash
jar tf target/incidencia-bq-1.0.0.jar | grep googlebigquery
```

### Step 2: Switch to the other version
```bash
# If using current (3.7MB), try old:
./switch_simba.sh 1.6.1

# If using old (1.5MB), try current:
./switch_simba.sh current
```

### Step 3: Test the application
```bash
./run.sh
```

### Step 4: Check logs for detailed error
```bash
tail -100 logs/incidencia-bq.log | grep -B 5 -A 10 "403\|Access Denied"
```

## Version-Specific Known Issues

### Simba JDBC 1.6.1.1002 (Older - 1.5MB)
- ✅ Generally more stable with service account auth
- ❌ May not support newer BigQuery features
- ❌ Slower query execution

### Simba JDBC Current/Latest (3.7MB)
- ✅ Better performance
- ✅ Supports latest BigQuery features
- ❌ May have stricter authentication requirements
- ❌ Different INFORMATION_SCHEMA query format

## Recommended Fix Path

1. **First, try switching driver versions** (quickest fix):
   ```bash
   ./switch_simba.sh 1.6.1
   ./run.sh
   ```

2. **If that doesn't work, check service account permissions**:
   - Verify the service account has `bigquery.metadataViewer` role
   - Ensure the key file is valid and not expired

3. **Use absolute path for service account key**:
   - Update `application.properties` with full path

4. **Enable detailed JDBC logging** to see exact API calls

5. **Compare with API method** to confirm it's JDBC-specific

## Testing Both Methods

Create a simple test to compare:

```bash
# Terminal 1: Test with old version
./switch_simba.sh 1.6.1
./run.sh
# Try accessing: http://localhost:8080/jdbc-metadata.html

# Terminal 2: Test with current version
./switch_simba.sh current
./run.sh
# Try accessing: http://localhost:8080/jdbc-metadata.html
```

## Additional Resources

- Check `SIMBA_VS_API_EXPLANATION.md` for differences between JDBC and API methods
- Check `MONITORING_GUIDE.md` for how to monitor authentication issues
- Check logs at: `logs/incidencia-bq.log`

## Need More Help?

If the issue persists:

1. Share the output of:
   ```bash
   # Version info
   jar tf target/incidencia-bq-1.0.0.jar | grep googlebigquery
   
   # Service account info (remove sensitive data)
   cat service-account-key.json | jq '{type, project_id, client_email}'
   
   # Recent logs
   tail -50 logs/incidencia-bq.log | grep -A 5 "403\|Error"
   ```

2. Try the API method first to confirm BigQuery access works
3. Check if specific datasets have restricted access

---

**Last Updated**: December 19, 2024



