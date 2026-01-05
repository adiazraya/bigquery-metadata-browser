# Service Account Key Location and Replacement Guide

## 📍 Current Location

Your service account key is located at:

```
/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json
```

**Current key info:**
- File size: 2.3 KB
- Last modified: November 20, 2024
- Service Account: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- Project: `ehc-alberto-diazraya-35c897`

## 🔧 How It's Configured

The application reads the service account key from the path specified in:

**File**: `src/main/resources/application.properties`

**Line 7**:
```properties
bigquery.service.account.key.path=${GOOGLE_APPLICATION_CREDENTIALS:./service-account-key.json}
```

This means:
1. **First**, it tries the `GOOGLE_APPLICATION_CREDENTIALS` environment variable
2. **If not set**, it defaults to `./service-account-key.json` (project root)

---

## 🔄 How to Replace with a New Service Account Key

### Method 1: Replace the Existing File (Easiest)

Simply replace the file with your new service account key:

```bash
# Backup the old key (optional)
mv service-account-key.json service-account-key.json.backup

# Copy your new key
cp /path/to/your/new-key.json service-account-key.json

# Verify it's valid JSON
cat service-account-key.json | jq .
```

**Important**: The new key must be named exactly `service-account-key.json`

### Method 2: Use a Different Filename

If you want to use a different filename:

1. **Edit** `application.properties`:
   ```properties
   bigquery.service.account.key.path=./my-new-key.json
   ```

2. **Place your key** in the project root:
   ```bash
   cp /path/to/your/key.json /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/my-new-key.json
   ```

3. **Restart** the application

### Method 3: Use Environment Variable

Set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable:

```bash
# Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS="/absolute/path/to/your-key.json"

# Run the application
./run.sh
```

**Or** add it to your `run.sh`:
```bash
#!/bin/bash
export GOOGLE_APPLICATION_CREDENTIALS="/absolute/path/to/your-key.json"
mvn spring-boot:run
```

### Method 4: Use Absolute Path

Edit `application.properties` to use an absolute path:

```properties
bigquery.service.account.key.path=/Users/alberto.diazraya/.gcloud/my-bigquery-key.json
```

This is useful if you want to keep the key in a central location.

---

## 📥 How to Download a New Service Account Key

### Using Google Cloud Console

1. **Go to IAM & Service Accounts**:
   ```
   https://console.cloud.google.com/iam-admin/serviceaccounts?project=ehc-alberto-diazraya-35c897
   ```

2. **Find your service account**:
   - Look for: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
   - Or create a new one if needed

3. **Create a new key**:
   - Click on the service account
   - Go to "Keys" tab
   - Click "Add Key" → "Create new key"
   - Choose "JSON" format
   - Click "Create"

4. **The key will download automatically** (usually to your Downloads folder)

5. **Move it to your project**:
   ```bash
   mv ~/Downloads/ehc-alberto-diazraya-35c897-*.json \
      /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json
   ```

### Using gcloud CLI

```bash
# List service accounts
gcloud iam service-accounts list --project=ehc-alberto-diazraya-35c897

# Create a new key for your service account
gcloud iam service-accounts keys create service-account-key.json \
  --iam-account=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com \
  --project=ehc-alberto-diazraya-35c897

# The key will be created in the current directory
```

---

## ✅ Verify Your New Key Works

After replacing the key, test it:

### Method 1: Using the Application

```bash
# Rebuild and run
mvn clean package
./run.sh

# Access the application
open http://localhost:8080
```

If datasets load, your key is working!

### Method 2: Using Test Script

```bash
# Run the BigQuery access test
./test_bigquery_access.sh
```

### Method 3: Manual Test with gcloud

```bash
# Activate the service account
gcloud auth activate-service-account \
  --key-file=service-account-key.json

# Test BigQuery access
gcloud projects list
gcloud bigquery datasets list --project=ehc-alberto-diazraya-35c897
```

---

## 🔐 Security Best Practices

### ✅ DO:

1. **Keep the key secure**:
   ```bash
   # Set proper permissions
   chmod 600 service-account-key.json
   ```

2. **Add to .gitignore** (already done):
   ```
   service-account-key.json
   ```

3. **Use example file as template**:
   - `service-account-key.json.example` is already in the repo
   - Shows structure without sensitive data

