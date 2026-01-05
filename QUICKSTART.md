# Quick Start Guide

Get up and running with IncidenciaBQ in minutes!

## 🚀 Quick Setup (3 Steps)

### 1. Get Your Service Account Key

You need the JSON key file for the service account:
- Email: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- Project: `ehc-alberto-diazraya-35c897`

Save it as `service-account-key.json` in the project root.

### 2. Run Setup Script

```bash
./setup.sh
```

This will:
- Check Java and Maven installation
- Verify service account key
- Build the application

### 3. Start the Application

```bash
./run.sh
```

Then open your browser to: **http://localhost:8080**

## ✨ What You'll See

1. **Main Screen**: List of all datasets in your BigQuery project
2. **Click a Dataset**: See all tables in that dataset
3. **Scroll**: Browse through your data easily

## 🔧 Manual Setup (Alternative)

If you prefer to set up manually:

```bash
# 1. Ensure Java 17+ and Maven are installed
java -version
mvn -version

# 2. Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS=./service-account-key.json

# 3. Build
mvn clean package

# 4. Run
mvn spring-boot:run
```

## 📋 API Endpoints

Test the API directly:

```bash
# Test connection
curl http://localhost:8080/api/bigquery/test

# List datasets
curl http://localhost:8080/api/bigquery/datasets

# List tables in a dataset
curl http://localhost:8080/api/bigquery/datasets/DATASET_NAME/tables
```

## ⚠️ Troubleshooting

### "Service account key not found"
- Make sure `service-account-key.json` is in the project root
- Check the file is valid JSON
- Verify it has the correct permissions

### "Connection failed"
- Verify service account has `BigQuery Data Viewer` role
- Check project ID is correct: `ehc-alberto-diazraya-35c897`
- Ensure you have internet connectivity

### "Build failed"
- Check Java version is 17 or higher
- Ensure Maven is installed
- Try: `mvn clean install -U`

### Port already in use
- Change port in `application.properties`: `server.port=8081`
- Or kill the process using port 8080

## 📦 What's Included

- ✅ Java Spring Boot backend
- ✅ SIMBA BigQuery JDBC driver
- ✅ RESTful API endpoints
- ✅ Beautiful responsive UI
- ✅ Heroku deployment ready

## 🚢 Deploy to Heroku

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed Heroku deployment instructions.

Quick version:

```bash
# 1. Login and create app
heroku login
heroku create your-app-name

# 2. Set service account credentials
heroku config:set GOOGLE_APPLICATION_CREDENTIALS_JSON="$(cat service-account-key.json)"

# 3. Deploy
git push heroku main
```

## 📚 Need More Help?

- Full documentation: [README.md](README.md)
- Heroku deployment: [DEPLOYMENT.md](DEPLOYMENT.md)
- Issues? Check the logs: `heroku logs --tail`

## 🎯 Next Steps

1. ✅ Run locally and test the UI
2. ✅ Try the API endpoints
3. ✅ Deploy to Heroku for remote access
4. ✅ Customize the UI if needed

Happy browsing! 🎉






