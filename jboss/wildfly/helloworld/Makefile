.PHONY: build run

IMAGE      = hellworld:demo
CONTAINER  = helloworld
PLATFORM   = linux/arm64/v8  # on mac m1, set as appropriate (linux/amd64, etc)

build:
	docker build --platform $(PLATFORM) -t $(IMAGE) .

run: build
	 docker run --name $(CONTAINER) --rm -it -p 8080:8080 -p 9990:9990 $(IMAGE)