4. **Rotate keys regularly**:
   - Create new key every 90 days
   - Delete old keys after rotation

5. **Use minimal permissions**:
   - `roles/bigquery.metadataViewer`
   - `roles/bigquery.jobUser`
   - (Not `roles/bigquery.admin`!)

### ❌ DON'T:

1. **Never commit the real key to Git**
2. **Don't share the key file**
3. **Don't use keys with excessive permissions**
4. **Don't keep expired or unused keys**

---

## 📋 Service Account Key Structure

Your JSON key file should look like this:

```json
{
  "type": "service_account",
  "project_id": "ehc-alberto-diazraya-35c897",
  "private_key_id": "abc123...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com",
  "client_id": "123456789",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

**Required fields**:
- `type`: Must be "service_account"
- `project_id`: Your GCP project
- `private_key`: The actual private key (long string)
- `client_email`: Service account email

---

## 🔧 Update Configuration for Different Environments

### Development (Local)

**Current setup** - uses file in project root:
```properties
bigquery.service.account.key.path=./service-account-key.json
```

### Production (Heroku/Cloud)

Use environment variable:
```properties
bigquery.service.account.key.path=${GOOGLE_APPLICATION_CREDENTIALS}
```

Set in Heroku:
```bash
heroku config:set GOOGLE_APPLICATION_CREDENTIALS="$(cat service-account-key.json)"
```

### Multiple Projects

Create different property files:

**application-dev.properties**:
```properties
bigquery.service.account.key.path=./keys/dev-key.json
```

**application-prod.properties**:
```properties
bigquery.service.account.key.path=./keys/prod-key.json
```

Run with specific profile:
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

---

## 🚨 Troubleshooting

### Problem: "File not found"

**Error**: 
```
java.io.FileNotFoundException: ./service-account-key.json (No such file or directory)
```

**Solution**:
1. Verify file exists:
   ```bash
   ls -la service-account-key.json
   ```

2. Check you're in the project root:
   ```bash
   pwd
   # Should be: /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ
   ```

3. Use absolute path in `application.properties`:
   ```properties
   bigquery.service.account.key.path=/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json
   ```

### Problem: "Invalid JSON"

**Error**:
```
com.google.auth.oauth2.GoogleCredentials: Error reading credentials
```

**Solution**:
1. Validate JSON:
   ```bash
   cat service-account-key.json | jq .
   ```

2. Check file isn't corrupted
3. Download a fresh copy from Google Cloud

### Problem: "Permission Denied" / 403 Error

**Error**:
```
403 Forbidden: User does not have permission
```

**Solution**:
1. Check service account has required roles:
   ```bash
   gcloud projects get-iam-policy ehc-alberto-diazraya-35c897 \
     --flatten="bindings[].members" \
     --filter="bindings.members:datacloud2sa@"
   ```

2. Add required permissions:
   ```bash
   # Run the permission downgrade script
   ./downgrade_permissions.sh
   ```

   Or manually:
   ```bash
   gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
     --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
     --role="roles/bigquery.metadataViewer"
   
   gcloud projects add-iam-policy-binding ehc-alberto-diazraya-35c897 \
     --member="serviceAccount:datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com" \
     --role="roles/bigquery.jobUser"
   ```

---

## 📝 Quick Reference Commands

```bash
# Check current key location
grep "service.account.key.path" src/main/resources/application.properties

# View current key info (without exposing sensitive data)
cat service-account-key.json | jq '{type, project_id, client_email}'

# Replace with new key
cp ~/Downloads/new-key.json service-account-key.json

# Set proper permissions
chmod 600 service-account-key.json

# Test the key
./test_bigquery_access.sh

# Run with different key (environment variable)
GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json ./run.sh
```

---

## Summary

**Current Location**: `/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/service-account-key.json`

**To Replace**:
1. Download new key from Google Cloud Console
2. Replace the file: `mv ~/Downloads/new-key.json service-account-key.json`
3. Set permissions: `chmod 600 service-account-key.json`
4. Test: `./run.sh`

**Configuration File**: `src/main/resources/application.properties` (line 7)

**Environment Variable**: `GOOGLE_APPLICATION_CREDENTIALS` (optional override)

That's it! Your service account key is ready to be replaced whenever needed. 🔑

