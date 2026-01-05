# 403 ERROR FIX - Quick Summary

## The Problem
You're getting `HTTP 500` error with underlying `403 Forbidden - Access Denied` when querying `INFORMATION_SCHEMA.TABLES` via JDBC.

```
Access Denied: Table ehc-alberto-diazraya-35c897:ProcessEfficiency.INFORMATION_SCHEMA.TABLES: 
User does not have permission to query table
```

## Root Cause
Different Simba JDBC driver versions handle authentication and API calls differently. The version you're currently using may have compatibility issues with your service account authentication.

## Quick Fix (Try First!)

### Option 1: Switch Simba JDBC Version
```bash
# Run the diagnostic script
./fix_jdbc_403.sh

# Or manually switch
./switch_simba.sh 1.6.1    # Try older version
./run.sh

# If that doesn't work, try the other
./switch_simba.sh current
./run.sh
```

### Option 2: Use Absolute Path for Service Account Key

Edit `src/main/resources/application.properties`:

```properties
# Change this line:
bigquery.service.account.key.path=./service-account-key.json

# To absolute path:
bigquery.service.account.key.path=/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json
```

Then rebuild:
```bash
mvn clean package
./run.sh
```

### Option 3: Use the API Method Instead

If JDBC continues to fail:
1. Open http://localhost:8080
2. Click on **"API Metadata Browser"** tab
3. Use the API-based interface (which doesn't have this issue)

## Why This Happens

| Version | Size | Authentication | Known Issues |
|---------|------|----------------|--------------|
| **1.6.1.1002** (older) | 1.5MB | More stable | Slower, older features |
| **Current** (newer) | 3.7MB | Stricter requirements | May need absolute paths |

The **older version (1.6.1.1002)** typically works better with service account authentication for INFORMATION_SCHEMA queries.

## Step-by-Step Fix

1. **Run diagnostic:**
   ```bash
   ./fix_jdbc_403.sh
   ```

2. **Follow the prompts** - it will suggest switching to the alternate version

3. **Test the application:**
   ```bash
   ./run.sh
   ```

4. **Try accessing:** http://localhost:8080/jdbc-metadata.html

5. **Check logs if still failing:**
   ```bash
   tail -50 logs/incidencia-bq.log | grep "403\|Error"
   ```

## Verification

To verify which version you're using:
```bash
ls -lh lib/GoogleBigQueryJDBC42*.jar
jar tf target/incidencia-bq-1.0.0.jar | grep googlebigquery
```

## If Nothing Works

1. **Verify service account permissions:**
   ```bash
   gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
     --flatten="bindings[].members" \
     --filter="bindings.members:datacloud2sa@"
   ```

2. **Check if key is valid:**
   ```bash
   cat service-account-key.json | jq .
   ```

3. **Use API method instead of JDBC:**
   - The API method doesn't have these version-specific issues
   - Access it at: http://localhost:8080

## Files Created to Help

- `fix_jdbc_403.sh` - Interactive diagnostic and fix script
- `JDBC_403_ERROR_FIX.md` - Detailed troubleshooting guide
- `SIMBA_VERSION_SWITCHING.md` - How to switch versions
- `switch_simba.sh` - Easy version switching

## Quick Commands Reference

```bash
# Diagnose and fix interactively
./fix_jdbc_403.sh

# Switch to older version (usually fixes it)
./switch_simba.sh 1.6.1
./run.sh

# Switch to newer version
./switch_simba.sh current
./run.sh

# Check logs
tail -f logs/incidencia-bq.log

# Show available versions
./show_simba_info.sh
```

---

**TL;DR**: Run `./fix_jdbc_403.sh` and follow the prompts. Most likely fix: switch to the older Simba JDBC version (1.6.1.1002).



