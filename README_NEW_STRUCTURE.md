# IncidenciaBQ - New Application Structure

## 🎯 Overview

The application now features a **dual-connection architecture** allowing you to compare two different methods of connecting to Google BigQuery:

1. **REST API Approach** - Using Google Cloud BigQuery Client Library
2. **JDBC Approach** - Using SIMBA BigQuery JDBC Driver (requires manual installation)

## 🏗️ Application Structure

### Main Landing Page
- **URL**: `http://localhost:8080/`
- **File**: `src/main/resources/static/index.html`
- **Purpose**: Parent window with navigation to both connection methods

### Metadata Info with API
- **URL**: `http://localhost:8080/api-metadata.html`
- **Frontend Files**: 
  - `src/main/resources/static/api-metadata.html`
  - `src/main/resources/static/app.js`
- **Backend**:
  - Service: `BigQueryService.java`
  - Controller: `BigQueryController.java` (`/api/bigquery/*`)
  - Config: `BigQueryConfig.java`
- **Connection Method**: REST API via Google Cloud BigQuery Client Library
- **Status**: ✅ **Fully Functional**

### Metadata Info with JDBC
- **URL**: `http://localhost:8080/jdbc-metadata.html`
- **Frontend Files**:
  - `src/main/resources/static/jdbc-metadata.html`
  - `src/main/resources/static/app-jdbc.js`
- **Backend**:
  - Service: `BigQueryJdbcService.java`
  - Controller: `BigQueryJdbcController.java` (`/api/bigquery-jdbc/*`)
  - Config: `BigQueryJdbcConfig.java`
- **Connection Method**: JDBC/SQL queries on INFORMATION_SCHEMA
- **Status**: ⚠️ **Requires SIMBA Driver Installation** (see [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md))

## 📁 File Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/mercadolibre/incidenciabq/
│   │       ├── config/
│   │       │   ├── BigQueryConfig.java          # REST API configuration
│   │       │   └── BigQueryJdbcConfig.java      # JDBC configuration
│   │       ├── controller/
│   │       │   ├── BigQueryController.java       # REST API endpoints
│   │       │   └── BigQueryJdbcController.java   # JDBC endpoints
│   │       ├── model/
│   │       │   ├── Dataset.java
│   │       │   └── Table.java
│   │       ├── service/
│   │       │   ├── BigQueryService.java          # REST API service
│   │       │   └── BigQueryJdbcService.java      # JDBC service
│   │       └── IncidenciaBQApplication.java
│   └── resources/
│       ├── static/
│       │   ├── index.html               # Main landing page
│       │   ├── api-metadata.html        # REST API UI
│       │   ├── jdbc-metadata.html       # JDBC UI
│       │   ├── app.js                   # REST API JavaScript
│       │   ├── app-jdbc.js              # JDBC JavaScript
│       │   └── styles.css               # Shared styles
│       └── application.properties
```

## 🔌 API Endpoints

### REST API Endpoints
- `GET /api/bigquery/datasets` - List all datasets using REST API
- `GET /api/bigquery/datasets/{datasetId}/tables` - List tables in dataset using REST API

### JDBC Endpoints
- `GET /api/bigquery-jdbc/datasets` - List all datasets using JDBC
- `GET /api/bigquery-jdbc/datasets/{datasetId}/tables` - List tables in dataset using JDBC

## 🚀 Quick Start

### Running with REST API Only (Default)

```bash
./run.sh
```

Navigate to:
- Main Page: http://localhost:8080/
- REST API Version: http://localhost:8080/api-metadata.html

### Running with Both Methods

1. Install SIMBA driver (see [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md))
2. Run the application:

```bash
./run.sh
```

Navigate to:
- Main Page: http://localhost:8080/
- REST API Version: http://localhost:8080/api-metadata.html ✅
- JDBC Version: http://localhost:8080/jdbc-metadata.html ✅

## 📊 Feature Comparison

| Feature | REST API | JDBC |
|---------|----------|------|
| **Installation** | ✅ Built-in | ⚠️ Manual download required |
| **Connection Method** | HTTP/REST | Traditional SQL |
| **Query Type** | API Methods | SQL on INFORMATION_SCHEMA |
| **Performance** | Optimized | Standard JDBC overhead |
| **Dependencies** | Maven Central | Local installation |
| **Metadata Source** | Direct API | SQL queries |
| **Best For** | Modern apps | Legacy systems, SQL tools |

## 📝 Detailed Logging

Both approaches include comprehensive logging:

- **Console**: Minimal output (INFO level)
- **File**: Detailed logs in `logs/incidencia-bq.log`
  - All API calls with parameters
  - Query execution times
  - Response processing details
  - Individual field extraction
  - Performance breakdowns

**Log Prefixes**:
- `[TIMING]` - Performance measurements
- `[DETAIL]` - Granular operation details
- `[TIMING][JDBC]` / `[DETAIL][JDBC]` - JDBC-specific logs

## 🔧 Configuration

All configuration is in `src/main/resources/application.properties`:

```properties
# BigQuery Configuration
bigquery.project.id=ehc-alberto-diazraya-35c897
bigquery.service.account.email=datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com
bigquery.service.account.key.path=./service-account-key.json

