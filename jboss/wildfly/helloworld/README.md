# helloworld

Build and test Java EE app with JBoss/Wildfly locally, with Docker, or
use Cloud Build to deploy to Cloud Run.

## Optional: run tests locally before building the image

```text
mvn test
```

This is useful for local edits since building an image takes time. In any
case, tests will be run when building the image.

## Launch demo locally

### Build the image

```text
docker build --platform linux/amd64 -t helloworld:demo .
```

> Note: for an M1 mac, explicitly set the platform:
> docker build --platform linux/arm64/v8 ...

### Launch a container

```text
docker run --name helloworld --rm -it -p 8080:8080 -p 9990:9990 helloworld:demo
```

Navigate to http://localhost:8080/helloworld in your browser.

### Stop the container

Press Ctrl-C, then remove the container:

```text
docker rm -f helloworld
```

## Launch demo on Cloud Run

Use the console to
[enable APIs](https://console.cloud.google.com/flows/enableapi?apiid=cloudbuild.googleapis.com,run.googleapis.com,containerregistry.googleapis.com,cloudresourcemanager.googleapis.com&redirect=https://cloud.google.com/build/docs/deploying-builds/deploy-cloud-run)
for building and running the
application on Cloud Run. Then
[enable permission](https://console.cloud.google.com/cloud-build/settings/service-account)
for Cloud Build to use the `Cloud Run Admin` role.

In your terminal, set the Google Cloud project ID for `gcloud`.

For example:

```text
gcloud config set project java-demo
```

Update `cloudbuild.yaml` to use a different region value for the `$_REGION`
substitution variable if you want to deploy to another region than `us-central`.

In the `helloworld` directory, enter the following command to upload the
contents of the directory to Cloud Build. Cloud Build will process the
Dockerfile to create a Docker image and push it to a Google Container Registry
repository in your project. Then it will launch a container on Cloud Run.

```text
gcloud builds submit
```

Use the `Service URL` printed in the build output to connect to the demo in
your browser. It should look something like this:

```text
...
Step #2: Routing traffic......done
Step #2: Done.
Step #2: Service [helloworld] revision [helloworld-00004-nuk] has been deployed and is serving 100 percent of traffic.
Step #2: Service URL: https://helloworld-wyhsobd74a-uc.a.run.app
Finished Step #2
...
DONE
...
```

## Notes on opening the Administrative Console

### First update the admin user, which is deactivated by default

```text
docker exec -it helloworld add-user.sh
```

Then follow the prompts:

```text
[jboss@eaa65a3ddd18 deployments]$ add-user.sh

What type of user do you wish to add?
 a) Management User (mgmt-users.properties)
 b) Application User (application-users.properties)
(a): a

Enter the details of the new user to add.
Using realm 'ManagementRealm' as discovered from the existing property files.
Username : admin
User 'admin' already exists and is disabled, would you like to...
 a) Update the existing user password and roles
 b) Enable the existing user
 c) Type a new username
(a): a
Password recommendations are listed below. To modify these restrictions edit the add-user.properties configuration file.
 - The password should be different from the username
 - The password should not be one of the following restricted values {root, admin, administrator}
 - The password should contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s)
Password :
Re-enter Password :
What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[  ]:
Updated user 'admin' to file '/opt/jboss/wildfly/standalone/configuration/mgmt-users.properties'
Updated user 'admin' to file '/opt/jboss/wildfly/domain/configuration/mgmt-users.properties'
Updated user 'admin' with groups  to file '/opt/jboss/wildfly/standalone/configuration/mgmt-groups.properties'
Updated user 'admin' with groups  to file '/opt/jboss/wildfly/domain/configuration/mgmt-groups.properties'
Is this new user going to be used for one AS process to connect to another AS process?
e.g. for a slave host controller connecting to the master or for a Remoting connection for server to server Jakarta Enterprise Beans calls.
yes/no? n
```

Then navigate to http://localhost:8080/console in your browser.

## Notes about the app

### App Structure

```text
helloworld
├── Dockerfile
├── Makefile
├── README.md
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── helloworld
    │   │       ├── HelloService.java
    │   │       └── HelloWorldServlet.java
    │   └── webapp
    │       ├── WEB-INF
    │       │   ├── beans.xml
    │       │   └── jboss-web.xml
    │       └── index.html
    └── test
        └── java
            └── helloworld
                └── TestHelloService.java
```

### Explanation of build files

* `Dockerfile` - the Docker command file for building the image
* `Makefile` - for convenience when building/launching locally
* `pom.xml` - the Maven project file for building the war file
* `src/main/webapp/WEB-INF/beans.xml` - marker file to enable CDI
   (contexts and dependency injection for Java EE)
* `src/main/webapp/WEB-INF/jboss-web.xml` - set webapp context root
* `index.html` - client redirect

### Explanation of Java files

* `HelloWorldServlet` - the servlet entrypoint
* `HelloService` - the service implementation injected into the servlet

### Dockerfile explanation

```Dockerfile
  1 FROM adoptopenjdk:11-jdk-hotspot as BUILD
  2 RUN curl -#L https://apache.claz.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz \
  3     | tar -C /opt/java -xzf -
  4 ENV PATH=$PATH:/opt/java/apache-maven-3.8.1/bin
  5 WORKDIR /app
  6 COPY . ./
  7 RUN mvn -e clean test install
  8
  9 FROM jboss/wildfly:24.0.0.Final
 10 RUN sed -i 's/127\.0\.0\.1/0.0.0.0/g' /opt/jboss/wildfly/standalone/configuration/standalone.xml
 11 ENV PATH=$PATH:/opt/jboss/wildfly/bin
 12 WORKDIR /opt/jboss/wildfly/standalone/deployments/
 13 COPY --from=BUILD /app/target/webapp.war ./
 ```

#### Line 1: `FROM adoptopenjdk:11-jdk-hotspot as BUILD`

This was the latest version of `adoptopenjdk` I could get to work since the
latest version of jboss/wildfly (`jboss/wildfly:24.0.0.Final`) uses a jdk
version that only supports major version 55 of a Java class file.

This is the first stage (`BUILD`) of the multistage Dockerfile.

#### Line 2-4: `RUN curl ...`

Download the latest release of
[Maven](https://maven.apache.org/docs/history.html) and put it in the path.
Make sure version is aligned with `pom.xml` (`<maven.compiler.plugin.version>`).

#### Line 5-6: `WORKDIR app`

Make `/app` the working directory (the directory doesn't need to already exist).
Copy all the files from the host Dockerfile directory (that aren't excluded
by `.dockerignore`) to the `/app` directory in the Docker build context for
the image.

#### Line 7: `RUN mvn -e clean install`

The `target` directory, if there was one on the host system, should be ignored
by the `.dockerignore`, but go ahead and run the `clean` anyway.

Next run all the unit tests.

Finally, `install` will output `webapp.war` (the name is specified in `pom.
xml`) into /opt/jboss/wildfly/standalone/deployments` in the image, where it
will be automatically loaded when the jboss/wildlfy server starts.

#### Line 9: `FROM jboss/wildfly:24.0.0.Final`

This is the second stage of the multistage Dockerfile for running the
server. It's based on the (currently) latest image for jboss/wildfly.

#### Line 10: `RUN sed ...`

There is an issue with the default server configuration that prevents it
from listening to external network connections. The default configuration
only listens to connections on `127.0.0.1`, which means when the server is
running in a container, you won't be able to connect to it from outside the
container. The `sed` command replaces `127.0.0.1` references with `0.0.0.0`,
which tells the server to listen on any IP.

I could have also created a modified copy of the configuration locally in my
project and then added a COPY instruction to the Dockerfile to overwrite the
default one in the image, or have taken the time to figure out how to
properly override the default configuration with a custom configuration,
copying that into the correct location, but it didn't seem worth the trouble.
This was expedient.

#### Line 11: `ENV PATH=$PATH:/opt/jboss/wildfly/bin`

I added wildfly `bin` to the path for convenience. Makes it easier to run
scripts like `add-user.sh`.

#### Line 12: `WORKDIR ...`

Not really necessary to change the working directory. I did it for convenience
since that's where I wanted to land by default when I ran
`docker exec -it helloworld bash`.

#### Line 13: `COPY --from=BUILD /app/target/webapp.war ./`

Copying the webapp from the build stage last to optimize use of the build cache
for this stage.
