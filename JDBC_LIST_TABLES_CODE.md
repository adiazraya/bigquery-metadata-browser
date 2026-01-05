# JDBC Method - List Tables Code Reference

## The Key Code

Here's the exact code that gets the list of tables in a dataset using **JDBC**:

### Location
**File**: `src/main/java/com/mercadolibre/incidenciabq/service/BigQueryJdbcService.java`

**Method**: `listTables(String datasetId)`

**Lines**: 217-349

---

## The Core Logic (Simplified)

```java
public List<Table> listTables(String datasetId) {
    List<Table> tables = new ArrayList<>();
    
    try {
        // Step 1: Get JDBC Connection
        try (Connection conn = getConnection()) {
            
            // Step 2: Build SQL Query to INFORMATION_SCHEMA
            String sql = String.format(
                "SELECT table_name, table_type, creation_time " +
                "FROM `%s.%s.INFORMATION_SCHEMA.TABLES` " +
                "ORDER BY table_name",
                projectId, datasetId
            );
            
            // Step 3: Execute SQL Query
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                // Step 4: Process Results
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    String tableType = rs.getString("table_type");
                    Timestamp creationTime = rs.getTimestamp("creation_time");
                    
                    // Create Table object
                    Table table = new Table();
                    table.setTableId(tableName);
                    table.setDatasetId(datasetId);
                    table.setProjectId(projectId);
                    table.setType(tableType);
                    if (creationTime != null) {
                        table.setCreationTime(creationTime.getTime());
                    }
                    
                    tables.add(table);
                }
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Failed to list tables: " + datasetId, e);
    }
    
    return tables;
}
```

---

## The Critical SQL Query

This is the **key piece** - the SQL query executed via JDBC:

```sql
SELECT table_name, table_type, creation_time 
FROM `{project}.{dataset}.INFORMATION_SCHEMA.TABLES` 
ORDER BY table_name
```

**Example for your project**:
```sql
SELECT table_name, table_type, creation_time 
FROM `ehc-alberto-diazraya-35c897.ProcessEfficiency.INFORMATION_SCHEMA.TABLES` 
ORDER BY table_name
```

### What This Query Does:
1. Queries BigQuery's **INFORMATION_SCHEMA** system catalog
2. Gets all tables in the specified dataset
3. Returns: table name, type (TABLE/VIEW/etc.), and creation time
4. Orders results by table name alphabetically

---

## Code Breakdown - Step by Step

### Step 1: Get JDBC Connection (Lines 243-245)

```java
try (Connection conn = getConnection()) {
```

This calls the `getConnection()` method which:
- Creates JDBC URL with service account credentials
- Connects to BigQuery via Simba JDBC driver
- Returns a `java.sql.Connection` object

### Step 2: Build SQL Query (Lines 256-261)

```java
String sql = String.format(
    "SELECT table_name, table_type, creation_time " +
    "FROM `%s.%s.INFORMATION_SCHEMA.TABLES` " +
    "ORDER BY table_name",
    projectId, datasetId
);
```

**Key Points**:
- Uses `INFORMATION_SCHEMA.TABLES` - BigQuery's metadata catalog
- Backticks required: `` `project.dataset.INFORMATION_SCHEMA.TABLES` ``
- Selects 3 columns: table_name, table_type, creation_time
- Orders alphabetically by table name

### Step 3: Execute Query (Lines 266-267)

```java
try (PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
```

**What Happens**:
1. `prepareStatement(sql)` - Creates prepared statement
2. `executeQuery()` - Executes SQL and returns results
3. Returns `ResultSet` with query results

**Behind the Scenes** (via Simba JDBC Driver):
- JDBC driver converts SQL to BigQuery API call
- Sends query as a BigQuery job
- Waits for job completion
- Returns results as JDBC ResultSet

### Step 4: Process Results (Lines 279-316)

```java
while (rs.next()) {
    // Extract data from ResultSet
    String tableName = rs.getString("table_name");
    String tableType = rs.getString("table_type");
    Timestamp creationTime = rs.getTimestamp("creation_time");
    
    // Create Table object
    Table table = new Table();
    table.setTableId(tableName);
    table.setDatasetId(datasetId);
    table.setProjectId(projectId);
    table.setType(tableType);
    if (creationTime != null) {
        table.setCreationTime(creationTime.getTime());
    }
    
    // Add to list
    tables.add(table);
}
```

**Process**:
- Iterates through each row in ResultSet
- Extracts column values using `getString()`, `getTimestamp()`
- Creates Java `Table` object for each row
- Adds to ArrayList
- Returns list of all tables

---

## Comparison: JDBC vs REST API

### JDBC Method (This Code)

```java
// Build SQL query
String sql = "SELECT table_name, table_type, creation_time " +
             "FROM `project.dataset.INFORMATION_SCHEMA.TABLES` " +
             "ORDER BY table_name";

// Execute via JDBC
PreparedStatement stmt = conn.prepareStatement(sql);
ResultSet rs = stmt.executeQuery();

// Process ResultSet
while (rs.next()) {
    String tableName = rs.getString("table_name");
    // ...
}
```

