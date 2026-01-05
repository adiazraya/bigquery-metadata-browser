# Heroku Deployment Guide

This guide will walk you through deploying the IncidenciaBQ application to Heroku.

## Prerequisites

1. Heroku CLI installed: https://devcenter.heroku.com/articles/heroku-cli
2. Heroku account created
3. Service account key JSON file

## Step-by-Step Deployment

### 1. Login to Heroku

```bash
heroku login
```

### 2. Create a New Heroku App

```bash
heroku create incidencia-bq-app
```

Or use your preferred app name:

```bash
heroku create your-app-name
```

### 3. Add Java Buildpack

Heroku should automatically detect this is a Java application, but you can explicitly set it:

```bash
heroku buildpacks:set heroku/java
```

### 4. Configure Service Account

You have two options for configuring the service account:

#### Option A: Using Config Vars (Recommended)

Set the entire JSON content as an environment variable:

```bash
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json)"
```

Then modify `BigQueryConfig.java` to read from this environment variable and write to a temp file.

#### Option B: Using Heroku Secrets

For better security, use the service account key as base64:

```bash
# Encode the key
cat service-account-key.json | base64 > encoded-key.txt

# Set as config var
heroku config:set SERVICE_ACCOUNT_KEY_BASE64="$(cat encoded-key.txt)"

# Clean up
rm encoded-key.txt
```

### 5. Configure Environment Variables

Set additional configuration if needed:

```bash
heroku config:set SPRING_PROFILES_ACTIVE=production
heroku config:set JAVA_OPTS="-Xmx512m"
```

### 6. Initialize Git Repository (if not already done)

```bash
git init
git add .
git commit -m "Initial commit"
```

### 7. Add Heroku Remote

If you created the app manually:

```bash
heroku git:remote -a your-app-name
```

### 8. Deploy to Heroku

```bash
git push heroku main
```

Or if you're on a different branch:

```bash
git push heroku your-branch:main
```

### 9. Scale the Application

Ensure at least one web dyno is running:

```bash
heroku ps:scale web=1
```

### 10. Open Your Application

```bash
heroku open
```

### 11. View Logs

To monitor your application:

```bash
heroku logs --tail
```

## Configuration for Production

### Update application.properties for Production

Create `src/main/resources/application-production.properties`:

```properties
# Server Configuration
server.port=${PORT:8080}

# BigQuery Configuration
bigquery.project.id=ehc-alberto-diazraya-35c897
bigquery.service.account.email=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com
bigquery.service.account.key.path=${GOOGLE_APPLICATION_CREDENTIALS:/tmp/service-account-key.json}

# Logging
logging.level.root=INFO
logging.level.com.mercadolibre=INFO
```

### Update BigQueryConfig for Heroku

Modify the configuration to handle the JSON from environment variable:

```java
@PostConstruct
public void init() throws IOException {
    String jsonContent = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
    if (jsonContent != null) {
        File tempFile = File.createTempFile("service-account", ".json");
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), jsonContent);
        this.serviceAccountKeyPath = tempFile.getAbsolutePath();
    }
}
```

## Troubleshooting

### Build Failures

Check build logs:

```bash
heroku logs --tail
```

Common issues:
- Maven dependencies not resolving
- Java version mismatch
- Memory issues during build

### Runtime Errors

1. **Connection Timeout**: Check service account permissions
2. **Authentication Failed**: Verify service account key is correctly set
3. **Out of Memory**: Increase dyno size

```bash
heroku ps:resize web=standard-2x
```

### Check Application Status

```bash
heroku ps
```

### Restart Application

```bash
heroku restart
```

### Access Heroku Bash

```bash
heroku run bash
```

## Monitoring

### View Metrics

```bash
heroku addons:create papertrail:choklad
```

### Database (if needed in future)

```bash
heroku addons:create heroku-postgresql:hobby-dev
```

## Cost Optimization

- Use free tier for testing: 550-1000 dyno hours/month
- Upgrade to Hobby ($7/month) for production
- Monitor usage: https://dashboard.heroku.com/apps/your-app-name/metrics

## Security Best Practices

1. Never commit `service-account-key.json` to git
2. Use Heroku config vars for sensitive data
3. Enable HTTPS (Heroku provides this automatically)
4. Regularly rotate service account keys
5. Use minimal IAM permissions (BigQuery Data Viewer only)

## Updating the Application

After making changes:

```bash
git add .
git commit -m "Your update message"
git push heroku main
```

## Rolling Back

If something goes wrong:

```bash
heroku releases
heroku rollback v123  # Replace with version number
```

## Custom Domain (Optional)

Add a custom domain:

```bash
heroku domains:add www.yourdomain.com
```

Follow DNS configuration instructions.

## Useful Commands

```bash
# View all config vars
heroku config

# Remove a config var
heroku config:unset VARIABLE_NAME

# Check buildpacks
heroku buildpacks

# View app info
heroku info

# Open dashboard
heroku dashboard
```

## Support

For issues:
- Heroku Status: https://status.heroku.com/
- Heroku Support: https://help.heroku.com/
- Application logs: `heroku logs --tail`






