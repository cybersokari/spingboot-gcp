# How to set up a new Compute Engine for this project

Follow the steps in [Google Doc](https://cloud.google.com/compute/docs/instances/create-start-instance#gcloud) to set up a new Compute Engine VM


After setting up the vm, use the Google CLI log into the vm with SSH
```shell
$ gcloud compute ssh gated-vm
```
### Install Java
```shell
$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
```
### Install Ops Agent on the VM
Use the Google Doc guide to install Ops agent. After that, use the following command to grant write permissions for the deployment 
of a custom Ops Agent `config.yaml` file that can be found in this repo.

Grant permission
```shell
$ sudo chmod u+o /etc/google-cloud-ops-agent/
```
Deploy custom Ops Agent Config
```shell
gcloud compute scp config.yaml [vm-name]:/etc/google-cloud-ops-agent/config.yaml
```
Replace `[vm-name]` with the name of the new VM


## Build and upload to Compute engine
```shell
$ mvn clean package

$ gcloud compute scp target/gated_access_service-0.0.1-SNAPSHOT.jar gated-vm:.
```

## SSH into vm and install Java 17
```shell
$ gcloud compute ssh gated-vm

$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
```

## Find Java Process
```shell
$ kill $(pgrep java)
```

## Find processes using a port
```shell
$ sudo lsof -i:8080
```

## SSH into VM and run app
```shell
$ gcloud compute ssh gated-vm

$ nohup java -jar opt/spring/gated_access_service-0.0.1-SNAPSHOT.jar &
```

## How to run the app on a VM

### Step 1
Create a new tmux session 
```shell
$ tmux new -s webapp
```

### Step 2
Run the app
```shell
$ java -jar application.jar
```

### Step 3
Detach from the tmux session by pressing `Ctrl + B`, then release both keys and press `D`. 

To reattach to the session run ``$ tmux attach-session``. If the is more than one session running `` $ tmux attach-session -t my_session``