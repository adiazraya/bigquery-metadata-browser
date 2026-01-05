# 🔐 Service Account Management - Feature Summary

## ✅ What's New

Your BigQuery Metadata Browser now includes **comprehensive service account management** with built-in permission guidance!

## 🌐 Live Access

### Production (Heroku)
**Service Account Manager UI**: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html

### Local Development
**Service Account Manager UI**: http://localhost:8080/service-account.html

## 🎯 Features

### 1. **View Current Service Account** ✅
- See which service account is currently active
- View service account email and project ID
- Check if the key file exists
- All without exposing sensitive credentials

**API Endpoint**: `GET /api/service-account/info`

**Web UI**: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html

### 2. **Upload New Service Account** ✅
- Drag and drop or click to upload
- Validates JSON format before accepting
- Saves as separate file (doesn't overwrite existing)
- Shows clear activation steps

**API Endpoints**:
- `POST /api/service-account/upload` - Upload file
- `POST /api/service-account/update` - Update from JSON string

**Web UI**: Includes beautiful upload interface with drag-and-drop

### 3. **Permission Guidance** ✅
- Clear explanation of required BigQuery IAM roles
- Shows exactly which permissions are needed and why
- Provides gcloud commands for assigning permissions
- Distinguishes between minimum, optional, and not recommended roles

**API Endpoint**: `GET /api/service-account/permissions`

**Web UI**: Shows all permission requirements with color-coded importance

### 4. **Test Your Permissions** ✅
- One-click testing of REST API and JDBC methods
- Immediate feedback on permission status
- Clear error messages if permissions are missing

**Web UI**: Built-in test buttons for both methods

## 📋 Required BigQuery Permissions

Your service account **MUST** have these two IAM roles:

### 1. **BigQuery Metadata Viewer** (`roles/bigquery.metadataViewer`)
- **Why**: To view datasets, tables, and schemas
- **What it allows**:
  - List datasets in a project
  - List tables in a dataset
  - View table schemas
  - Query INFORMATION_SCHEMA

### 2. **BigQuery Job User** (`roles/bigquery.jobUser`)
- **Why**: To run BigQuery jobs (queries)
- **What it allows**:
  - Create and run BigQuery jobs
  - Required for JDBC driver to execute SQL queries
  - Query INFORMATION_SCHEMA tables

## 🛠️ How to Use

### Via Web Interface (Easiest!)

1. **Open the Service Account Manager**:
   ```
   https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html
   ```

2. **View Current Service Account**:
   - Automatically loaded on page open
   - See email, project ID, and key path

3. **Check Permission Requirements**:
   - Scroll to "Required BigQuery Permissions" section
   - Copy the gcloud commands provided

4. **Upload New Service Account** (if needed):
   - Scroll to "Upload New Service Account Key"
   - Drag and drop your JSON file or click to browse
   - Click "Upload Service Account Key"
   - Follow the activation steps shown

5. **Test Your Permissions**:
   - Scroll to "Test Your Service Account"
   - Click "Test REST API" or "Test JDBC"
   - See immediate results

### Via API (For Automation)

**Check current service account**:
```bash
curl https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/service-account/info
```

**Get permission requirements with gcloud commands**:
```bash
curl https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/service-account/permissions | jq
```

**Upload new service account**:
```bash
curl -X POST https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/service-account/upload \
  -F "file=@new-service-account-key.json"
```

## 📝 Complete Workflow Example

### Scenario: You need to use a different service account

**Step 1: Create new service account in Google Cloud**
```bash
# Set your project
export PROJECT_ID="your-project-id"

# Create service account
gcloud iam service-accounts create bigquery-metadata-browser \
  --display-name="BigQuery Metadata Browser" \
  --project=$PROJECT_ID

# Get the email
export SERVICE_ACCOUNT_EMAIL="bigquery-metadata-browser@$PROJECT_ID.iam.gserviceaccount.com"

# Assign required permissions
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

**Step 2: Upload via Web UI**
1. Go to: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html
2. Scroll to "Upload New Service Account Key"
3. Drag and drop `~/new-service-account-key.json`
4. Click "Upload Service Account Key"
5. Note the success message and next steps

**Step 3: Activate (Local Development)**
```bash
# Backup current key
mv service-account-key.json service-account-key-backup.json

# Activate new key
mv service-account-key-new.json service-account-key.json

# Restart application
./run.sh
```

**Step 3: Activate (Heroku Production)**
```bash
# Update environment variable with new key
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat ~/new-service-account-key.json | jq -c .)" \
  -a calm-garden-18355

