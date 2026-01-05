# Heroku Deployment - Manual Steps Required

## Summary

Due to the Simba JDBC driver being a system dependency, automated Heroku deployment requires some adjustments. The easiest solution is to:

**Use the REST API method on Heroku (no JDBC needed)**

## Your Heroku App

✅ **Created**: `calm-garden-18355`
📍 **URL**: https://calm-garden-18355.herokuapp.com
🔗 **Git Remote**: heroku (already added)

## Quick Fix - Deploy Without JDBC

### Option 1: Comment Out JDBC Dependency (Simplest)

1. **Temporarily comment out JDBC in pom.xml**:
   ```bash
   # Edit pom.xml and comment out lines 62-68
   <!--
   <dependency>
       <groupId>com.simba.googlebigquery.jdbc</groupId>
       <artifactId>googlebigquery-jdbc</artifactId>
       ...
   </dependency>
   -->
   ```

2. **Commit and push**:
   ```bash
   git add pom.xml
   git commit -m "Disable JDBC for Heroku deployment"
   git push heroku main
   ```

3. **The REST API endpoints will work perfectly**:
   - `/api/metadata/datasets`
   - `/api/metadata/datasets/{id}/tables`
   - `/api/metadata/datasets/{id}/tables/{table}/schema`

### Option 2: Include JDBC JARs in Git (Not Recommended)

```bash
# Remove lib/ from .gitignore
# Add lib files
git add -f lib/GoogleBigQueryJDBC42-current.jar
git commit -m "Add JDBC driver for Heroku"
git push heroku main
```

**Why not recommended**: Large binary files (3.7MB) in Git

### Option 3: Use Maven Profile (Already Configured)

The pom.xml now has profiles, but Heroku's auto-detection might not activate them correctly.

## Current Heroku Configuration

```bash
# Check your app
heroku apps:info -a calm-garden-18355

# View config
heroku config -a calm-garden-18355
```

## Manual Deployment Commands

```bash
cd /Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ

# 1. Comment out JDBC dependency in pom.xml (lines 62-68)

# 2. Commit
git add pom.xml
git commit -m "Remove JDBC for Heroku"

# 3. Deploy
git push heroku main

# 4. Set environment variables (if not already done)
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json | jq -c .)"
heroku config:set SPRING_PROFILES_ACTIVE=production

# 5. Open app
heroku open -a calm-garden-18355

# 6. View logs
heroku logs --tail -a calm-garden-18355
```

## What Works on Heroku

✅ **REST API Method** (Google Cloud BigQuery library)
   - All `/api/metadata/*` endpoints
   - No JDBC driver needed
   - Fast and reliable

❌ **JDBC Method** (Simba driver)
   - Requires system dependency
   - Not compatible with Heroku's build system
   - Can be disabled without affecting REST API

## Recommended Approach

**For Production (Heroku)**: Use REST API only
- Edit `src/main/resources/static/index.html`
- Make "API Metadata Browser" the default tab
- Users won't notice JDBC is unavailable

**For Development (Local)**: Both methods available
- Keep JDBC for testing
- Use either method

## After Successful Deployment

Your app will be at: **https://calm-garden-18355.herokuapp.com**

Test with:
```bash
curl https://calm-garden-18355.herokuapp.com/api/metadata/datasets
```

## Clean Up Old Deployment

If you want to start fresh:

```bash
# Delete current app
heroku apps:destroy calm-garden-18355 --confirm calm-garden-18355

# Create new app
heroku create bigquery-browser

# Deploy
git push heroku main
```

## Alternative: Deploy to Google Cloud Run

Google Cloud Run might be better for this app since it:
- Handles Java apps natively
- Works with service accounts seamlessly
- Supports both REST and JDBC methods
- Free tier available

Would you like instructions for Google Cloud Run deployment instead?

---

**Next Steps**:
1. Choose Option 1 (comment out JDBC)
2. Run the manual deployment commands above
3. Test the REST API endpoints
4. Enjoy your deployed app!


