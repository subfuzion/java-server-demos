.PHONY: build deploy

PROJECT_ID = tonypujals-ebook-examples  # use `gcloud config get-value project`
APP        = helloworld

build:
	gcloud builds submit --tag gcr.io/$(PROJECT_ID)/helloworld

deploy:
	gcloud run deploy --image gcr.io/$(PROJECT_ID)/helloworld