**Protocol**: SQL query → JDBC → HTTP → BigQuery API

### REST API Method (Alternative)

```java
// Direct API call
DatasetId dataset = DatasetId.of(projectId, datasetId);
Page<Table> tablePage = bigQueryClient.listTables(dataset);

// Process results
for (Table table : tablePage.iterateAll()) {
    String tableName = table.getTableId().getTable();
    // ...
}
```

**Protocol**: Java API → HTTP → BigQuery API

---

## Under the Hood: What the Simba JDBC Driver Does

When you execute the SQL query via JDBC:

```
Your Code: stmt.executeQuery()
     ↓
Simba JDBC Driver receives SQL query
     ↓
Driver translates SQL to BigQuery API request
     ↓
HTTP POST to BigQuery API:
  POST https://bigquery.googleapis.com/bigquery/v2/projects/{project}/jobs
  Body: {
    "query": "SELECT table_name... FROM INFORMATION_SCHEMA.TABLES",
    "useLegacySql": false
  }
     ↓
BigQuery creates query job
     ↓
Driver polls job status until complete
     ↓
Driver fetches results
     ↓
Converts results to JDBC ResultSet format
     ↓
Returns to your code
```

---

## The SQL Query in Detail

### Full Query Structure

```sql
SELECT 
    table_name,        -- Name of the table
    table_type,        -- TABLE, VIEW, EXTERNAL, MATERIALIZED_VIEW
    creation_time      -- Unix timestamp of creation
FROM 
    `{project}.{dataset}.INFORMATION_SCHEMA.TABLES`
ORDER BY 
    table_name
```

### INFORMATION_SCHEMA.TABLES Columns Available

Your code selects 3 columns, but many more are available:

```sql
-- All available columns in INFORMATION_SCHEMA.TABLES
table_catalog         -- Project ID
table_schema          -- Dataset ID
table_name            -- Table name
table_type            -- Type of table
is_insertable_into    -- Can insert rows
is_typed              -- Is typed table
creation_time         -- Creation timestamp
base_table_catalog    -- For views
base_table_schema     -- For views
base_table_name       -- For views
snapshot_time_ms      -- For snapshots
ddl                   -- CREATE TABLE statement
```

### Why This Query?

You're selecting the **minimum necessary** columns:
- `table_name` - Required to identify the table
- `table_type` - To distinguish TABLE vs VIEW vs EXTERNAL
- `creation_time` - Nice to have for sorting/filtering

This keeps the query fast and efficient!

---

## Where This Code Is Called From

### Controller Layer

**File**: `src/main/java/com/mercadolibre/incidenciabq/controller/BigQueryJdbcController.java`

```java
@GetMapping("/api/jdbc/datasets/{datasetId}/tables")
public ResponseEntity<List<Table>> listTables(@PathVariable String datasetId) {
    List<Table> tables = bigQueryJdbcService.listTables(datasetId);
    return ResponseEntity.ok(tables);
}
```

### HTTP Request

```
GET http://localhost:8080/api/jdbc/datasets/ProcessEfficiency/tables
```

### Flow

```
Browser/Client
    ↓ HTTP GET
BigQueryJdbcController.listTables()
    ↓ Java method call
BigQueryJdbcService.listTables(datasetId)
    ↓ JDBC connection
getConnection()
    ↓ SQL query
"SELECT ... FROM INFORMATION_SCHEMA.TABLES"
    ↓ Simba JDBC Driver
HTTP to BigQuery API
    ↓ Results
ResultSet → Table objects → JSON response → Browser
```

---

## Complete Code Reference

**Exact lines in BigQueryJdbcService.java**:

- **Lines 256-261**: SQL query construction
- **Lines 266-267**: SQL execution
- **Lines 279-316**: ResultSet processing
- **Lines 306-315**: Table object creation

### Full Method Signature

```java
public List<Table> listTables(String datasetId)
```

**Location**: `src/main/java/com/mercadolibre/incidenciabq/service/BigQueryJdbcService.java:217-349`

---

## Quick Reference

**THE CORE CODE** - This is what you're looking for:

```java
String sql = String.format(
    "SELECT table_name, table_type, creation_time " +
    "FROM `%s.%s.INFORMATION_SCHEMA.TABLES` " +
    "ORDER BY table_name",
    projectId, datasetId
);

try (PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    
    while (rs.next()) {
        String tableName = rs.getString("table_name");
        // Create Table object and add to list
    }
}
```

**That's it!** This is the exact code that gets tables using JDBC.

---

## Summary

✅ **File**: `BigQueryJdbcService.java`
✅ **Method**: `listTables(String datasetId)`
✅ **Key Code**: Lines 256-316
✅ **SQL Query**: `SELECT ... FROM INFORMATION_SCHEMA.TABLES`
✅ **Execution**: Via JDBC PreparedStatement
✅ **Results**: Processed from ResultSet into List<Table>