# Restart application
heroku restart -a calm-garden-18355
```

**Step 4: Verify**
1. Go back to: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html
2. Check "Current Service Account" section - should show new email
3. Click "Test REST API" and "Test JDBC"
4. Both should return ✅ Success

## 🎨 User Interface Highlights

### Modern, Beautiful Design
- Gradient purple background
- Clean white cards with shadows
- Color-coded permission levels:
  - 🟢 Green = Minimum required
  - 🟡 Yellow = Optional
  - 🔴 Red = Not recommended
- Drag-and-drop file upload
- Real-time validation and feedback

### Responsive
- Works on desktop, tablet, and mobile
- Adapts layout for smaller screens

### User-Friendly
- No technical jargon in error messages
- Step-by-step activation instructions
- One-click testing
- Loading indicators
- Success/error notifications

## 📚 Documentation

### Quick Reference
**File**: `SERVICE_ACCOUNT_QUICK.md`
- Quick commands and common tasks
- Essential information only
- Perfect for daily use

### Complete Guide
**File**: `SERVICE_ACCOUNT_API.md`
- Full API documentation
- All endpoints with examples
- Detailed permission explanations
- Troubleshooting guide
- Security best practices

## 🔒 Security Features

1. **Validation**: 
   - Validates JSON format before accepting
   - Ensures it's a valid service account key
   - Extracts and displays email/project for verification

2. **Safe Upload**:
   - New keys saved as `service-account-key-new.json`
   - Never overwrites existing key automatically
   - Requires manual activation step

3. **No Credential Exposure**:
   - API never returns private keys
   - Only returns non-sensitive information (email, project ID)

4. **Clear Guidance**:
   - Warns about overly permissive roles
   - Recommends minimal necessary permissions
   - Explains what each permission does

## ⚠️ Important Notes

### For Local Development
- Use the reference service account in `service-account-key.json`
- Or upload a new one via the web UI
- Remember to restart the application after changing keys

### For Heroku Production
- Service account is stored in `GOOGLE_APPLICATION_CREDENTIALS_JSON` environment variable
- Update via: `heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat key.json | jq -c .)"`
- Or use the upload API endpoint
- Always restart after updating: `heroku restart -a calm-garden-18355`

### Permission Requirements
- **Minimum**: `metadataViewer` + `jobUser` (both required!)
- **Optional**: `dataViewer` (only if you need to read actual data)
- **Avoid**: `admin` (too permissive for metadata browsing)

## 🧪 Testing

After setting up or changing service accounts, always test:

```bash
# Test REST API
curl https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/metadata/datasets

# Test JDBC
curl https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/jdbc/datasets
```

**Expected**: `200 OK` with list of datasets

**If `403 Forbidden`**: Missing permissions - assign required roles

## 🆘 Troubleshooting

### "403 Forbidden - User does not have permission"
**Solution**: Assign `roles/bigquery.metadataViewer` and `roles/bigquery.jobUser`

Use the gcloud commands from:
```bash
curl https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/api/service-account/permissions | jq '.gcloudCommands'
```

### "Invalid service account key"
**Solution**: Ensure your JSON file:
- Is a valid service account key (not API key or OAuth client)
- Contains required fields: type, project_id, private_key, client_email
- Is not corrupted or modified

### "Service account uploaded but not working"
**Solution**: Remember the activation steps:
1. Backup old key
2. Rename new key to `service-account-key.json`
3. Restart the application
4. For Heroku: Update environment variable and restart

## 📊 API Endpoints Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/service-account/info` | GET | View current service account |
| `/api/service-account/permissions` | GET | Get permission requirements |
| `/api/service-account/upload` | POST | Upload new key (file) |
| `/api/service-account/update` | POST | Update key (JSON) |
| `/service-account.html` | GET | Web interface |

## 🚀 Quick Start

**Fastest way to manage service accounts:**

1. Open: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html
2. View current service account at the top
3. See permission requirements in the middle
4. Upload new key at the bottom (if needed)
5. Test permissions with one click

**That's it!** The web interface handles everything.

## ✨ Summary

You now have a **complete service account management system** that:
- ✅ Shows current service account information
- ✅ Provides clear permission guidance with gcloud commands
- ✅ Allows easy upload of new service accounts
- ✅ Tests permissions with one click
- ✅ Works both locally and on Heroku
- ✅ Has a beautiful, user-friendly web interface
- ✅ Includes comprehensive documentation
- ✅ Follows security best practices

**Live now at**: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html

---

**GitHub Repository**: https://github.com/adiazraya/bigquery-metadata-browser

**Questions?** Check `SERVICE_ACCOUNT_API.md` for complete documentation.

