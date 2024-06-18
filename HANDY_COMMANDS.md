# Some handy commands to help you during development

Access the VM via SSH with Google Cloud CLI
```shell
$ gcloud compute ssh [vm-name]
```

Pull docker image from Artifact Registry
```shell
$ docker pull [host-name]/[project-id]/[repository]/[image-name]
```

Run a container on your machine 
```shell
$ docker run -ti --rm -p 8080:80 gcr.io/[project-id]/[image-name]
```

### Add tag to Compute Engine VM
```shell
$ gcloud compute instances add-tags [vm-name] --tags=[tag-name]  
```
### Create Firewall rule for access to the VM
```shell
gcloud compute firewall-rules create [rule-name] \
  --project=[project-id] \
  --network=[network-name]  \     
  --source-ranges=0.0.0.0/0 \
  --target-tags=[vm-tag-name] \
  --allow=tcp:[port-number]
```
## Find processes using a port
```shell
$ sudo lsof -i:[port-number]
```