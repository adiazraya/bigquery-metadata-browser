# 🔐 Session-Based Service Account Management

## Overview

The application now supports **per-user session credentials**! Each user can upload and use their own service account without affecting other users.

## How It Works

### Session Isolation

When you upload a service account:

1. **Unique Session ID**: A UUID is generated for your browser session
2. **Isolated Storage**: Your credentials are saved to `session-credentials/service-account-{UUID}.json`
3. **Session-Specific**: Only your session uses your credentials
4. **No Conflicts**: Multiple users can upload different service accounts simultaneously

### Example

**User A** uploads service account: `user-a-sa@project.iam.gserviceaccount.com`
- Saved to: `session-credentials/service-account-abc-123-def-456.json`
- Session ID: `abc-123-def-456`

**User B** uploads service account: `user-b-sa@project.iam.gserviceaccount.com`
- Saved to: `session-credentials/service-account-xyz-789-ghi-012.json`
- Session ID: `xyz-789-ghi-012`

Both users work independently without interfering with each other!

## API Changes

### Endpoint Signatures

All service account endpoints now use session-based storage:

```java
// Get current service account info (session-aware)
GET /api/service-account/info

// Upload new service account (session-specific)
POST /api/service-account/upload
  FormData: file

// Update service account from JSON (session-specific)
POST /api/service-account/update
  Body: { "serviceAccountJson": "..." }
```

### Response Format

When you upload a service account, you'll see:

```json
{
  "status": "uploaded",
  "message": "Service account uploaded successfully for your session",
  "serviceAccountEmail": "your-sa@project.iam.gserviceaccount.com",
  "projectId": "your-project-id",
  "sessionId": "abc-123-def-456",
  "savedTo": "./session-credentials/service-account-abc-123-def-456.json",
  "nextSteps": [
    "✅ Your service account is now active for YOUR SESSION only",
    "Other users' sessions are not affected",
    "Test your permissions using the buttons below",
    "Your credentials will persist for this browser session"
  ]
}
```

## Key Features

### 1. **Session Persistence**
- Credentials persist for the duration of your browser session
- Same credentials used across multiple tabs (same session)
- Automatic fallback to default service account if no session credentials exist

### 2. **Multi-User Support**
- Each user can upload their own service account
- No conflicts between different users
- Perfect for shared environments (demo, staging, etc.)

### 3. **Automatic Cleanup**
- Session credentials are isolated in `session-credentials/` directory
- Not committed to Git (automatically ignored)
- Can be manually cleaned up if needed

### 4. **Fallback Mechanism**
- If no session-specific credentials exist, uses default `service-account-key.json`
- Seamless experience for users who haven't uploaded custom credentials

## Usage Examples

### Web Interface

1. **Open Service Account Manager**:
   ```
   https://your-app.herokuapp.com/service-account.html
   ```

2. **Upload Your Service Account**:
   - Drag and drop your JSON file
   - Click "Upload Service Account Key"
   - See confirmation with your session ID

3. **Test Immediately**:
   - Click "Test REST API" or "Test JDBC"
   - Your session-specific credentials are used
   - Other users are unaffected

### API (cURL)

```bash
# Upload your service account
curl -X POST https://your-app.herokuapp.com/api/service-account/upload \
  -F "file=@my-service-account.json" \
  -b cookies.txt -c cookies.txt

# Check what service account your session is using
curl https://your-app.herokuapp.com/api/service-account/info \
  -b cookies.txt

# Test with your credentials
curl https://your-app.herokuapp.com/api/metadata/datasets \
  -b cookies.txt
```

**Note**: The `-b/-c cookies.txt` flags maintain session state across requests.

## Security

### Session Isolation
- Each user's credentials are completely isolated
- No way for User A to access User B's credentials
- Session IDs are UUIDs (impossible to guess)

### File Storage
- Credentials stored in `session-credentials/` directory
- Directory automatically created on startup
- Added to `.gitignore` (never committed)

### Memory Caching
- Credentials cached in memory per session
- Reduces file I/O
- Cache automatically cleared when session ends

## Technical Implementation

### SessionCredentialsManager

New service class that handles session-based credentials:

```java
@Service
public class SessionCredentialsManager {
    
    // Get or create unique ID for session
    public String getSessionCredentialsId(HttpSession session);
    
    // Save credentials for session
    public Map<String, Object> saveSessionCredentials(
        HttpSession session, String jsonContent);
    
    // Get credentials for session (with fallback)
    public GoogleCredentials getCredentials(
        HttpSession session, String defaultKeyPath);
    
    // Check if session has custom credentials
    public boolean hasSessionCredentials(HttpSession session);
    
    // Get info about session credentials
    public Map<String, Object> getSessionCredentialsInfo(
        HttpSession session, String defaultKeyPath);
    
    // Clear session credentials
    public void clearSessionCredentials(HttpSession session);
}
```

### Controller Updates

`ServiceAccountController` now passes `HttpSession` to all methods:

```java
@GetMapping("/info")
public ResponseEntity<Map<String, Object>> getServiceAccountInfo(
    HttpSession session) {
    // Uses session-specific credentials
}

@PostMapping("/upload")
public ResponseEntity<Map<String, Object>> uploadServiceAccountKey(
    @RequestParam("file") MultipartFile file,
    HttpSession session) {
    // Saves to session-specific location
}
```

## Directory Structure

```
project-root/
├── service-account-key.json           # Default credentials (fallback)
├── session-credentials/               # Per-user credentials (gitignored)
│   ├── service-account-abc-123.json  # User A's credentials
│   ├── service-account-xyz-789.json  # User B's credentials
│   └── ...                            # More users
```

## Testing Multi-User Scenarios

### Scenario 1: Two Users, Different Service Accounts

**User A** (Chrome):
```bash
# Upload service account A
curl -X POST http://localhost:8080/api/service-account/upload \
  -F "file=@service-account-a.json" \
  -b cookies-a.txt -c cookies-a.txt

# Test (uses service account A)
curl http://localhost:8080/api/metadata/datasets \
  -b cookies-a.txt
```

**User B** (Firefox):
```bash
# Upload service account B
curl -X POST http://localhost:8080/api/service-account/upload \
  -F "file=@service-account-b.json" \
  -b cookies-b.txt -c cookies-b.txt

# Test (uses service account B)
curl http://localhost:8080/api/metadata/datasets \
  -b cookies-b.txt
```

Both work independently!

### Scenario 2: User Without Custom Credentials

**User C** (Safari):
```bash
# No upload - just test
curl http://localhost:8080/api/metadata/datasets

# Uses default service-account-key.json
# Works seamlessly
```

## Migration Guide

### For Existing Users

**Nothing changes!**

- If you don't upload a custom service account, the default `service-account-key.json` is used
- Existing functionality works exactly as before
- Session-based credentials are an **optional enhancement**

### For New Features

If you want to use session-specific credentials:

1. Keep your default `service-account-key.json` as fallback
2. Users can optionally upload their own via the web UI
3. Their credentials are automatically used for their session only

## Troubleshooting

### "My credentials aren't working"

**Check your session**:
```bash
curl http://localhost:8080/api/service-account/info -b cookies.txt
```

Look for:
- `sessionId`: Your unique session ID
- `hasCustomCredentials`: true/false
- `credentialsSource`: "Session-specific" or "Default"

### "I want to switch back to default"

Clear your browser cookies or call:
```bash
# (Note: Clear endpoint can be added if needed)
# For now, clear browser cookies to reset session
```

### "Session credentials directory missing"

The directory is automatically created on app startup. If missing:
```bash
mkdir -p session-credentials
```

## Benefits

✅ **Multi-User Support**: Each user can have their own credentials
✅ **No Conflicts**: Sessions are completely isolated
✅ **Easy Testing**: Test with different service accounts without restarting
✅ **Secure**: UUID-based session IDs prevent credential access across sessions
✅ **Backward Compatible**: Existing functionality unchanged
✅ **Flexible**: Falls back to default if no session credentials exist

## Summary

With session-based credentials, your BigQuery Metadata Browser now supports:

1. **Per-user service accounts** - Each user uploads their own
2. **Session isolation** - No conflicts between users
3. **Automatic fallback** - Uses default if no custom credentials
4. **Seamless experience** - Works across multiple tabs
5. **Enterprise-ready** - Perfect for shared environments

**Upload your service account and start using your own credentials today!** 🎊

---

**Live**: https://calm-garden-18355-eb7d77ad6a7a.herokuapp.com/service-account.html

**GitHub**: https://github.com/adiazraya/bigquery-metadata-browser

