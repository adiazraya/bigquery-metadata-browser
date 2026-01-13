# 🚀 Local Setup Guide - BigQuery Metadata Browser

Complete step-by-step guide for setting up this project on your local machine.

---

## 📋 Prerequisites to Install

Before you start, you'll need to install the following software:

### 1. **Java Development Kit (JDK) 17 or higher**

**Check if you have Java:**
```bash
java -version
```

**Install Java:**
- **macOS** (using Homebrew):
  ```bash
  brew install openjdk@17
  ```

- **Ubuntu/Debian**:
  ```bash
  sudo apt update
  sudo apt install openjdk-17-jdk
  ```

- **Windows**:
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
  - Install and add to PATH

**Verify installation:**
```bash
java -version
# Should show: openjdk version "17.0.x" or higher
```

### 2. **Apache Maven 3.6 or higher**

**Check if you have Maven:**
```bash
mvn -version
```

**Install Maven:**
- **macOS** (using Homebrew):
  ```bash
  brew install maven
  ```

- **Ubuntu/Debian**:
  ```bash
  sudo apt update
  sudo apt install maven
  ```

- **Windows**:
  - Download from [Apache Maven](https://maven.apache.org/download.cgi)
  - Extract and add `bin/` directory to PATH

**Verify installation:**
```bash
mvn -version
# Should show: Apache Maven 3.6.x or higher
```

### 3. **Git**

**Check if you have Git:**
```bash
git --version
```

**Install Git:**
- **macOS**: Already installed, or `brew install git`
- **Ubuntu/Debian**: `sudo apt install git`
- **Windows**: Download from [git-scm.com](https://git-scm.com/)

### 4. **Google Cloud Service Account** (Setup instructions below)

---

## 🔧 Step-by-Step Setup

### Step 1: Clone the Repository

```bash
# Clone the project
git clone https://github.com/adiazraya/bigquery-metadata-browser.git

# Navigate into the project directory
cd bigquery-metadata-browser
```

### Step 2: Set Up Google Cloud Service Account

You need a Google Cloud service account with BigQuery access:

#### Option A: Get Service Account from Project Owner
Ask the project owner for:
- Service account JSON key file
- Project ID

#### Option B: Create Your Own Service Account

1. **Go to Google Cloud Console:**
   - Visit: https://console.cloud.google.com/

2. **Select or Create a Project:**
   - Select existing project or create a new one

3. **Enable BigQuery API:**
   - Go to: APIs & Services → Library
   - Search for "BigQuery API"
   - Click "Enable"

4. **Create Service Account:**
   - Go to: IAM & Admin → Service Accounts
   - Click "Create Service Account"
   - Name: `bigquery-metadata-browser`
   - Click "Create and Continue"

5. **Assign Roles:**
   Add these two roles (minimum required):
   - `BigQuery Metadata Viewer` (roles/bigquery.metadataViewer)
   - `BigQuery Job User` (roles/bigquery.jobUser)
   
   Click "Continue" then "Done"

6. **Create Key:**
   - Click on the newly created service account
   - Go to "Keys" tab
   - Click "Add Key" → "Create new key"
   - Select "JSON"
   - Click "Create"
   - The JSON file will download automatically

7. **Save the Key File:**
   ```bash
   # Copy the downloaded JSON file to the project directory
   cp ~/Downloads/your-project-xxxxx.json ./service-account-key.json
   ```

### Step 3: Configure the Application

Edit the configuration file:

```bash
# Open the configuration file
nano src/main/resources/application.properties
# Or use your preferred editor: vim, code, etc.
```

Update these values:

```properties
# Your Google Cloud Project ID
bigquery.project.id=YOUR-PROJECT-ID

# Your service account email (found in the JSON key file)
bigquery.service.account.email=your-service-account@your-project.iam.gserviceaccount.com

# Path to your service account key file
bigquery.service.account.key.path=./service-account-key.json
```

**How to find these values:**
- Open your `service-account-key.json` file
- `project_id` → Copy to `bigquery.project.id`
- `client_email` → Copy to `bigquery.service.account.email`

### Step 4: Build the Application

```bash
# Clean and build the project
mvn clean package -DskipTests

# This will:
# 1. Download all dependencies
# 2. Compile the Java code
# 3. Create a JAR file in target/incidencia-bq-1.0.0.jar
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 30.xxx s
```

**If build fails:**
- Check Java version: `java -version` (must be 17+)
- Check Maven version: `mvn -version` (must be 3.6+)
- Check internet connection (Maven needs to download dependencies)

### Step 5: Run the Application

**Option A: Using the run script (recommended):**
```bash
./run.sh
```

**Option B: Manual execution:**
```bash
java -jar target/incidencia-bq-1.0.0.jar
```

**Option C: Using Maven:**
```bash
mvn spring-boot:run
```

**Expected output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

... Application running on http://localhost:8080
```

### Step 6: Access the Application

Open your web browser and go to:

🏠 **Main Menu:**
```
http://localhost:8080/
```

You should see three options:
1. 🌐 REST API Metadata Browser
2. 🔌 JDBC Metadata Browser
3. 🔐 Service Account Manager

### Step 7: Test the Application

**Test REST API version:**
1. Click "REST API Metadata Browser"
2. You should see a list of datasets in the left panel
3. Click on any dataset to see its tables
4. Click on any table to see its schema

**Test JDBC version (already included):**
1. Click "JDBC Metadata Browser"
2. Should work the same way (using JDBC instead of REST)

---

## ✅ Verification Checklist

Use this checklist to verify everything is working:

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] Repository cloned successfully
- [ ] Service account JSON key file in project root
- [ ] `application.properties` configured with correct values
- [ ] Application builds without errors (`mvn clean package`)
- [ ] Application starts without errors
- [ ] Can access http://localhost:8080/
- [ ] Can see datasets in REST API version
- [ ] Can click on dataset and see tables
- [ ] Can click on table and see schema

---

## 🎨 Additional Features

### Service Account Manager

Upload and test different service accounts without restarting:

1. Go to http://localhost:8080/service-account.html
2. Upload a different service account JSON file
3. Test connections with REST API and JDBC

### Monitoring

View live logs:
```bash
# Watch all logs
tail -f logs/incidencia-bq.log

# Watch only timing information
tail -f logs/incidencia-bq.log | grep TIMING

# Live dashboard
./monitor_live.sh

# Daily report
./daily_report.sh
```

---

## 🐛 Troubleshooting

### Issue: Port 8080 already in use

**Error message:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solution:**
```bash
# Find what's using port 8080
lsof -ti:8080

# Kill the process
lsof -ti:8080 | xargs kill

# Or change the port in application.properties:
server.port=8081
```

### Issue: Can't connect to BigQuery

**Error message:**
```
Failed to list datasets
403 Forbidden
```

**Solutions:**

1. **Check service account permissions:**
   - Service account must have these roles:
     - `roles/bigquery.metadataViewer`
     - `roles/bigquery.jobUser`

2. **Verify configuration:**
   ```bash
   # Check if service account key exists
   ls -la service-account-key.json
   
   # Verify it's valid JSON
   cat service-account-key.json | python -m json.tool
   ```

3. **Check project ID:**
   - Open `service-account-key.json`
   - Verify `project_id` matches your BigQuery project

### Issue: Maven build fails

**Error message:**
```
Failed to execute goal ... Could not resolve dependencies
```

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean package -DskipTests

# If still fails, check internet connection
curl -I https://repo.maven.apache.org/maven2/
```

### Issue: Java version mismatch

**Error message:**
```
unsupported class file version 61.0
```

**Solution:**
```bash
# Check Java version
java -version

# Must be 17 or higher
# If lower, install Java 17+

# On macOS:
brew install openjdk@17
```

### Issue: Can't see any datasets

**Possible causes:**

1. **No datasets in project:**
   ```bash
   # Create a test dataset in BigQuery Console
   # Or use a project that already has datasets
   ```

2. **Wrong project ID:**
   - Verify `bigquery.project.id` in `application.properties`
   - Must match the project that has datasets

3. **Service account lacks access:**
   - Grant `BigQuery Metadata Viewer` role to service account

---

## 📚 Additional Documentation

Once you have the application running, check these files for more information:

- **`README.md`** - General overview
- **`QUICK_START.md`** - Fast setup guide
- **`SERVICE_ACCOUNT_QUICK.md`** - Service account management
- **`MONITORING_GUIDE.md`** - Monitoring and logging
- **`PERFORMANCE_ANALYSIS.md`** - REST API vs JDBC comparison
- **`BIGQUERY_PERMISSIONS_GUIDE.md`** - Detailed permissions info

---

## 🚀 Quick Commands Reference

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
./run.sh

# Stop the application
# Press Ctrl+C in the terminal

# View logs
tail -f logs/incidencia-bq.log

# Check if application is running
curl http://localhost:8080/actuator/health

# Run tests
mvn test

# Clean build artifacts
mvn clean
```

---

## 📞 Need Help?

If you encounter issues:

1. Check the troubleshooting section above
2. Review logs: `tail -f logs/incidencia-bq.log`
3. Contact the project owner
4. Open an issue on GitHub: https://github.com/adiazraya/bigquery-metadata-browser/issues

---

## 🎯 Success! What's Next?

Once everything is running:

1. **Explore the UI:**
   - Browse your BigQuery datasets
   - View table schemas
   - Compare REST API vs JDBC performance

2. **Upload different service accounts:**
   - Test with different GCP projects
   - No need to restart the application

3. **Monitor performance:**
   - Check the browser console for timing information
   - Review backend logs for detailed metrics

4. **Read the documentation:**
   - Learn about the architecture
   - Understand the two connection methods
   - Explore monitoring capabilities

---

**Happy exploring! 🎉**
