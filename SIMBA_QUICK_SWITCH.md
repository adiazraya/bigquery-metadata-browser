# Quick Guide: Switching Simba JDBC Driver Versions

## Three Easy Ways to Switch

### 1. **Easiest: Use the helper script**
```bash
./switch_simba.sh 1.6.1      # Use older version
./switch_simba.sh current    # Use latest version
```

### 2. **Quick: Build with Maven profile**
```bash
mvn clean package -P simba-1.6.1     # Use older version
mvn clean package -P simba-current   # Use latest version (default)
```

### 3. **Running: Use modified run script**
```bash
./run.sh simba-1.6.1     # Build and run with older version
./run.sh                 # Build and run with current version
```

## Available Versions

| Profile | Version | File | Size |
|---------|---------|------|------|
| `simba-current` | Latest | `GoogleBigQueryJDBC42-current.jar` | 3.7MB |
| `simba-1.6.1` | 1.6.1.1002 | `GoogleBigQueryJDBC42-1.6.1.1002.jar` | 1.5MB |
| `simba-default` | Original | `GoogleBigQueryJDBC42.jar` | 3.7MB |

## Examples

```bash
# Test with older version
./switch_simba.sh 1.6.1
java -jar target/incidencia-bq-1.0.0.jar

# Switch back to current version
./switch_simba.sh current
java -jar target/incidencia-bq-1.0.0.jar

# Or use run.sh directly with profile
./run.sh simba-1.6.1
```

## More Details

For complete documentation, see [SIMBA_VERSION_SWITCHING.md](SIMBA_VERSION_SWITCHING.md)



