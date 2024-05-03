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

## Update Ops Agent config on the VM
### Grant permission
```shell
$ sudo chmod u+o /etc/google-cloud-ops-agent/
```
### Deploy custom Ops Agent Config
```shell
gcloud compute scp config.yaml [vm-name]:/etc/google-cloud-ops-agent/config.yaml
```
Replace `[vm-name]` with the name of the new VM

## Find processes using a port
```shell
$ sudo lsof -i:[port-number]
```

## Create a new tmux session 
```shell
$ tmux new -s [session-name]
```
Detach from a tmux session`Ctrl + B`, then release both keys and press `D`.
To reattach to the session run ``$ tmux attach-session``. If the is more than one session running `` $ tmux attach-session -t my_session``

## Run a java application
```shell
$ java -jar [file-path].jar
```