# Banking SWIFT Service

**Banking SWIFT Service** is a Spring Boot REST API for managing and retrieving SWIFT codes stored in a PostgreSQL database.

## Overview

Banking SWIFT Service provides a RESTful API built using Spring Boot (Java) that allows users to:
- Retrieve detailed information about SWIFT codes (both headquarters and branches).
- Query all SWIFT codes for a specific country.
- Add new SWIFT code entries.
- Delete SWIFT code data.

## Technologies Used

- **Java & Spring Boot** – Building the REST API.
- **PostgreSQL** – Storing SWIFT code data.
- **Docker & Docker Compose** – Containerizing the application and database.

## REST API Endpoints

### Retrieve details of a single SWIFT code

- **GET**: `/v1/swift-codes/{swift-code}`

### Return all SWIFT codes with details for a specific country

- **GET**: `/v1/swift-codes/country/{countryISO2code}`

###  Adds new SWIFT code entries to the database for a specific country

- **POST**: `/v1/swift-codes`

- #### Request Structure:

```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarters": "bool",
    "swiftCode": "string"
}
```

### Deletes swift-code data if swiftCode matches the one in the database

- **DELETE**: `/v1/swift-codes/{swift-code}`

# Getting Started

Follow these steps to set up, run, and test the project.

## Prerequisites

- **Java:** Ensure Java 17+ is installed and configured on your machine.  
  [Get Java 17+](https://www.oracle.com/java/technologies/downloads/)
- **Docker:** Ensure Docker is installed and running on your machine.  
  [Get Docker](https://docs.docker.com/get-docker/)
- **Git (optional):** If you prefer to clone the repository using Git.  
  [Get Git](https://git-scm.com/downloads)
- No additional software is required on your host.

## Download the Project

- **Path Constraint:** Ensure that the absolute path to the project does not contain any country-specific letters (e.g., letters with diacritics) to avoid potential issues.
- Clone the project from GitHub (If you don't have Git installed, you can download the entire project as a ZIP file).

```console
git clone https://github.com/BartoszKorec/banking-swift-api.git
```

- go to the project directory

```console
cd banking-swift-api
```

### 1. Build the Docker Image

- Make sure that Docker Engine is running on your machine.
- Use the Maven Wrapper to build the Docker image via Spring Boot Buildpacks:

```console
./mvnw org.springframework.boot:spring-boot-maven-plugin:build-image -DskipTests
```

### 2. Run the Containers

- To start the containers, use the following command:

```console
docker-compose up -d
```

### 3. Stop the Containers

- To stop the running containers, use the following command:

```console
docker-compose down
```

## Running Tests

- Make sure that Docker Engine is running on your machine (Testcontainers require up and running Docker Engine, otherwise tests will fail).
- To run unit tests, use the following command:

```console
./mvnw test
```

- To run both unit and integration tests, use the following command:

```console
./mvnw verify
```

### Code Coverage

- To check the code coverage, first run **`./mvnw verify`**. After that, open the following file in your browser:

  **`target/site/jacoco/index.html`**

## Acknowledgments

This project was developed as part of a recruitment exercise for **Remitly Poland**. The exercise description and accompanying TSV file (containing SWIFT code data) were provided by Remitly Poland. All rights to the original exercise materials remain with Remitly Poland, and they were used solely for demonstrating the application.
