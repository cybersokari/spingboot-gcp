# GatedAccess Web Service Repo

![coverage][coverage_badge]
## Before diving in 🙌

- Have a look into our [Contributing Guide](./.github/CONTRIBUTING.md)
- Our recommend IDE for this project is IntelliJ, but you can use any IDE that supports Springboot
- Request access to the `Firebase Developer Console` and `MongoDB Atlas console`
- Install the latest stable version of [Maven](https://maven.apache.org/docs/history.html). This will be your main version across your machine.

## Getting Started 🚀

This project contains 2 profiles:

- dev
- prod

To run the desired profile use `mvn spring-boot:run -P dev|prod`

## Setup MongoDB



### Springboot application.properties for production environment

Create a `application-prod.properties` file in `src/main/resources` directory and add the following variables with the appropriate values

```properties
spring.application.name=gated_access_service
spring.data.mongodb.auto-index-creation=true
spring.data.mongodb.uri=string
springdoc.api-docs.enabled=false
```

### Packaging for deployment
Run
```sh
$ mvn clean package
```
The following file are required in the `src/main/resources` directory, but are not available in Git for security purposes
1. `application-prod.properties` file for data url and production configs
2. `service-account.json` file for Firebase Admin SDK

## Creating Routes

Routes can be found in `src/main/***/http/controller`
There is a `BaseController.kt` abstract class that every `@RestController` can must inherit.

### Logging
This app uses [Logback](https://logback.qos.ch/manual) for logging. You can find the config in `/src/main/resources` folder
When running in `prod` Logs are sent to [Cloud Logging](https://cloud.google.com/logging), `dev` writes to console

Our current cloud setup uses Google's Ops Agent for collecting telemetry. The config.yaml in this repo is used to update the Ops Agent config. 
Follow the steps in the Google Documentation if you ever need to update the configuration

#### Deploying custom config for Google's Ops Agent
Our custom Ops Agent config can be found in the ``config.yaml`` file. Use the following command to Update the Ops Agent config  when setting up a new VM
```shell 
$ gcloud compute scp config.yaml gated-vm:/etc/google-cloud-ops-agent/config.yaml
```

## Deployments

The app runs on a Google Compute Engine VM with full GCP API permissions and required scopes

### Publishing a new version to Google Artifact Registry
You will need to have write access to our Google Artifact Registry.

1. Run the ``mvn clean package`` command to publish the new version to Google Artifact Registry
2. Run the gcloud
```shell
$ gcloud compute instances update-container [instance-name] --zone=[zone-name] --container-image=[container-image-name]
```
Replace the `instance-name`, `zone-name` and `container-image-name` with the appropriate values.

### Inspecting the app on the VM
While will not need to log into the VM to get telementry information, you can SSH into the VM and run the following command to inspect the app
1. Run ``docker ps`` to view the docker instances running
2. Run ``docker attach [container-id]`` to attach to the container and start seeing logs. Note that you will only see logs from when you attach, not the past logs.

Use Cloud logging to inspect the logs and health of the machine.





## Running Tests 🧪
### Unit test
### Integration test


---

## Folder Architecture 🚀


Here are tips on [how to optimize java application](https://cloud.google.com/run/docs/tips/java).