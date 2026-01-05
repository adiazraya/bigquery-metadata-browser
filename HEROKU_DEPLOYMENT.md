# Heroku Deployment Guide

## Quick Deploy

The easiest way to deploy to Heroku:

```bash
./deploy_heroku.sh
```

This automated script will:
1. Create a new Heroku app
2. Configure buildpacks
3. Set environment variables
4. Build the application
5. Deploy to Heroku
6. Open the application in your browser

---

## Manual Deployment Steps

If you prefer to deploy manually or troubleshoot issues:

### Prerequisites

1. **Heroku CLI installed**:
   ```bash
   brew tap heroku/brew && brew install heroku
   # or download from: https://devcenter.heroku.com/articles/heroku-cli
   ```

2. **Heroku account**:
   ```bash
   heroku login
   ```

3. **Application built**:
   ```bash
   mvn clean package -DskipTests
   ```

### Step 1: Create Heroku App

```bash
# Create with auto-generated name
heroku create

# OR create with specific name
heroku create bigquery-metadata-browser
```

This will:
- Create a new Heroku application
- Add Heroku as a Git remote: `heroku`
- Output the app URL

### Step 2: Configure Buildpack

```bash
# Set Java buildpack
heroku buildpacks:set heroku/java
```

### Step 3: Set Environment Variables

#### Service Account Credentials

```bash
# Set service account JSON as environment variable
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json | jq -c .)"
```

**Important**: The service account JSON is stored as a config var and written to `/tmp/service-account-key.json` at startup by `heroku-start.sh`.

#### Spring Profile

```bash
# Use production profile
heroku config:set SPRING_PROFILES_ACTIVE=production
```

#### Java Options (Optional)

```bash
# Optimize memory usage
heroku config:set JAVA_OPTS="-Xmx512m -XX:+UseG1GC"
```

### Step 4: Deploy

```bash
# Push to Heroku
git push heroku main
```

Heroku will:
1. Receive your code
2. Detect Java application
3. Run Maven build
4. Start the application

### Step 5: Verify Deployment

```bash
# Open application in browser
heroku open

# View logs
heroku logs --tail

# Check dyno status
heroku ps
```

---

## Configuration Files

### Procfile

Tells Heroku how to start your application:

```
web: bash heroku-start.sh
```

### system.properties

Specifies Java version:

```
java.runtime.version=17
```

### heroku-start.sh

Startup script that:
- Extracts service account JSON from environment variable
- Writes it to `/tmp/service-account-key.json`
- Starts the application with production profile

### application-production.properties

Production-specific configuration:
- Uses `$PORT` environment variable (set by Heroku)
- Points to `/tmp/service-account-key.json`
- Enables compression
- Optimized logging

---

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `PORT` | Port to run on (set by Heroku) | Yes (Auto) |
| `GOOGLE_APPLICATION_CREDENTIALS_JSON` | Service account key as JSON string | Yes |
| `SPRING_PROFILES_ACTIVE` | Spring profile (set to `production`) | Yes |
| `JAVA_OPTS` | JVM options | No |

View all config vars:
```bash
heroku config
```

Set a config var:
```bash
heroku config:set KEY=value
```

Remove a config var:
```bash
heroku config:unset KEY
```

---

## Scaling

### Dyno Types

```bash
# Check current dyno type
heroku ps

# Scale to hobby dyno (free)
heroku ps:scale web=1:hobby

# Scale to standard dyno
heroku ps:scale web=1:standard-1x
```

### Auto-scaling

For production, consider enabling auto-scaling:
```bash
# Enable auto-scaling (requires Standard dyno or higher)
heroku ps:autoscale:enable web --min 1 --max 3
```

---

## Monitoring & Logs

### View Logs

```bash
# Tail logs (live)
heroku logs --tail

# Last 500 lines
heroku logs --num=500

# Filter by source
heroku logs --source app

# Save to file
heroku logs --num=1000 > heroku-logs.txt
```

### Application Metrics

```bash
# Open metrics dashboard
heroku addons:create heroku-metrics:basic
```

### Errors

```bash
# View recent errors
heroku logs --tail | grep ERROR
```

---

## Updating the Application

After making code changes:

### Quick Update

```bash
# Commit changes
git add .
git commit -m "Your update message"

# Deploy
git push heroku main
```

### Update with Dependencies

```bash
# Clean build
mvn clean package -DskipTests

# Commit including target
git add target/
git commit -m "Update with new build"

# Deploy
git push heroku main
```

### Update Environment Variables

```bash
# Update service account key
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json | jq -c .)"
```

---

## Database & Add-ons

If you want to add a database or other services:

```bash
# Add Postgres
heroku addons:create heroku-postgresql:hobby-dev

# Add Redis
heroku addons:create heroku-redis:hobby-dev

# List add-ons
heroku addons
```

---

## Custom Domain

To use your own domain:

```bash
# Add domain
heroku domains:add www.yourdomain.com

# View DNS settings
heroku domains
```