# Logging Configuration
logging.file.name=logs/incidencia-bq.log
```

## 🎨 User Interface

### Main Landing Page
- Clean, modern design with gradient background
- Two card-based options for navigation
- Visual distinction between REST API and JDBC approaches
- Information section explaining both methods

### Metadata Browser Pages
Both pages share the same layout:
- **Left Panel**: List of datasets
- **Right Panel**: Tables in selected dataset
- **Header**: Method indicator (REST API vs JDBC)
- **Footer**: Connection method and technology stack
- **Back Link**: Return to main menu

## 🔍 How Each Method Works

### REST API Method
1. Uses Google Cloud BigQuery Client Library
2. Makes direct REST API calls to BigQuery API v2
3. Receives structured JSON responses
4. Processes response objects directly
5. **Query**: `BigQuery.listDatasets()`, `BigQuery.listTables()`

### JDBC Method
1. Uses SIMBA BigQuery JDBC Driver
2. Creates traditional JDBC connection
3. Executes SQL queries on INFORMATION_SCHEMA
4. Processes SQL ResultSet
5. **Query**: 
   ```sql
   SELECT schema_name FROM INFORMATION_SCHEMA.SCHEMATA
   SELECT table_name, table_type FROM INFORMATION_SCHEMA.TABLES
   ```

## 📈 Performance Monitoring

Both methods log detailed timing information:

**Stages Measured**:
1. Client/Connection acquisition
2. API call / SQL query execution
3. Response/ResultSet processing
4. UI rendering (frontend)
5. JSON serialization (backend)

**Example Log Output**:
```
[TIMING] ########## GET /api/bigquery/datasets completed in 1234ms ##########
[TIMING] Breakdown: service: 1100ms (89%), serialization: 134ms (11%)
```

## 🧪 Testing Both Methods

1. Start the application
2. Open the main page
3. Click "Metadata Info with API"
   - Should work immediately
   - Uses REST API
4. Click "Metadata Info with JDBC"
   - If SIMBA installed: Works with SQL queries
   - If not installed: Shows error message

## 🔒 Security

Both methods use the same authentication:
- Service Account: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- Key File: `./service-account-key.json`
- OAuth 2.0 authentication
- Secure credential handling

## 📚 Additional Documentation

- [SIMBA Installation Guide](./SIMBA_INSTALLATION.md) - How to install JDBC driver
- [Deployment Guide](./DEPLOYMENT.md) - Heroku deployment instructions
- [Logging to File Summary](./LOGGING_TO_FILE_SUMMARY.md) - Logging configuration details

## 🛠️ Development

### Adding New Endpoints

**For REST API**:
1. Add method to `BigQueryService.java`
2. Add endpoint to `BigQueryController.java`
3. Update `app.js` if needed

**For JDBC**:
1. Add method to `BigQueryJdbcService.java`
2. Add endpoint to `BigQueryJdbcController.java`
3. Update `app-jdbc.js` if needed

### Modifying the UI

- **Shared styles**: Edit `styles.css`
- **Main page**: Edit `index.html`
- **REST API page**: Edit `api-metadata.html` and `app.js`
- **JDBC page**: Edit `jdbc-metadata.html` and `app-jdbc.js`

## 🐛 Troubleshooting

### JDBC Not Working
- See [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md)
- Check logs in `logs/incidencia-bq.log`
- Look for `[JDBC] ✗ SIMBA BigQuery JDBC Driver NOT found`

### REST API Issues
- Verify service account key exists
- Check BigQuery permissions
- Review logs for API error messages

### General Issues
- Check `logs/incidencia-bq.log` for detailed error information
- Verify `application.properties` configuration
- Ensure port 8080 is available

## 🎯 Next Steps

1. **Try REST API Method** - Already works out of the box
2. **Install SIMBA Driver** - Follow [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md)
3. **Compare Performance** - Test both methods with your data
4. **Review Logs** - Check `logs/incidencia-bq.log` for insights
5. **Deploy to Heroku** - Follow [DEPLOYMENT.md](./DEPLOYMENT.md)

## 📞 Support

For issues or questions:
- Check the detailed logs in `logs/incidencia-bq.log`
- Review the documentation files in the project root
- Ensure all prerequisites are installed correctly

---

**Version**: 1.0.0  
**Last Updated**: December 2025  
**Author**: MercadoLibre



