# Fix: Heroku Deployment - System Dependency Issue

## Problem

The deployment failed because:
- The Simba JDBC driver is a `system` dependency pointing to `lib/GoogleBigQueryJDBC42-current.jar`
- System dependencies don't work on Heroku
- The `lib/` folder wasn't included in Git (and shouldn't be due to size)

## Solution

**Use the REST API method on Heroku** (which works perfectly without JDBC):

The application supports both:
- **JDBC Method** (Simba driver) - For local development
- **REST API Method** (Google Cloud library) - For production/Heroku ✅

### Quick Fix

Make the Simba JDBC dependency optional:

```xml
<dependency>
    <groupId>com.simba.googlebigquery.jdbc</groupId>
    <artifactId>googlebigquery-jdbc</artifactId>
    <version>1.6.5</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/${simba.jdbc.jar}</systemPath>
    <optional>true</optional>  <!-- Add this -->
</dependency>
```

Or use a Maven profile to exclude it on Heroku.

### Alternative: Use REST API Only on Heroku

The REST API endpoints will still work:
- `/api/metadata/datasets` ✅
- `/api/metadata/datasets/{id}/tables` ✅  
- `/api/metadata/datasets/{id}/tables/{table}/schema` ✅

The JDBC endpoints will return errors:
- `/api/jdbc/datasets` ❌ (but not needed)
- `/api/jdbc/datasets/{id}/tables` ❌ (but not needed)

## Updated Deployment Instructions

I'll create a Heroku-specific profile that excludes the JDBC dependency.