Then configure your DNS provider with the provided DNS target.

---

## SSL Certificate

Heroku provides free SSL certificates:

```bash
# Enable automatic SSL
heroku certs:auto:enable
```

Your app will be available at:
- `https://your-app.herokuapp.com` (automatic)
- `https://www.yourdomain.com` (if custom domain configured)

---

## Heroku CLI Commands Reference

### Application Management

```bash
heroku apps                      # List all apps
heroku apps:info                 # App information
heroku apps:destroy              # Delete app
heroku restart                   # Restart all dynos
```

### Configuration

```bash
heroku config                    # List all config vars
heroku config:get KEY            # Get specific var
heroku config:set KEY=VALUE      # Set var
heroku config:unset KEY          # Remove var
```

### Logs & Monitoring

```bash
heroku logs --tail               # Stream logs
heroku ps                        # List dynos
heroku ps:restart                # Restart dynos
```

### Run Commands

```bash
heroku run bash                  # Open bash shell
heroku run "mvn --version"       # Run command
```

### Database (if using Postgres)

```bash
heroku pg                        # Database info
heroku pg:psql                   # Connect to database
heroku pg:backups                # Manage backups
```

---

## Troubleshooting

### Application Won't Start

1. **Check logs**:
   ```bash
   heroku logs --tail
   ```

2. **Verify buildpack**:
   ```bash
   heroku buildpacks
   ```

3. **Check Procfile**:
   ```bash
   cat Procfile
   ```

4. **Restart dynos**:
   ```bash
   heroku restart
   ```

### "Application Error" Page

This usually means the application crashed. Check logs:

```bash
heroku logs --tail | grep -A 10 "error\|Error\|ERROR"
```

Common causes:
- Service account key not configured
- Port binding issues (must use `$PORT`)
- Missing dependencies
- Java version mismatch

### Build Failures

```bash
# Check build logs
heroku builds

# View specific build
heroku builds:info BUILD_ID

# Clear build cache
heroku plugins:install heroku-builds
heroku builds:cache:purge
```

### Service Account Issues

```bash
# Verify config var is set
heroku config:get GOOGLE_APPLICATION_CREDENTIALS_JSON

# Re-set if needed
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json | jq -c .)"

# Check logs for credential errors
heroku logs --tail | grep "credentials\|authentication\|403"
```

### Memory Issues

```bash
# Check memory usage
heroku ps -a your-app-name

# Increase dyno size
heroku ps:scale web=1:standard-1x

# Optimize Java memory
heroku config:set JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
```

---

## Cost Optimization

### Free Tier

Heroku offers a free tier with limitations:
- Sleeps after 30 minutes of inactivity
- 550-1000 free dyno hours per month
- Limited to hobby dynos

### Keep App Awake

Use a service like:
- UptimeRobot (https://uptimerobot.com)
- Kaffeine (http://kaffeine.herokuapp.com)

Or create a simple cron job:
```bash
# Ping every 25 minutes
*/25 * * * * curl https://your-app.herokuapp.com > /dev/null 2>&1
```

### Upgrade to Paid Tier

For production use:
```bash
# Hobby dyno ($7/month)
heroku ps:scale web=1:hobby

# Standard dyno ($25/month)
heroku ps:scale web=1:standard-1x
```

---

## Security Best Practices

1. **Never commit credentials**:
   - Service account key is in `.gitignore`
   - Use environment variables

2. **Use HTTPS**:
   - Enabled by default on Heroku

3. **Rotate keys regularly**:
   ```bash
   # Generate new key
   gcloud iam service-accounts keys create new-key.json --iam-account=...
   
   # Update Heroku
   heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat new-key.json | jq -c .)"
   ```

4. **Minimal permissions**:
   - Use `bigquery.metadataViewer` + `bigquery.jobUser`
   - Not `bigquery.admin`

5. **Review access**:
   ```bash
   # View who has access to your app
   heroku access
   ```

---

## Additional Resources

- **Heroku Dev Center**: https://devcenter.heroku.com/
- **Java on Heroku**: https://devcenter.heroku.com/articles/getting-started-with-java
- **Maven on Heroku**: https://devcenter.heroku.com/articles/deploying-java-applications-with-the-heroku-maven-plugin
- **Heroku CLI**: https://devcenter.heroku.com/articles/heroku-cli

---

## Quick Command Summary

```bash
# Initial Deployment
./deploy_heroku.sh

# Update After Changes
git add .
git commit -m "Update"
git push heroku main

# Monitor
heroku logs --tail
heroku ps

# Troubleshoot
heroku restart
heroku config
heroku run bash

# Scale
heroku ps:scale web=1:standard-1x
```

---

## Support

If you encounter issues:

1. Check Heroku status: https://status.heroku.com/
2. View app logs: `heroku logs --tail`
3. Check build logs: `heroku builds`
4. Review documentation: `DEPLOYMENT.md`
5. Contact Heroku support: https://help.heroku.com/

---

**Last Updated**: January 5, 2026

