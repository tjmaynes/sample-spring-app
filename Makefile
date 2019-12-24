IMAGE_NAME := recipe-book
RECIPE_BOOK_ENVIRONMENT := development
RECIPE_BOOK_DB_CONNECTION_STRING := hello
REGISTRY_USERNAME := tjmaynes
REGISTRY_PASSWORD := ""
TAG := 0.1.0

build:
	./gradlew build

test:
	./gradlew test

setup_docker_network:
	(docker network rm $(IMAGE_NAME)-network || true) && docker network create $(IMAGE_NAME)-network

start_local_db: setup_docker_network
	(docker rm -f $(IMAGE_NAME)-db|| true) && docker run -d \
		--name $(IMAGE_NAME)-db \
		-e POSTGRES_USER=postgres \
		-e POSTGRES_PASSWORD=password \
		-p 5432:5432 \
		postgres:9.5.14-alpine

.PHONY: build