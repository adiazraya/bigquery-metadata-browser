# IncidenciaBQ - Project Overview

## 🎯 Project Goal

Build a web application to browse BigQuery datasets and tables using SIMBA JDBC driver, with deployment capabilities to Heroku.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                   Browser                        │
│  ┌───────────────────────────────────────┐     │
│  │  HTML5 + CSS3 + JavaScript (Vanilla)  │     │
│  └───────────────┬───────────────────────┘     │
└──────────────────┼──────────────────────────────┘
                   │ HTTP/REST
┌──────────────────▼──────────────────────────────┐
│           Spring Boot Application               │
│  ┌─────────────────────────────────────────┐   │
│  │        Controllers (REST API)            │   │
│  │  • BigQueryController                    │   │
│  │    - GET /datasets                       │   │
│  │    - GET /datasets/{id}/tables           │   │
│  └──────────────┬──────────────────────────┘   │
│  ┌──────────────▼──────────────────────────┐   │
│  │           Services                       │   │
│  │  • BigQueryService                       │   │
│  │    - listDatasets()                      │   │
│  │    - listTables(datasetId)               │   │
│  └──────────────┬──────────────────────────┘   │
│  ┌──────────────▼──────────────────────────┐   │
│  │    Configuration & Models                │   │
│  │  • BigQueryConfig                        │   │
│  │  • Dataset, Table models                 │   │
│  └──────────────┬──────────────────────────┘   │
└─────────────────┼────────────────────────────────┘
                  │ JDBC (SIMBA Driver)
┌─────────────────▼────────────────────────────────┐
│              Google BigQuery                     │
│  Project: ehc-alberto-diazraya-35c897           │
│  Service Account: datacloud2sa@...              │
└──────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
IncidenciaBQ/
├── src/
│   └── main/
│       ├── java/com/mercadolibre/incidenciabq/
│       │   ├── IncidenciaBQApplication.java    # Main application
│       │   ├── config/
│       │   │   └── BigQueryConfig.java         # Configuration
│       │   ├── controller/
│       │   │   └── BigQueryController.java     # REST endpoints
│       │   ├── model/
│       │   │   ├── Dataset.java                # Dataset model
│       │   │   └── Table.java                  # Table model
│       │   └── service/
│       │       └── BigQueryService.java        # Business logic
│       └── resources/
│           ├── application.properties           # Main config
│           ├── application-production.properties# Heroku config
│           └── static/
│               ├── index.html                  # Main UI
│               ├── styles.css                  # Styling
│               └── app.js                      # Frontend logic
├── pom.xml                                      # Maven dependencies
├── Procfile                                     # Heroku process
├── system.properties                            # Java version
├── setup.sh                                     # Setup script
├── run.sh                                       # Run script
├── README.md                                    # Main documentation
├── QUICKSTART.md                                # Quick start guide
├── DEPLOYMENT.md                                # Heroku deployment
├── TEST.md                                      # Testing guide
├── .gitignore                                   # Git ignore rules
└── service-account-key.json.example            # Key template
```

## 🔑 Key Components

### Backend (Java Spring Boot)

#### 1. BigQueryConfig
- Manages configuration for BigQuery connection
- Handles service account authentication
- Supports both file-based and environment variable credentials
- Automatically creates temp files for Heroku deployment

#### 2. BigQueryService
- Core business logic
- Connects to BigQuery via SIMBA JDBC driver
- Queries INFORMATION_SCHEMA for metadata
- Methods:
  - `listDatasets()`: Retrieves all datasets
  - `listTables(datasetId)`: Retrieves tables in a dataset
  - `testConnection()`: Verifies connectivity

#### 3. BigQueryController
- REST API endpoints
- Handles HTTP requests/responses
- CORS enabled for frontend access
- Endpoints:
  - `GET /api/bigquery/test`: Connection test
  - `GET /api/bigquery/datasets`: List datasets
  - `GET /api/bigquery/datasets/{id}/tables`: List tables

#### 4. Models
- **Dataset**: Represents a BigQuery dataset
  - datasetId, projectId, friendlyName, etc.
- **Table**: Represents a BigQuery table
  - tableId, datasetId, type, numRows, etc.

### Frontend (HTML/CSS/JavaScript)

#### 1. index.html
- Main UI structure
- Two-column layout: datasets | tables
- Responsive design
- Loading states and error handling

#### 2. styles.css
- Modern, gradient design
- Smooth animations and transitions
- Custom scrollbars
- Mobile responsive (grid layout)
- Professional color scheme

#### 3. app.js
- Fetches data from REST API
- Updates UI dynamically
- Handles user interactions
- Error handling and loading states
- Features:
  - Click dataset to load tables
  - Smooth scrolling
  - Visual feedback (highlights)

## 🔧 Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.0 |
| JDBC Driver | SIMBA BigQuery | 1.5.2 |
| Build Tool | Maven | 3.6+ |
| Frontend | HTML5/CSS3/JS | Vanilla |
| Cloud Platform | Google BigQuery | - |
| Deployment | Heroku | - |

## 🔐 Authentication

**Method**: Service Account with JSON Key

**Service Account Details:**
- Email: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- Project: `ehc-alberto-diazraya-35c897`
- Required Role: `BigQuery Data Viewer`

**Local Development:**
- Key stored in `service-account-key.json`
- Referenced via `GOOGLE_APPLICATION_CREDENTIALS` env var

**Heroku Production:**
- Key content stored in `GOOGLE_APPLICATION_CREDENTIALS_JSON` config var
- Automatically written to temp file on startup

## 📊 Data Flow

### Listing Datasets

```
User clicks "Refresh"
    ↓
