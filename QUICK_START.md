# Quick Start Guide

## 🚀 Your Application is Already Running!

The application is running on **http://localhost:8080/**

## 📱 Three Ways to Access

### 1. Main Menu (Recommended)
```
http://localhost:8080/
```
Beautiful landing page with two options:
- **⚡ Metadata Info with API** (Working now)
- **🔌 Metadata Info with JDBC** (Needs SIMBA driver)

### 2. Direct REST API Access
```
http://localhost:8080/api-metadata.html
```
✅ Fully functional - browse datasets and tables immediately

### 3. Direct JDBC Access
```
http://localhost:8080/jdbc-metadata.html
```
⚠️ Requires SIMBA driver (see [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md))

## 🎯 What Works Right Now

✅ **REST API Version** - Everything works!
- List all BigQuery datasets
- View tables in each dataset
- Detailed performance logging
- All logs in `logs/incidencia-bq.log`

⚠️ **JDBC Version** - Infrastructure ready, needs driver
- Backend code complete
- Frontend UI complete
- Will show clear error without driver
- Installation guide available

## 📊 API Endpoints

### Working Endpoints (REST API)
```bash
# List all datasets
curl http://localhost:8080/api/bigquery/datasets

# List tables in a dataset
curl http://localhost:8080/api/bigquery/datasets/YOUR_DATASET/tables
```

### JDBC Endpoints (Requires SIMBA)
```bash
# List all datasets via JDBC
curl http://localhost:8080/api/bigquery-jdbc/datasets

# List tables via JDBC
curl http://localhost:8080/api/bigquery-jdbc/datasets/YOUR_DATASET/tables
```

## 📝 Where Are the Logs?

All detailed logs are in: **`logs/incidencia-bq.log`**

This includes:
- API call timings
- Query parameters
- Response processing details
- Performance breakdowns
- Error messages (if any)

Console only shows minimal INFO messages.

## 🔧 Common Tasks

### View Logs
```bash
tail -f logs/incidencia-bq.log
```

### Stop the Server
Press `Ctrl+C` in the terminal where it's running

### Restart the Server
```bash
./run.sh
```

### Rebuild After Changes
```bash
mvn clean install
./run.sh
```

## 📚 Need More Information?

- **Main Documentation**: [README_NEW_STRUCTURE.md](./README_NEW_STRUCTURE.md)
- **SIMBA Installation**: [SIMBA_INSTALLATION.md](./SIMBA_INSTALLATION.md)
- **Implementation Details**: [IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)
- **Deployment**: [DEPLOYMENT.md](./DEPLOYMENT.md)

## 🎉 Quick Test

1. Open browser: http://localhost:8080/
2. Click "Metadata Info with API"
3. See your datasets load
4. Click any dataset
5. See tables appear

That's it! You're done! 🚀

---

**Next Step**: Explore the application or optionally install SIMBA for JDBC comparison.






