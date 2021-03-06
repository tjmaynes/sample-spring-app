IMAGE_NAME := recipe-book
RECIPE_BOOK_ENVIRONMENT := development
RECIPE_BOOK_DB_CONNECTION_STRING ?= mongodb://localhost:27017/recipe-book
RECIPE_BOOK_RAW_INGREDIENTS = `cat ./recipe-book-fixtures/ingredient.json`
REGISTRY_USERNAME := tjmaynes
REGISTRY_PASSWORD := ""
TAG := 0.1.0

seed_db:
	RECIPE_BOOK_DB_CONNECTION_STRING=$(RECIPE_BOOK_DB_CONNECTION_STRING) \
	RECIPE_BOOK_RAW_INGREDIENTS=$(RECIPE_BOOK_RAW_INGREDIENTS) \
	./gradlew clean run

build:
	RECIPE_BOOK_DB_CONNECTION_STRING=$(RECIPE_BOOK_DB_CONNECTION_STRING) \
	./gradlew clean build

test:
	RECIPE_BOOK_DB_CONNECTION_STRING=$(RECIPE_BOOK_DB_CONNECTION_STRING) \
	./gradlew clean test

remove_docker_network:
	docker network rm $(IMAGE_NAME)-network || true

setup_docker_network: remove_docker_network
	docker network create $(IMAGE_NAME)-network

remove_local_db:
	docker rm -f $(IMAGE_NAME)-db || true

start_local_db: remove_local_db setup_docker_network
	docker run -d \
		--name $(IMAGE_NAME)-db \
		--network $(IMAGE_NAME)-network \
		-p 27017:27017 \
		mongo:4.1

.PHONY: build
