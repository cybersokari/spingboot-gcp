# Cove Web Service Repo

![Deployment](https://github.com/sprinthubmobile/cove_web/actions/workflows/deploy.yml/badge.svg?branch=main)
![Test](https://github.com/sprinthubmobile/cove_web/actions/workflows/test.yml/badge.svg)
## Before diving in 🙌

- Our recommend IDE for this project is IntelliJ, but you can use any IDE that supports Springboot
- Request access to the `Google Cloud Developer Console` and `MongoDB Atlas console`
- Install the latest stable version of [Maven](https://maven.apache.org/docs/history.html). This will be your main version across your machine.

## Getting Started 🚀

This project contains 2 profiles:

- dev
- prod

To run the desired profile use `mvn spring-boot:run -P dev|prod`

### Setup MongoDB


### Creating Routes
Routes can be found in `src/main/***/http/controller`
There is a `BaseController.kt` abstract class that every `@RestController` can must inherit.

### Creating Mongo Documents and Repositories
Documents can be found in `src/main/***/model`
Repositories can be found in `src/main/***/repository`


### Logging
This app uses [Logback](https://logback.qos.ch/manual) for logging. You can find the config in `/src/main/resources` folder
When running in `prod` Logs are sent to [Cloud Logging](https://cloud.google.com/logging) via [Logback Cloud logging plugin](https://cloud.google.com/logging/docs/setup/java).
The Logback plugin only reports logs from the LF4J logging API, so we only use the `org.slf4j.Logger` interface for logging.
When running in `dev` profile, logs are configured to write to the console.


Use this [setup to configure Docker](https://docs.docker.com/config/containers/logging/gcplogs/) to work with Cloud Logging when moving to a new VM

#### Deploying custom config for Google's Ops Agent
Our custom Ops Agent config can be found in the ``config.yaml`` file. Use the following command to Update the Ops Agent config  when setting up a new VM
```shell 
$ gcloud compute scp config.yaml gated-vm:/etc/google-cloud-ops-agent/config.yaml
```

### Secrets Management 🔒
We use Google Cloud Secrets Manager to manage secrets (API keys, passwords, database URLs, etc.)

### Deployments

Deployments are currently automated via GitHub actions. The workflow file is located at ``/.github/workflows/deploy.yml``

The app runs on a Google Compute Engine VM with full GCP API permissions and required scopes

### Publishing a new version to Google Artifact Registry
You will need to have write access to our Google Artifact Registry on Google Cloud Platform and install docker on your machine.
1. Run ``gcloud auth configure-docker us-central1-docker.pkg.dev`` to enable [Google Cloud CLI to authenticate requests to Artifact Registry](https://cloud.google.com/artifact-registry/docs/docker/store-docker-container-images#linux).
2. Run the ``mvn clean package`` command to publish the new version to Google Artifact Registry. The required repository path is
configured in the [Jib Maven plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) in pom.xml file


### Updating the container image with the new image version
It takes the Compute Engine VM 20 to 30 seconds to update the container image. So we try not to run the following command
at peak hours of the day if possible.
```shell
$ gcloud compute instances update-container [instance-name] --zone=[zone-name] --container-image=[container-image-name]
```
Replace the `instance-name`, `zone-name` and `container-image-name` with the appropriate values.

### Inspecting the app on the VM
While will not need to log into the VM to get Telementry information, you can SSH into the VM and run the following command to inspect the app
1. Run ``docker ps`` to view the docker instances running
2. Run ``docker attach [container-id]`` to attach to the container and start seeing logs. Note that you will only see logs from when you attach, not the past logs.

Use Cloud logging to inspect the logs and health of the machine.

### Running the production docker image on local machine
1. Install and start docker daemon
2. Run ``mvn compile jib:dockerBuild`` to build the docker image
3. Temporarily allow the production Mongo Atlas to accept traffic from anywhere on the internet.
4. Install Gcloud CLI and authenticate it ``gcloud auth application-default login``
5. Run ``docker-compose up`` from the project root folder.


### Tests 🧪
#### Unit test
#### Integration test

---

Here are some tips on [how to optimize java application](https://cloud.google.com/run/docs/tips/java).
