# Hello World using Spring Boot, Cloud Build, and Cloud Run

## Deploy to Cloud Run

Set the `PROJECT_ID` environment variable to your Google Cloud project ID.

For example:

```text
export PROJECT_ID=tonypujals-ebook-examples
```

Update `cloudbuild.yaml` to use a different region value for the `$_REGION`
substitution variable if you want to deploy to another region than `us-central`.

Enter the following command to package the directory and send to Cloud Build.
Cloud Build will create a Docker image and push it to a Google Container Registry
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
