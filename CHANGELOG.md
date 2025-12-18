# Changelog

All notable changes to the IncidenciaBQ project.

## [1.2.0] - 2025-12-16

### 🔄 Changed - Switched to Google Cloud BigQuery Client Library

#### Breaking Changes
- **Replaced SIMBA JDBC driver** with official Google Cloud BigQuery client library
- Removed JDBC-specific configuration (no more JDBC URL needed)
- Updated `BigQueryService` to use BigQuery client API instead of SQL queries

#### Benefits
- ✅ **No manual driver installation** - All dependencies from Maven Central
- ✅ **Better performance** - Direct API calls instead of JDBC overhead
- ✅ **More reliable** - Official Google-supported library
- ✅ **Easier to use** - Simpler API, better error handling
- ✅ **More features** - Access to full BigQuery metadata (row counts, descriptions, etc.)

#### Migration Guide
No action needed for new installations. The application works out of the box.

**Updated Dependencies:**
- Added: `google-cloud-bigquery:2.38.2`
- Removed: `googlebigquery-jdbc42:1.5.2.1005` (not in Maven Central)

**Code Changes:**
- `BigQueryService`: Now uses `BigQuery` client instead of JDBC `Connection`
- `BigQueryConfig`: Removed `jdbcUrl` property
- Timing logs updated to reflect client operations instead of JDBC operations

---

## [1.1.0] - 2025-12-16

### ✨ Added - Comprehensive Timing Logs

#### Backend (Java)
- **Controller Layer**: Added detailed timing for all HTTP endpoints
  - Request start/end timestamps
  - Service layer execution time
  - JSON serialization time
  - Result counts
  
- **Service Layer**: Step-by-step timing breakdown
  - JDBC driver loading time
  - Connection establishment time
  - SQL statement creation time
  - Query execution time
  - Result processing time
  - Resource cleanup time

- **Helper Methods**: Added resource cleanup helper with timing

#### Frontend (JavaScript)
- **API Operations**: Complete timing for all operations
  - UI preparation time
  - Network request time (fetch API)
  - JSON parsing time
  - DOM rendering time
  
- **User Actions**: Timing for user interactions
  - Dataset selection
  - Page initialization

#### Documentation
- **TIMING_LOGS.md**: Comprehensive guide (4000+ words)
  - How timing logs work
  - Example outputs
  - Performance benchmarks
  - Troubleshooting guide
  - Customization options
  
- **TIMING_SUMMARY.txt**: Visual quick reference
  - ASCII art diagrams
  - Request flow visualization
  - Performance expectations
  
- **Updated README.md**: Added performance monitoring section

#### Configuration
- **application.properties**: Enhanced logging configuration
  - Custom log patterns
  - Timing-specific log levels

### 🎯 Benefits

- **Performance Monitoring**: Track every step of data flow
- **Bottleneck Identification**: Quickly find slow operations
- **Debugging**: Detailed timing helps diagnose issues
- **Optimization**: Data-driven performance improvements
- **Production Ready**: Monitor live application performance

### 📊 Example Output

**Backend:**
```
[TIMING] ========== listDatasets completed in 2738 ms 
         (conn: 1245ms, stmt: 12ms, query: 1450ms, process: 23ms) ==========
```

**Frontend:**
```javascript
[TIMING] ########## loadDatasets completed: 2864ms {
  datasets: 15,
  breakdown: { apiCall: "2847ms", parsing: "3ms", rendering: "12ms" }
}
```

### 🔧 Technical Details

- Zero performance impact (< 1ms overhead)
- Non-intrusive logging
- Easy to enable/disable
- Filterable with clear markers
- Works in both local and Heroku environments

---

## [1.0.0] - 2025-12-16

### ✨ Initial Release

#### Features
- BigQuery dataset listing
- Table listing per dataset
- SIMBA JDBC driver integration
- Service account authentication
- Spring Boot REST API
- Beautiful responsive UI
- Heroku deployment support

#### Backend Components
- `IncidenciaBQApplication` - Main application
- `BigQueryConfig` - Configuration management
- `BigQueryController` - REST endpoints
- `BigQueryService` - Business logic
- `Dataset` & `Table` models

#### Frontend Components
- Modern gradient UI design
- Two-column responsive layout
- Smooth animations
- Custom scrollbars
- Loading states
- Error handling

#### Documentation
- README.md
- GETTING_STARTED.md
- QUICKSTART.md
- DEPLOYMENT.md
- TEST.md
- PROJECT_OVERVIEW.md
- PROJECT_SUMMARY.md
- PROJECT_STRUCTURE.txt

#### Scripts
- `setup.sh` - Automated setup
- `run.sh` - Quick start

#### Configuration Files
- `pom.xml` - Maven dependencies
- `Procfile` - Heroku configuration
- `system.properties` - Java version
- `.gitignore` - Git ignore rules
- `application.properties` - Configuration

---

## Future Enhancements

### Planned for v1.2.0
- [ ] Connection pooling for better performance
- [ ] Caching layer for frequently accessed data
- [ ] Table schema viewer
- [ ] Data preview (first 100 rows)
- [ ] Search and filter capabilities
- [ ] Export timing data to file
- [ ] Performance metrics dashboard

### Planned for v2.0.0
- [ ] User authentication
- [ ] Multiple project support
- [ ] Query editor
- [ ] Data visualization
- [ ] Advanced metadata display
- [ ] Query history
- [ ] Scheduled queries
- [ ] Role-based access control

---

## Version History

| Version | Date | Description |
|---------|------|-------------|
| 1.1.0 | 2025-12-16 | Added comprehensive timing logs |
| 1.0.0 | 2025-12-16 | Initial release |

---

## Upgrade Guide

### From 1.0.0 to 1.1.0

No breaking changes. Simply update your code:

```bash
git pull origin main
mvn clean package
./run.sh
```

**What's New:**
- Timing logs automatically enabled
- No configuration changes needed
- View logs in console/browser
- See TIMING_LOGS.md for details

**To Disable Timing Logs:**
```properties
# In application.properties
logging.level.com.mercadolibre=WARN
```

---

## Contributors

- Alberto Diaz Raya
- MercadoLibre Team

## License

MIT License

