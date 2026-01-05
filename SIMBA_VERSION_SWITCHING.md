# Simba JDBC Driver Version Switching

This project supports multiple versions of the Simba JDBC driver for Google BigQuery. You can easily switch between versions using Maven profiles.

## Available Versions

- **Current/Latest** (`GoogleBigQueryJDBC42-current.jar`) - Default, newer version (~3.7MB)
- **Version 1.6.1.1002** (`GoogleBigQueryJDBC42-1.6.1.1002.jar`) - Older version (~1.5MB)
- **Original** (`GoogleBigQueryJDBC42.jar`) - Original JAR name for backward compatibility

## How to Switch Versions

### Method 1: Using Maven Profiles (Recommended)

#### Use Current/Latest Version (Default)
```bash
mvn clean package
# or explicitly
mvn clean package -P simba-current
```

#### Use Older Version (1.6.1.1002)
```bash
mvn clean package -P simba-1.6.1
```

#### Use Original JAR Name
```bash
mvn clean package -P simba-default
```

### Method 2: Using run.sh Script

Edit the `run.sh` script to include the profile:

```bash
#!/bin/bash
mvn clean package -P simba-1.6.1  # Add the -P flag with desired profile
java -jar target/incidencia-bq-1.0.0.jar
```

### Method 3: IDE Configuration

#### IntelliJ IDEA
1. Open Maven tool window (View → Tool Windows → Maven)
2. Expand "Profiles"
3. Check the profile you want to use (simba-current, simba-1.6.1, or simba-default)
4. Rebuild the project

#### Eclipse
1. Right-click on project → Properties
2. Maven → Select "Active Maven Profiles"
3. Enter the profile name (e.g., `simba-1.6.1`)
4. Apply and rebuild

#### VS Code / Cursor
1. Open Command Palette (Cmd/Ctrl + Shift + P)
2. Type "Maven: Select Maven Profile"
3. Choose the desired profile
4. Rebuild the project

### Method 4: Permanently Change Default

To change which version is used by default, edit `pom.xml`:

```xml
<profile>
    <id>simba-1.6.1</id>
    <activation>
        <activeByDefault>true</activeByDefault>  <!-- Move this line -->
    </activation>
    <properties>
        <simba.jdbc.jar>GoogleBigQueryJDBC42-1.6.1.1002.jar</simba.jdbc.jar>
    </properties>
</profile>
```

## Adding New Versions

To add a new Simba JDBC driver version:

1. **Place the JAR file** in the `lib/` directory with a descriptive name:
   ```bash
   cp path/to/new/driver.jar lib/GoogleBigQueryJDBC42-vX.Y.Z.jar
   ```

2. **Add a new profile** in `pom.xml`:
   ```xml
   <profile>
       <id>simba-X.Y.Z</id>
       <properties>
           <simba.jdbc.jar>GoogleBigQueryJDBC42-vX.Y.Z.jar</simba.jdbc.jar>
       </properties>
   </profile>
   ```

3. **Use the new version**:
   ```bash
   mvn clean package -P simba-X.Y.Z
   ```

## Verification

To verify which version is being used:

```bash
# Check the JAR being referenced
mvn help:effective-pom | grep simba.jdbc.jar

# Or check what's in the compiled package
jar tf target/incidencia-bq-1.0.0.jar | grep GoogleBigQueryJDBC
```

## Troubleshooting

### Build fails with "cannot find symbol"
- Clean the project: `mvn clean`
- Verify the JAR exists: `ls -lh lib/GoogleBigQueryJDBC42*.jar`
- Rebuild with the correct profile

### Wrong version being used
- Check active profiles: `mvn help:active-profiles`
- Ensure you're using the `-P` flag when building
- Clear IDE caches and reimport the project

### Multiple profiles activated
Only one Simba profile should be active at a time. If multiple are activated, Maven will use the last one defined.

## Current Setup

```
lib/
├── GoogleBigQueryJDBC42.jar           # Original (3.7MB)
├── GoogleBigQueryJDBC42-current.jar   # Current/Latest (3.7MB) - DEFAULT
└── GoogleBigQueryJDBC42-1.6.1.1002.jar # Older version (1.5MB)
```

## Running the Application

After building with the desired profile, run as usual:

```bash
# Build with specific version
mvn clean package -P simba-1.6.1

# Run the application
java -jar target/incidencia-bq-1.0.0.jar

# Or use the run script (modify it to include -P flag)
./run.sh
```

## Notes

- The default profile is `simba-current` which uses the latest version
- All versions are kept in the `lib/` directory for easy switching
- The systemPath in the dependency uses a Maven property `${simba.jdbc.jar}` which is set by the active profile
- This approach doesn't require code changes, only Maven profile selection



