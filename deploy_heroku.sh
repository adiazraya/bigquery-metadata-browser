#!/bin/bash

echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║                Deploy BigQuery Metadata Browser to Heroku                    ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo ""

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    echo "❌ Heroku CLI not found!"
    echo "   Install it from: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

echo "✅ Heroku CLI found"
echo ""

# Check if logged in to Heroku
if ! heroku auth:whoami &> /dev/null; then
    echo "🔐 You need to login to Heroku first"
    echo "   Running: heroku login"
    heroku login
fi

echo ""
echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 1: Create Heroku App"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

# Prompt for app name
read -p "Enter Heroku app name (or press Enter for auto-generated): " APP_NAME

if [ -z "$APP_NAME" ]; then
    echo "Creating app with auto-generated name..."
    heroku create
else
    echo "Creating app: $APP_NAME"
    heroku create $APP_NAME
fi

if [ $? -ne 0 ]; then
    echo "❌ Failed to create Heroku app"
    exit 1
fi

# Get the app name
APP_NAME=$(heroku apps:info --json | jq -r '.app.name')
echo "✅ Created Heroku app: $APP_NAME"
echo "   URL: https://$APP_NAME.herokuapp.com"
echo ""

echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 2: Configure Buildpacks"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

# Set Java buildpack
echo "Setting Java buildpack..."
heroku buildpacks:set heroku/java
echo "✅ Java buildpack configured"
echo ""

echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 3: Set Environment Variables"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

# Check if service account key exists
if [ ! -f "service-account-key.json" ]; then
    echo "⚠️  WARNING: service-account-key.json not found!"
    echo "   The application won't work without it."
    echo ""
    read -p "Do you want to continue anyway? (y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Deployment cancelled."
        exit 1
    fi
else
    echo "Setting GOOGLE_APPLICATION_CREDENTIALS from service-account-key.json..."
    
    # Read the service account key and set as config var
    SERVICE_ACCOUNT_JSON=$(cat service-account-key.json | jq -c .)
    heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$SERVICE_ACCOUNT_JSON"
    
    echo "✅ Service account credentials configured"
fi

echo ""
echo "Setting other environment variables..."

# Set production profile
heroku config:set SPRING_PROFILES_ACTIVE=production

# Set Java options
heroku config:set JAVA_OPTS="-Xmx512m -XX:+UseG1GC"

echo "✅ Environment variables configured"
echo ""

echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 4: Build Application"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

echo "Building application locally..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Application built successfully"
echo ""

echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 5: Deploy to Heroku"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

echo "Deploying to Heroku..."
git push heroku main

if [ $? -ne 0 ]; then
    echo "❌ Deployment failed!"
    echo ""
    echo "Troubleshooting tips:"
    echo "1. Make sure all changes are committed: git status"
    echo "2. Check Heroku logs: heroku logs --tail"
    echo "3. Try manual push: git push heroku main --force"
    exit 1
fi

echo "✅ Deployment successful!"
echo ""

echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 6: Verify Deployment"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

echo "Opening application..."
heroku open

echo ""
echo "Checking logs..."
heroku logs --tail --num=50

echo ""
echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║                          ✅ DEPLOYMENT COMPLETE!                              ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "Your application is now running on Heroku!"
echo ""
echo "📱 Application URL:"
echo "   https://$APP_NAME.herokuapp.com"
echo ""
echo "🔧 Useful Heroku Commands:"
echo "   heroku logs --tail              # View logs"
echo "   heroku ps                       # Check dynos"
echo "   heroku config                   # View config vars"
echo "   heroku restart                  # Restart app"
echo "   heroku run bash                 # Open shell"
echo ""
echo "📚 Documentation:"
echo "   heroku help                     # Heroku CLI help"
echo "   See DEPLOYMENT.md for more details"
echo ""

