.PHONY: build run

IMAGE      = hellworld:demo
CONTAINER  = helloworld
PLATFORM   = linux/amd64  # on mac m1 use: linux/arm64/v8

build:
	docker build --platform $(PLATFORM) -t $(IMAGE) .

run: build
	 docker run --name $(CONTAINER) --rm -it -p 8080:8080 -p 9990:9990 $(IMAGE)
