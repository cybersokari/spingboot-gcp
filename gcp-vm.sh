gcloud compute instances create-with-container graalvm \
    --project=gatedaccessdev \
    --zone=us-central1-a \
    --machine-type=e2-micro \
    --network-interface=network-tier=PREMIUM,stack-type=IPV4_ONLY,subnet=default \
    --maintenance-policy=MIGRATE \
    --provisioning-model=STANDARD \
    --service-account=653203556655-compute@developer.gserviceaccount.com \
    --scopes=https://www.googleapis.com/auth/cloud-platform \
    --tags=sok,http-server,https-server,lb-health-check \
    --image=projects/cos-cloud/global/images/cos-stable-113-18244-85-24 \
    --boot-disk-size=10GB \
    --boot-disk-type=pd-balanced \
    --boot-disk-device-name=graalvm \
    --container-image=us-central1-docker.pkg.dev/gatedaccessdev/cove-repo/vm:latest \
    --container-restart-policy=always \
    --no-shielded-secure-boot \
    --shielded-vtpm \
    --shielded-integrity-monitoring \
    --labels=goog-ec-src=vm_add-gcloud,container-vm=cos-stable-113-18244-85-24