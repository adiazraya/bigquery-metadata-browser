#!/bin/bash

# Script to check BigQuery query history using gcloud command
# This works even if audit logs are not enabled in Cloud Logging

echo "======================================"
echo "Checking BigQuery Query History"
echo "======================================"
echo ""

# Set the project
export PROJECT_ID="ehc-alberto-diazraya-35c897"

echo "Fetching recent BigQuery jobs..."
echo ""

# List recent BigQuery jobs
gcloud alpha bq jobs list \
  --project="${PROJECT_ID}" \
  --format="table(job_id,user_email,creation_time,state,job_type)" \
  --limit=20

echo ""
echo "======================================"
echo "To see details of a specific job, run:"
echo "gcloud alpha bq jobs describe JOB_ID --project=${PROJECT_ID}"
echo "======================================"






