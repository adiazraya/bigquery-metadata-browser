# Testing Guide

This guide helps you test the IncidenciaBQ application locally before deployment.

## Pre-Test Checklist

- [ ] Java 17+ installed
- [ ] Maven 3.6+ installed
- [ ] Service account key file in place (`service-account-key.json`)
- [ ] Internet connection available
- [ ] Port 8080 available

## Local Testing Steps

### 1. Build and Start the Application

```bash
# Option 1: Using helper script
./setup.sh
./run.sh

# Option 2: Manual
mvn clean package
mvn spring-boot:run
```

Wait for the message: "Started IncidenciaBQApplication"

### 2. Test the API

Open a new terminal and run these tests:

#### Test Connection
```bash
curl http://localhost:8080/api/bigquery/test
```

Expected response: `Connection successful!`

#### List Datasets
```bash
curl http://localhost:8080/api/bigquery/datasets | jq
```

Expected: JSON array of datasets

#### List Tables (replace DATASET_NAME)
```bash
curl http://localhost:8080/api/bigquery/datasets/DATASET_NAME/tables | jq
```

Expected: JSON array of tables

### 3. Test the UI

1. Open browser to: http://localhost:8080
2. You should see:
   - Header with "BigQuery Browser"
   - List of datasets on the left
   - Empty tables section on the right
3. Click on any dataset:
   - Dataset should highlight
   - Tables should load in the right panel
4. Scroll through tables:
   - Should be smooth scrolling
   - Each table shows name and type

### 4. Browser Developer Tools Testing

Open browser DevTools (F12) and check:

#### Console Tab
- No JavaScript errors
- See log messages about API calls

#### Network Tab
- `/api/bigquery/datasets` returns 200 OK
- `/api/bigquery/datasets/{id}/tables` returns 200 OK
- Response times are reasonable (<5 seconds)

#### Elements Tab
- UI renders correctly
- Styles are applied
- Responsive design works at different widths

## Common Issues and Solutions

### Issue: "Connection test failed"

**Possible causes:**
1. Service account key is invalid
2. Service account lacks permissions
3. Project ID is incorrect
4. Network connectivity issues

**Solutions:**
```bash
# Verify key file exists
ls -la service-account-key.json

# Check key format (should be valid JSON)
cat service-account-key.json | jq .

# Verify project ID in key matches config
cat service-account-key.json | jq -r .project_id
```

### Issue: "Port 8080 already in use"

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process (replace PID)
kill -9 PID

# Or change port in application.properties
echo "server.port=8081" >> src/main/resources/application.properties
```

### Issue: "No datasets found"

**Possible causes:**
1. Service account doesn't have permissions
2. Project has no datasets
3. Wrong project ID

**Solutions:**
- Verify in BigQuery Console that datasets exist
- Check service account has `BigQuery Data Viewer` role
- Confirm project ID: `ehc-alberto-diazraya-35c897`

### Issue: "Maven build failed"

**Solutions:**
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn clean install -U

# Skip tests
mvn clean package -DskipTests

# Check Java version
java -version  # Should be 17+
```

### Issue: "JDBC Driver not found"

**Possible causes:**
1. SIMBA driver not in Maven repository
2. Dependency resolution failed

**Solutions:**
```bash
# Force update dependencies
mvn clean install -U

# Check if driver is in Maven cache
ls -la ~/.m2/repository/com/simba/

# Try manual installation (if automatic fails)
# Download from: https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers
```

## Performance Testing

### Response Time Benchmarks

Expected response times (local):
- Connection test: < 2 seconds
- List datasets: < 5 seconds
- List tables: < 3 seconds per dataset

### Load Testing (Optional)

Use Apache Bench or similar:

```bash
# Install Apache Bench
# macOS: brew install httpd
# Linux: sudo apt-get install apache2-utils

# Test datasets endpoint
ab -n 100 -c 10 http://localhost:8080/api/bigquery/datasets

# Test specific dataset tables
ab -n 50 -c 5 http://localhost:8080/api/bigquery/datasets/DATASET_NAME/tables
```

## Automated Testing

### Unit Tests (Future Enhancement)

```bash
mvn test
```

### Integration Tests (Future Enhancement)

```bash
mvn verify
```

## Logs and Debugging

### Application Logs

Check console output for:
- INFO: Connection successful messages
- DEBUG: Detailed query information
- ERROR: Any failures or exceptions

### Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.com.mercadolibre=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Log File (Optional)

Add to `application.properties`:
```properties
logging.file.name=logs/incidencia-bq.log
```

## Security Testing

### Check Credentials Protection

```bash
# Service account key should NOT be in git
git ls-files | grep service-account-key.json
# Should return nothing

# Check .gitignore
cat .gitignore | grep service-account-key.json
# Should show the file is ignored
```

### API Security (Future Enhancement)

- Add authentication
- Implement rate limiting
- Add CORS restrictions

## Pre-Deployment Checklist

Before deploying to Heroku:

- [ ] All local tests pass
- [ ] UI works in multiple browsers (Chrome, Firefox, Safari)
- [ ] No JavaScript console errors
- [ ] API endpoints respond correctly
- [ ] Service account key not committed to git
- [ ] Documentation is up to date
- [ ] Environment variables configured
- [ ] Procfile and system.properties present

## Test Data

For testing purposes, you can use these sample queries:

### Create Test Dataset (Optional)

```sql
-- In BigQuery Console
CREATE SCHEMA IF NOT EXISTS test_dataset;
```

### Create Test Table (Optional)

```sql
-- In BigQuery Console
CREATE TABLE test_dataset.test_table (
  id INT64,
  name STRING,
  created_at TIMESTAMP
);

INSERT INTO test_dataset.test_table VALUES
  (1, 'Test 1', CURRENT_TIMESTAMP()),
  (2, 'Test 2', CURRENT_TIMESTAMP());
```

## Continuous Testing

### Watch Mode

For development, use:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Auto-Reload (Frontend)

Since we're using static files, simply refresh the browser after changes.

## Success Criteria

✅ Application starts without errors
✅ All API endpoints return valid responses
✅ UI displays datasets and tables correctly
✅ No JavaScript errors in console
✅ Connection test passes
✅ Can browse through multiple datasets
✅ Tables load when dataset is selected
✅ Responsive design works on mobile

## Next Steps After Testing

1. Document any issues found
2. Fix critical bugs
3. Optimize performance if needed
4. Proceed with Heroku deployment
5. Test on Heroku staging environment
6. Deploy to production

## Support

If tests fail and you can't resolve:
1. Check all log files
2. Verify configuration in `application.properties`
3. Ensure service account has correct permissions
4. Review this testing guide again
5. Check README.md for additional information

Happy testing! 🧪



