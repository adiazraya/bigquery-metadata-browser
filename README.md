# BigQuery Metadata Browser

A comprehensive Java Spring Boot application for browsing Google BigQuery metadata with dual connection methods: **REST API** and **JDBC**.

## 🌟 Features

- **Dual Connection Architecture**: Compare performance between REST API and JDBC connections
- **Browse Datasets**: List all datasets in your BigQuery project
- **Browse Tables**: View all tables within a selected dataset
- **View Table Schema**: Click any table to see its complete field structure (name, type, mode, description)
- **Beautiful UI**: Modern, responsive 3-column interface with gradient styling
- **Performance Monitoring**: Comprehensive timing logs for every operation
- **Real-time Updates**: Refresh datasets on demand

## 🏗️ Architecture

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Logging**: SLF4J with detailed timing information

### Frontend
- **Pure HTML/CSS/JavaScript** (no frameworks)
- **Responsive Design**: Works on desktop and mobile
- **Real-time Performance Tracking**: Console timing logs

### Connection Methods
1. **REST API**: Google Cloud BigQuery Client Library
2. **JDBC**: SIMBA BigQuery JDBC Driver (optional)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Google Cloud Project with BigQuery enabled
- Service Account with BigQuery permissions

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/adiazraya/bigquery-metadata-browser.git
cd bigquery-metadata-browser
```

### 2. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# BigQuery Configuration
bigquery.project.id=your-project-id
bigquery.service.account.email=your-service-account@your-project.iam.gserviceaccount.com
bigquery.service.account.key.path=/path/to/your/service-account-key.json
```

### 3. Build and Run

```bash
# Build the application
mvn clean package -DskipTests

# Run the application
./run.sh

# Or manually:
java -jar target/incidencia-bq-1.0.0.jar
```

### 4. Access the Application

Open your browser and navigate to:
- **Main Menu**: http://localhost:8080/
- **API Version**: http://localhost:8080/api-metadata.html
- **JDBC Version**: http://localhost:8080/jdbc-metadata.html

## 📖 Usage

### Browsing Metadata

1. **Select a Dataset**: Click on any dataset in the left panel
2. **View Tables**: Tables will appear in the middle panel
3. **View Schema**: Click on any table to see its fields in the right panel

### API vs JDBC Comparison

- **API Version**: Uses Google's official BigQuery REST API v2
  - Pros: Official support, rich metadata
  - Cons: Slower for large datasets (pagination overhead)
  
- **JDBC Version**: Uses SIMBA JDBC driver with SQL queries
  - Pros: Much faster (up to 140x for large datasets)
  - Cons: Requires separate driver installation

## 📊 Monitoring

The application includes comprehensive monitoring capabilities:

### Live Dashboard
```bash
./monitor_live.sh
```

### Daily Report
```bash
./daily_report.sh
```

### View Logs
```bash
tail -f logs/incidencia-bq.log
```

See `MONITORING_GUIDE.md` for complete monitoring documentation.

## 🔧 Project Structure

```
IncidenciaBQ/
├── src/main/
│   ├── java/com/mercadolibre/incidenciabq/
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── model/               # Data models (Dataset, Table, Field)
│   │   └── service/             # Business logic (API & JDBC)
│   └── resources/
│       ├── static/              # Frontend files
│       │   ├── index.html       # Main menu
│       │   ├── api-metadata.html   # API version UI
│       │   ├── jdbc-metadata.html  # JDBC version UI
│       │   ├── app.js           # API version logic
│       │   ├── app-jdbc.js      # JDBC version logic
│       │   └── styles.css       # Styling
│       └── application.properties  # Configuration
├── logs/                        # Application logs
├── monitoring scripts/          # Monitoring utilities
├── documentation/               # Detailed docs
└── pom.xml                      # Maven configuration
```

## 🎯 API Endpoints

### REST API Version
- `GET /api/bigquery/datasets` - List all datasets
- `GET /api/bigquery/datasets/{datasetId}/tables` - List tables in dataset
- `GET /api/bigquery/datasets/{datasetId}/tables/{tableId}/schema` - Get table schema

### JDBC Version
- `GET /api/bigquery-jdbc/datasets` - List all datasets (via JDBC)
- `GET /api/bigquery-jdbc/datasets/{datasetId}/tables` - List tables (via JDBC)
- `GET /api/bigquery-jdbc/datasets/{datasetId}/tables/{tableId}/schema` - Get table schema (via JDBC)

## 📚 Documentation

- **`QUICK_START.md`**: Fast setup guide
- **`MONITORING_GUIDE.md`**: Complete monitoring documentation
- **`MONITORING_QUICK_START.md`**: Quick monitoring reference
- **`PERFORMANCE_ANALYSIS.md`**: API vs JDBC performance comparison
- **`SIMBA_JDBC_SETUP.md`**: JDBC driver installation guide
- **`TIMING_LOGS.md`**: Understanding timing logs

## ⚡ Performance

Based on real-world testing with ~30,000 tables:

| Method | Time | Relative Speed |
|--------|------|----------------|
| JDBC   | 1.3s | ⚡ **140x faster** |
| API    | 3.1min | 🐌 Slower (due to pagination) |

See `PERFORMANCE_ANALYSIS.md` for detailed analysis.

## 🔐 Security

**Important**: Never commit your service account key file!

The `.gitignore` file excludes:
- `*.json` (service account keys)
- `logs/` (may contain sensitive data)
- `lib/*.jar` (local dependencies)

## 🛠️ Development

### Build
```bash
mvn clean package
```

### Run in Development
```bash
mvn spring-boot:run
```

### View Logs
```bash
# All logs
tail -f logs/incidencia-bq.log

# Only timing information
tail -f logs/incidencia-bq.log | grep TIMING
```

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check if port 8080 is in use
lsof -ti:8080

# Kill existing process
lsof -ti:8080 | xargs kill
```

### Can't See BigQuery Data
- Verify service account has BigQuery permissions
- Check `application.properties` configuration
- Review logs: `tail -f logs/incidencia-bq.log`

### JDBC Version Not Working
- Install SIMBA driver (see `SIMBA_JDBC_SETUP.md`)
- Verify driver is in `lib/` directory
- Check Maven successfully included the driver

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Alberto Díaz Raya**
- Email: adiazraya@gmail.com
- GitHub: [@adiazraya](https://github.com/adiazraya)

## 🙏 Acknowledgments

- Google Cloud Platform for BigQuery
- SIMBA for the BigQuery JDBC driver
- Spring Boot team for the excellent framework

## 🔄 Version History

- **v1.0.0** (2025-12-18)
  - Initial release
  - Dual connection architecture (API + JDBC)
  - Three-column UI with schema viewing
  - Comprehensive monitoring and logging
  - Performance analysis tools

## 🚀 Future Enhancements

- [ ] Query execution interface
- [ ] Data preview functionality
- [ ] Export schema to various formats (JSON, CSV, SQL)
- [ ] Multi-project support
- [ ] Dark mode UI
- [ ] Advanced filtering and search
- [ ] Schema comparison between tables
- [ ] Cost estimation for queries

## 📞 Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Email: adiazraya@gmail.com

---

**Made with ❤️ using Spring Boot and BigQuery**
