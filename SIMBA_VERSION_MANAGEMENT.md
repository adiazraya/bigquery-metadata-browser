# Simba JDBC Driver Version Management - Implementation Summary

## What Was Done

I've implemented a flexible system for switching between different versions of the Simba JDBC driver for Google BigQuery. This allows you to easily test and compare different driver versions without modifying code or manually swapping JAR files.

## Changes Made

### 1. **Maven POM Configuration** (`pom.xml`)
- Added a `simba.jdbc.jar` property that defaults to `GoogleBigQueryJDBC42-current.jar`
- Created three Maven profiles:
  - `simba-current`: Latest version (default, active by default)
  - `simba-1.6.1`: Older version 1.6.1.1002
  - `simba-default`: Original JAR name for backward compatibility
- Modified the system dependency to use `${simba.jdbc.jar}` property

### 2. **JAR File Organization** (`lib/` directory)
- `GoogleBigQueryJDBC42.jar` - Original file (3.7MB)
- `GoogleBigQueryJDBC42-current.jar` - Current/latest version (3.7MB)
- `GoogleBigQueryJDBC42-1.6.1.1002.jar` - Older version (1.5MB)

### 3. **Helper Scripts**

#### `switch_simba.sh` (NEW)
A convenient script to switch versions with a single command:
```bash
./switch_simba.sh current    # Use latest version
./switch_simba.sh 1.6.1      # Use older version
./switch_simba.sh default    # Use original JAR
```

Features:
- Cleans and rebuilds the project
- Verifies the correct JAR is included
- Provides clear success/failure messages
- Accepts multiple aliases (current, latest, old, older, etc.)

#### `run.sh` (MODIFIED)
Enhanced to accept a profile parameter:
```bash
./run.sh                     # Use default (current)
./run.sh simba-1.6.1         # Use specific profile
```

Changes:
- Accepts optional Maven profile as first argument
- Rebuilds if profile is specified
- Displays available profiles on startup

### 4. **Documentation**

#### `SIMBA_VERSION_SWITCHING.md` (NEW)
Comprehensive guide covering:
- All methods to switch versions
- Maven profile usage
- IDE configuration (IntelliJ, Eclipse, VS Code)
- How to add new versions
- Troubleshooting guide
- Verification commands

#### `SIMBA_QUICK_SWITCH.md` (NEW)
Quick reference guide with:
- Three easy methods to switch
- Available versions table
- Example commands
- Link to detailed documentation

## How to Use

### Simple Method (Recommended)
```bash
# Switch to older version and build
./switch_simba.sh 1.6.1

# Run the application
java -jar target/incidencia-bq-1.0.0.jar
```

### Maven Method
```bash
# Build with specific version
mvn clean package -P simba-1.6.1

# Run
java -jar target/incidencia-bq-1.0.0.jar
```

### Integrated Method
```bash
# Build and run in one command
./run.sh simba-1.6.1
```

## Testing Different Versions

To test if a specific version fixes an issue:

```bash
# Test with current version
./switch_simba.sh current
./run.sh
# Test your functionality

# Test with older version
./switch_simba.sh 1.6.1
./run.sh
# Test again with older version
```

## Verifying Active Version

```bash
# Check which JAR is being used
mvn help:effective-pom | grep simba.jdbc.jar

# Verify JAR in compiled package
jar tf target/incidencia-bq-1.0.0.jar | grep GoogleBigQuery
```

## Adding New Versions

1. Place new JAR in `lib/` directory:
   ```bash
   cp /path/to/new/driver.jar lib/GoogleBigQueryJDBC42-vX.Y.Z.jar
   ```

2. Add profile to `pom.xml`:
   ```xml
   <profile>
       <id>simba-X.Y.Z</id>
       <properties>
           <simba.jdbc.jar>GoogleBigQueryJDBC42-vX.Y.Z.jar</simba.jdbc.jar>
       </properties>
   </profile>
   ```

3. Use it:
   ```bash
   mvn clean package -P simba-X.Y.Z
   ```

## Benefits

1. **No Code Changes**: Switch versions without modifying Java code
2. **Easy Testing**: Quickly test different driver versions to identify issues
3. **Version Control**: All versions kept in source control
4. **Reproducible Builds**: Specify exact version in build commands
5. **CI/CD Ready**: Can specify profile in deployment pipelines
6. **Backward Compatible**: Original JAR name still works

## Files Modified/Created

- ✏️ `pom.xml` - Added profiles and property
- ✏️ `run.sh` - Enhanced with profile support
- ✨ `switch_simba.sh` - New helper script
- ✨ `SIMBA_VERSION_SWITCHING.md` - Detailed documentation
- ✨ `SIMBA_QUICK_SWITCH.md` - Quick reference
- ✨ `lib/GoogleBigQueryJDBC42-current.jar` - Current version copy
- ✨ `lib/GoogleBigQueryJDBC42-1.6.1.1002.jar` - Older version extracted

## Current Status

✅ Maven profiles configured and tested
✅ All JAR files in place
✅ Helper scripts created and executable
✅ Documentation complete
✅ Backward compatible with existing setup

## Next Steps (Optional)

1. Test both versions to confirm they work correctly
2. Update CI/CD pipelines if needed to specify profile
3. Add more versions as needed
4. Consider adding version detection/display in the application logs

## Example Workflow

```bash
# Initial setup - already done!

# Day-to-day usage:

# Morning: Test with current version
./switch_simba.sh current
./run.sh
# Access http://localhost:8080

# Found an issue? Try older version:
./switch_simba.sh 1.6.1
./run.sh
# Test again

# Need to deploy? Specify profile:
mvn clean package -P simba-current
# or
mvn clean package -P simba-1.6.1
```

## Support

For detailed usage instructions, see:
- `SIMBA_QUICK_SWITCH.md` - Quick reference
- `SIMBA_VERSION_SWITCHING.md` - Complete guide
- `SIMBA_VS_API_EXPLANATION.md` - Driver vs API explanation

---

**Created**: December 19, 2024
**Author**: Implementation for MercadoLibre IncidenciaBQ Project



