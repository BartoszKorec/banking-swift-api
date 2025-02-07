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

**GET**: `/v1/swift-codes/{swift-code}`

### Return all SWIFT codes with details for a specific country

**GET**: `/v1/swift-codes/country/{countryISO2code}`

###  Adds new SWIFT code entries to the database for a specific country

**POST**: `/v1/swift-codes`

#### Request Structure:

```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": "bool",
    "swiftCode": "string"
}
```

### Deletes swift-code data if swiftCode matches the one in the database

**DELETE**: `/v1/swift-codes/{swift-code}`

## Getting Started

Follow these steps to set up, run, and test the project.

## Prerequisites

- **Docker:** Ensure Docker is installed and running on your machine.  
  [Get Docker](https://docs.docker.com/get-docker/)
- No additional software is required on your host.

## Download the Project

If you don't have Git installed, you can download the entire project as a ZIP file from the root of this repository:

- [banking-swift-service.zip](banking-swift-service.zip)

### 1. Build the Docker Image

Use the Maven Wrapper to build the Docker image via Spring Boot Buildpacks:

```console
./mvnw spring:boot:build-image
```
### 2. Run the Containers

To start the containers, use the following command:

```console
docker-compose up -d
```

### 3. Stop the Containers

To stop the running containers, use the following command:

```console
docker-compose down
```

### 4. Run Tests

To run unit tests, use the following command:

```console
./mvnw test
```

To run both unit and integration tests, use the following command:

```console
./mvnw verify
```