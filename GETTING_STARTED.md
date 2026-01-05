# 🚀 Getting Started with IncidenciaBQ

Welcome! This guide will help you get your BigQuery browser app running in just a few minutes.

## ⚡ Prerequisites

Before you begin, make sure you have:

1. ✅ **Java 17 or higher**
   ```bash
   java -version
   ```

2. ✅ **Maven 3.6 or higher**
   ```bash
   mvn -version
   ```

3. ✅ **Service Account Key** for BigQuery
   - You need the JSON key file for: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
   - Project: `ehc-alberto-diazraya-35c897`

## 🎯 Quick Start (3 Simple Steps)

### Step 1: Add Your Service Account Key

1. Obtain the service account JSON key file
2. Rename it to `service-account-key.json`
3. Place it in the project root directory:
   ```
   IncidenciaBQ/
   ├── service-account-key.json  ← PUT IT HERE
   ├── pom.xml
   ├── setup.sh
   └── ...
   ```

> ⚠️ **Important**: This file contains sensitive credentials. Never commit it to Git!

### Step 2: Run the Setup

```bash
./setup.sh
```

This script will:
- ✓ Verify Java and Maven are installed
- ✓ Check for the service account key
- ✓ Build the application
- ✓ Prepare everything for first run

### Step 3: Start the Application

```bash
./run.sh
```

That's it! Open your browser to: **http://localhost:8080**

## 🎨 What You'll See

### Main Interface

```
┌─────────────────────────────────────────────────┐
│        BigQuery Browser                         │
│   Project: ehc-alberto-diazraya-35c897         │
├──────────────────┬──────────────────────────────┤
│   Datasets       │         Tables               │
│  ┌────────────┐  │                              │
│  │ dataset_1  │  │  Select a dataset to view    │
│  ├────────────┤  │  its tables                  │
│  │ dataset_2  │  │                              │
│  ├────────────┤  │                              │
│  │ dataset_3  │  │                              │
│  └────────────┘  │                              │
└──────────────────┴──────────────────────────────┘
```

### How to Use

1. **View Datasets**: On the left, you'll see all datasets in your BigQuery project
2. **Select Dataset**: Click any dataset to load its tables
3. **Browse Tables**: Tables appear on the right with details like type and row count
4. **Scroll**: Both panels are scrollable for large lists

## 🧪 Testing the API

You can also test the backend directly:

### Test Connection
```bash
curl http://localhost:8080/api/bigquery/test
```

### List All Datasets
```bash
curl http://localhost:8080/api/bigquery/datasets | jq
```

### List Tables in a Dataset
```bash
curl http://localhost:8080/api/bigquery/datasets/YOUR_DATASET/tables | jq
```

## 📁 Project Structure Overview

```
IncidenciaBQ/
├── 📄 README.md                    # Main documentation
├── 📄 QUICKSTART.md                # Quick reference
├── 📄 GETTING_STARTED.md           # This file
├── 📄 DEPLOYMENT.md                # Heroku deployment guide
├── 📄 TEST.md                      # Testing guide
├── 📄 PROJECT_OVERVIEW.md          # Technical architecture
│
├── 🔧 pom.xml                      # Maven configuration
├── 🔧 Procfile                     # Heroku configuration
├── 🔧 system.properties            # Java version for Heroku
│
├── 🔐 service-account-key.json     # YOUR KEY (not in git)
├── 📝 service-account-key.json.example  # Key template
│
├── 🚀 setup.sh                     # Setup script
├── 🚀 run.sh                       # Run script
│
└── 📂 src/
    └── 📂 main/
        ├── 📂 java/                # Backend code
        │   └── com/mercadolibre/incidenciabq/
        │       ├── IncidenciaBQApplication.java
        │       ├── config/
        │       ├── controller/
        │       ├── model/
        │       └── service/
        │
        └── 📂 resources/
            ├── application.properties
            └── 📂 static/          # Frontend code
                ├── index.html
                ├── styles.css
                └── app.js
```

## 🔧 Troubleshooting

### "Service account key not found"

**Problem**: The application can't find your credentials file.

**Solution**:
```bash
# Check if file exists
ls -l service-account-key.json

# It should be in the project root
pwd  # Make sure you're in the right directory
```

### "Port 8080 already in use"

**Problem**: Another application is using port 8080.

**Solution**:
```bash
# Option 1: Kill the process using the port
lsof -i :8080
kill -9 <PID>

# Option 2: Change the port
echo "server.port=8081" >> src/main/resources/application.properties
```

### "Connection failed"

**Problem**: Can't connect to BigQuery.

**Solution**:
1. Verify service account has `BigQuery Data Viewer` role
2. Check project ID is correct: `ehc-alberto-diazraya-35c897`
3. Ensure you have internet connectivity
4. Validate JSON key file:
   ```bash
   cat service-account-key.json | jq .
   ```

### "Build failed"

**Problem**: Maven build errors.

**Solution**:
```bash
# Clean and rebuild
mvn clean install -U

# Check Java version
java -version  # Must be 17 or higher
```

## 📚 Next Steps

Now that you have the app running locally, you might want to:

1. **📖 Read the full documentation**
   - See [README.md](README.md) for complete details

2. **🧪 Run comprehensive tests**
   - See [TEST.md](TEST.md) for testing procedures

3. **🚢 Deploy to Heroku**
   - See [DEPLOYMENT.md](DEPLOYMENT.md) for deployment steps

4. **🏗️ Understand the architecture**
   - See [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) for technical details

## 💡 Tips

### Running in Development Mode

For faster development with auto-reload:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Viewing Logs

Logs appear in the terminal where you ran `./run.sh`. Look for:
- ✅ `Started IncidenciaBQApplication` - App is running
- ✅ `Connection test successful!` - BigQuery connected
- ❌ `ERROR` messages - Something went wrong

### Stopping the Application

Press `Ctrl + C` in the terminal where the app is running.

### Building for Production

```bash
mvn clean package -DskipTests
java -jar target/incidencia-bq-1.0.0.jar
```

## 🆘 Need Help?

1. **Check the logs** - Most issues show helpful error messages
2. **Review documentation** - All .md files have detailed info
3. **Verify credentials** - Most problems are authentication-related
4. **Check connectivity** - Ensure you can reach BigQuery
5. **Try clean build** - `mvn clean install -U`

## ✅ Success Checklist

After setup, you should be able to:

- [ ] Run `./setup.sh` without errors
- [ ] Start app with `./run.sh`
- [ ] See "Started IncidenciaBQApplication" in logs
- [ ] Access http://localhost:8080 in browser
- [ ] See list of datasets
- [ ] Click dataset and see tables
- [ ] Scroll through large lists
- [ ] Refresh datasets with button

## 🎉 You're Ready!

If everything above works, congratulations! Your BigQuery browser is ready to use.

**Enjoy browsing your data!** 🚀

---

For questions or issues, refer to:
- [README.md](README.md) - Complete documentation
- [TEST.md](TEST.md) - Troubleshooting guide
- [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) - Architecture details






