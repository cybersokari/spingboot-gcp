# Copy the default Google Cloud application default credentials.json file to the project directory
cp "$HOME"/.config/gcloud/application_default_credentials.json ./credentials.json
# Read the GCLOUD_PROJECT from the credentials.json file using Python
GCLOUD_PROJECT=$(python3 -c "import sys, json; print(json.load(open('credentials.json'))['quota_project_id'])")
export GCLOUD_PROJECT
# Start the app
docker-compose up jvm