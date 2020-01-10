# Recipe Book

> An app for creating and viewing recipes, making shopping lists for ingredients, and more!

[![CircleCI](https://circleci.com/gh/tjmaynes/recipe-book.svg?style=svg)](https://circleci.com/gh/tjmaynes/recipe-book)

## Requirements

- [Docker](https://hub.docker.com)

## Usage
To build the project, run the following command:
```bash
RECIPE_BOOK_DB_CONNECTION_STRING=<some-connection-string> \
make build
```

To run all tests, run the following command:
```bash
RECIPE_BOOK_DB_CONNECTION_STRING=<some-connection-string> \
make test
```

To run the database locally, run the following command:
```bash
make start_local_db
```

To seed the database, run the following command:
```bash
RECIPE_BOOK_DB_CONNECTION_STRING=<some-connection-string> \
RECIPE_BOOK_RAW_INGREDIENTS=<some-raw-json-data> \
make seed_db
```