Frontend: GET /api/bigquery/datasets
    ↓
Controller: BigQueryController.listDatasets()
    ↓
Service: BigQueryService.listDatasets()
    ↓
SIMBA JDBC: Query INFORMATION_SCHEMA.SCHEMATA
    ↓
BigQuery: Returns dataset metadata
    ↓
Service: Maps to Dataset objects
    ↓
Controller: Returns JSON array
    ↓
Frontend: Renders dataset list
```

### Listing Tables

```
User clicks dataset
    ↓
Frontend: GET /api/bigquery/datasets/{id}/tables
    ↓
Controller: BigQueryController.listTables(id)
    ↓
Service: BigQueryService.listTables(id)
    ↓
SIMBA JDBC: Query {dataset}.INFORMATION_SCHEMA.TABLES
    ↓
BigQuery: Returns table metadata
    ↓
Service: Maps to Table objects
    ↓
Controller: Returns JSON array
    ↓
Frontend: Renders table list
```

## 🚀 Deployment Strategy

### Local Development
1. Clone repository
2. Add service account key
3. Run `./setup.sh`
4. Run `./run.sh`
5. Access at `http://localhost:8080`

### Heroku Deployment
1. Create Heroku app
2. Set config vars (service account JSON)
3. Push to Heroku Git
4. Heroku builds and deploys
5. Access at `https://your-app.herokuapp.com`

### Environment Variables

| Variable | Local | Heroku |
|----------|-------|--------|
| `GOOGLE_APPLICATION_CREDENTIALS` | `./service-account-key.json` | N/A |
| `GOOGLE_APPLICATION_CREDENTIALS_JSON` | N/A | Full JSON content |
| `PORT` | 8080 (default) | Set by Heroku |

## 🔍 SQL Queries Used

### List Datasets
```sql
SELECT schema_name 
FROM INFORMATION_SCHEMA.SCHEMATA
```

### List Tables
```sql
SELECT table_name, table_type 
FROM `{project}.{dataset}.INFORMATION_SCHEMA.TABLES`
```

## 📈 Future Enhancements

### Short Term
- [ ] Add table schema viewer
- [ ] Implement data preview (first 100 rows)
- [ ] Add search/filter functionality
- [ ] Show more metadata (creation date, size, etc.)

### Medium Term
- [ ] User authentication
- [ ] Multiple project support
- [ ] Query editor
- [ ] Export capabilities (CSV, JSON)

### Long Term
- [ ] Data visualization
- [ ] Scheduled queries
- [ ] Admin dashboard
- [ ] Role-based access control

## 🧪 Testing Strategy

### Local Testing
1. Connection test
2. API endpoint tests (curl)
3. UI testing (manual)
4. Browser DevTools inspection

### Production Testing
1. Heroku deployment test
2. Live API verification
3. Performance monitoring
4. Error logging

## 📝 Configuration Files

### application.properties
- Server port
- BigQuery project ID
- Service account details
- Logging levels

### application-production.properties
- Heroku-specific settings
- Production logging
- Performance optimizations

### pom.xml
- Spring Boot dependencies
- SIMBA JDBC driver
- Lombok for cleaner code
- Build configuration

### Procfile
- Heroku web dyno command
- Port configuration from `$PORT`

### system.properties
- Java runtime version (17)

## 🔒 Security Considerations

### Current
- Service account key in gitignore
- No key committed to repository
- Environment variables for sensitive data
- CORS enabled (all origins)

### Recommended for Production
- Restrict CORS to specific domains
- Add API authentication
- Implement rate limiting
- Use secret management service
- Regular key rotation
- Audit logging

## 📚 Documentation

- **README.md**: Comprehensive setup and usage
- **QUICKSTART.md**: Fast-track setup guide
- **DEPLOYMENT.md**: Detailed Heroku deployment
- **TEST.md**: Testing procedures
- **PROJECT_OVERVIEW.md**: This file (architecture)

## 🎓 Learning Resources

### Technologies Used
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [BigQuery Documentation](https://cloud.google.com/bigquery/docs)
- [SIMBA JDBC Driver](https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers)
- [Heroku Java Support](https://devcenter.heroku.com/categories/java-support)

### Tutorials
- REST API with Spring Boot
- BigQuery JDBC connectivity
- Heroku deployment process
- Service account authentication

## 💡 Design Decisions

### Why SIMBA JDBC?
- Official Google-supported driver
- Full JDBC compatibility
- Good documentation
- Reliable performance

### Why Spring Boot?
- Industry standard
- Easy REST API creation
- Built-in server
- Heroku-friendly

### Why Vanilla JavaScript?
- No build process needed
- Lightweight
- Fast loading
- Easy to understand

### Why Service Account?
- Automated authentication
- No user interaction needed
- Secure key-based auth
- Easy to rotate credentials

## 🎯 Success Metrics

### Functionality
- ✅ Lists all datasets
- ✅ Lists tables per dataset
- ✅ Responsive UI
- ✅ Error handling

### Performance
- < 5 seconds for dataset listing
- < 3 seconds for table listing
- Smooth UI interactions

### Deployment
- ✅ Runs locally
- ✅ Heroku-ready
- ✅ Easy setup process

## 👥 Roles and Responsibilities

### Developer Tasks
- Code implementation
- Testing
- Documentation
- Deployment

### Required Permissions
- BigQuery: Data Viewer
- Heroku: App access
- Git: Repository access

---

**Project Status**: ✅ Complete and ready for deployment
**Last Updated**: December 2025
**Version**: 1.0.0



