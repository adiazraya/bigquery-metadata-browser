# 🔍 Monitoring Quick Start

Since you can't access Google Cloud Console logs, here's how to monitor your BigQuery application.

---

## ⚡ Quick Commands

### 1. **Live Monitoring Dashboard** (RECOMMENDED)
```bash
./monitor_live.sh
```
Shows real-time activity, response times, and errors. Refreshes every 5 seconds.

### 2. **Daily Summary Report**
```bash
./daily_report.sh
```
Generates a comprehensive report of today's activity.

### 3. **Test Application Status**
```bash
./test_bigquery_access.sh
```
Tests if both API and JDBC are working and shows response times.

### 4. **Watch Live Logs**
```bash
tail -f logs/incidencia-bq.log
```
See all activity as it happens.

### 5. **Watch Only Important Events**
```bash
tail -f logs/incidencia-bq.log | grep "TIMING\|PAGINATION\|ERROR"
```
Filters to show only timing info, pagination events, and errors.

---

## 📊 Analysis Commands

### Check Today's Activity
```bash
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep "Received GET" | wc -l
```

### Find Slowest Operations
```bash
grep "completed in" logs/incidencia-bq.log | grep -oP '\d+ ms' | sort -rn | head -10
```

### Count Errors
```bash
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep -i error | wc -l
```

### See Pagination Events
```bash
grep "PAGINATION EVENT" logs/incidencia-bq.log | tail -20
```

---

## 🎯 What Each Method Shows

| Method | What It Monitors |
|--------|------------------|
| **Application Logs** | Everything: requests, queries, timings, errors |
| **Google Cloud Console** | ❌ Not accessible (permission denied) |
| **Live Monitor Script** | Real-time dashboard of activity |
| **Daily Report** | Summary statistics for the day |
| **Test Script** | Quick health check |

---

## 📈 Understanding the Logs

### Log Levels
- `[INFO]` - Normal operation
- `[TIMING]` - Performance metrics
- `[DETAIL]` - Detailed operation logs
- `[WARN]` - Warnings (like pagination delays)
- `[ERROR]` - Errors

### Important Markers
- `[TIMING]` - Response time measurements
- `[PAGINATION EVENT]` - API had to fetch another page (slow!)
- `[DETAIL][JDBC]` - JDBC/SQL operations
- `Received GET` - New HTTP request received
- `completed in X ms` - Operation finished

---

## 🚀 Example Workflow

### Morning Check
```bash
# Start the app
./run.sh

# In another terminal, watch activity
./monitor_live.sh
```

### During the Day
```bash
# Keep logs visible
tail -f logs/incidencia-bq.log | grep TIMING
```

### End of Day
```bash
# Generate summary
./daily_report.sh > reports/report-$(date +%Y%m%d).txt
```

---

## 🔧 Troubleshooting

### Application Not Responding?
```bash
# Check if it's running
curl http://localhost:8080/api/bigquery/datasets

# If not, restart it
./run.sh
```

### Too Many Logs?
```bash
# Archive old logs
tar -czf logs/archive-$(date +%Y%m%d).tar.gz logs/*.log
# Keep only last 10,000 lines
tail -10000 logs/incidencia-bq.log > logs/temp.log
mv logs/temp.log logs/incidencia-bq.log
```

### Want to See Specific Dataset Activity?
```bash
grep "datasets/YOUR_DATASET_NAME/tables" logs/incidencia-bq.log
```

---

## 📝 Log File Locations

- **Application logs**: `logs/incidencia-bq.log`
- **All monitoring scripts**: `./*.sh`
- **Documentation**: `./MONITORING_GUIDE.md`

---

## 🎓 Pro Tips

1. **Keep monitor running**: Run `./monitor_live.sh` in a dedicated terminal
2. **Generate daily reports**: Add to cron for automated reports
3. **Watch for pagination**: High pagination = slow API performance
4. **Compare API vs JDBC**: Use timing logs to see which is faster
5. **Archive logs regularly**: Prevent disk space issues

---

## 📞 Quick Reference Card

```
┌─────────────────────────────────────────────────┐
│  MONITORING COMMAND REFERENCE                   │
├─────────────────────────────────────────────────┤
│  Live Monitor:     ./monitor_live.sh            │
│  Daily Report:     ./daily_report.sh            │
│  Health Check:     ./test_bigquery_access.sh    │
│  Watch Logs:       tail -f logs/incidencia-bq.log│
│  Start App:        ./run.sh                     │
│  Stop App:         lsof -ti:8080 | xargs kill   │
└─────────────────────────────────────────────────┘
```

---

**Remember**: Your application logs contain EVERYTHING you need. You don't need Google Cloud Console access to monitor your application effectively!



