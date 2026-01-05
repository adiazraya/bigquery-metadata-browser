#!/bin/bash

echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║         BigQuery Service Account Permission Downgrade Script                 ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo ""

# Configuration
PROJECT_ID="ehc-alberto-diazraya-35c897"
SERVICE_ACCOUNT="datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"

echo "This script will:"
echo "  1. Check current permissions"
echo "  2. Remove bigquery.admin role (too permissive)"
echo "  3. Add minimal required roles:"
echo "     - bigquery.metadataViewer (view metadata)"
echo "     - bigquery.jobUser (run queries)"
echo ""
echo "Project: $PROJECT_ID"
echo "Service Account: $SERVICE_ACCOUNT"
echo ""

# Check if gcloud is available
if ! command -v gcloud &> /dev/null; then
    echo "❌ ERROR: gcloud CLI not found"
    echo "   Please install: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Check current permissions
echo "─────────────────────────────────────────────────────────────────────────────"
echo "📋 Step 1: Checking current permissions..."
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

CURRENT_ROLES=$(gcloud projects get-iam-policy $PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:$SERVICE_ACCOUNT" \
  --format="value(bindings.role)" 2>&1)

if [ $? -ne 0 ]; then
    echo "❌ ERROR: Failed to check permissions"
    echo "   Make sure you're authenticated: gcloud auth login"
    exit 1
fi

echo "Current roles for service account:"
echo "$CURRENT_ROLES" | while read role; do
    if [ ! -z "$role" ]; then
        if [[ "$role" == *"admin"* ]]; then
            echo "   ⚠️  $role (EXCESSIVE)"
        else
            echo "   ✓ $role"
        fi
    fi
done
echo ""

# Check if admin role exists
HAS_ADMIN=$(echo "$CURRENT_ROLES" | grep -c "bigquery.admin")

if [ "$HAS_ADMIN" -eq 0 ]; then
    echo "✅ bigquery.admin role not found (good!)"
    echo ""
    echo "Checking if minimal roles are already set..."
    HAS_METADATA=$(echo "$CURRENT_ROLES" | grep -c "bigquery.metadataViewer")
    HAS_JOBUSER=$(echo "$CURRENT_ROLES" | grep -c "bigquery.jobUser")
    
    if [ "$HAS_METADATA" -gt 0 ] && [ "$HAS_JOBUSER" -gt 0 ]; then
        echo "✅ Minimal roles already configured!"
        echo "   - bigquery.metadataViewer: YES"
        echo "   - bigquery.jobUser: YES"
        echo ""
        echo "No changes needed. Your permissions are correctly configured."
        exit 0
    else
        echo "⚠️  Missing some minimal roles:"
        echo "   - bigquery.metadataViewer: $([ "$HAS_METADATA" -gt 0 ] && echo 'YES' || echo 'NO')"
        echo "   - bigquery.jobUser: $([ "$HAS_JOBUSER" -gt 0 ] && echo 'YES' || echo 'NO')"
        echo ""
    fi
fi

# Confirm changes
echo "─────────────────────────────────────────────────────────────────────────────"
echo "⚠️  PERMISSION CHANGE REQUIRED"
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""
echo "Changes to be made:"
if [ "$HAS_ADMIN" -gt 0 ]; then
    echo "  ❌ REMOVE: roles/bigquery.admin"
fi
echo "  ✅ ADD (if missing): roles/bigquery.metadataViewer"
echo "  ✅ ADD (if missing): roles/bigquery.jobUser"
echo ""
echo "This follows the principle of least privilege."
echo ""

read -p "Proceed with permission changes? (y/n) " -n 1 -r
echo ""
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Operation cancelled."
    exit 0
fi

# Remove admin role if it exists
if [ "$HAS_ADMIN" -gt 0 ]; then
    echo "─────────────────────────────────────────────────────────────────────────────"
    echo "📝 Step 2: Removing bigquery.admin role..."
    echo "─────────────────────────────────────────────────────────────────────────────"
    echo ""
    
    gcloud projects remove-iam-policy-binding $PROJECT_ID \
      --member="serviceAccount:$SERVICE_ACCOUNT" \
      --role="roles/bigquery.admin" \
      --quiet
    
    if [ $? -eq 0 ]; then
        echo "✅ Successfully removed bigquery.admin role"
    else
        echo "❌ Failed to remove bigquery.admin role"
        echo "   You may need additional permissions"
        exit 1
    fi
    echo ""
fi

# Add metadataViewer role
echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 3: Adding bigquery.metadataViewer role..."
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/bigquery.metadataViewer" \
  --quiet

if [ $? -eq 0 ]; then
    echo "✅ Successfully added bigquery.metadataViewer role"
else
    echo "⚠️  Role may already exist or failed to add"
fi
echo ""

# Add jobUser role
echo "─────────────────────────────────────────────────────────────────────────────"
echo "📝 Step 4: Adding bigquery.jobUser role..."
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/bigquery.jobUser" \
  --quiet

if [ $? -eq 0 ]; then
    echo "✅ Successfully added bigquery.jobUser role"
else
    echo "⚠️  Role may already exist or failed to add"
fi
echo ""

# Verify final permissions
echo "─────────────────────────────────────────────────────────────────────────────"
echo "📋 Step 5: Verifying new permissions..."
echo "─────────────────────────────────────────────────────────────────────────────"
echo ""

FINAL_ROLES=$(gcloud projects get-iam-policy $PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:$SERVICE_ACCOUNT" \
  --format="value(bindings.role)")

echo "Final roles for service account:"
echo "$FINAL_ROLES" | while read role; do
    if [ ! -z "$role" ]; then
        echo "   ✓ $role"
    fi
done
echo ""

# Check if we have the required roles
HAS_METADATA=$(echo "$FINAL_ROLES" | grep -c "bigquery.metadataViewer")
HAS_JOBUSER=$(echo "$FINAL_ROLES" | grep -c "bigquery.jobUser")

if [ "$HAS_METADATA" -gt 0 ] && [ "$HAS_JOBUSER" -gt 0 ]; then
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                          ✅ SUCCESS!                                          ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Your service account now has minimal required permissions:"
    echo "  ✅ bigquery.metadataViewer - View datasets, tables, and schemas"
    echo "  ✅ bigquery.jobUser - Run INFORMATION_SCHEMA queries"
    echo ""
    echo "⏱️  IAM changes may take up to 2 minutes to propagate."
    echo ""
    echo "Next steps:"
    echo "  1. Wait 2 minutes for changes to propagate"
    echo "  2. Restart your application: ./run.sh"
    echo "  3. Test at: http://localhost:8080/jdbc-metadata.html"
    echo ""
else
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                          ⚠️  WARNING                                          ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Some required roles may be missing:"
    echo "  - bigquery.metadataViewer: $([ "$HAS_METADATA" -gt 0 ] && echo 'PRESENT' || echo 'MISSING')"
    echo "  - bigquery.jobUser: $([ "$HAS_JOBUSER" -gt 0 ] && echo 'PRESENT' || echo 'MISSING')"
    echo ""
    echo "Please verify permissions manually or re-run this script."
fi

echo "─────────────────────────────────────────────────────────────────────────────"
echo ""
echo "📚 For more information, see: BIGQUERY_PERMISSIONS_GUIDE.md"
echo ""



