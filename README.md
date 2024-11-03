
# Second-Hand Marketplace

## Overview
This project is a second-hand clothing marketplace built with Spring Boot, enabling users to register, authenticate, and manage garment listings.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Logging](#logging)
- [Swagger Documentation](#swagger-documentation)
- [License](#license)

## Features
- User registration and authentication.
- JWT-based authentication for secure access.
- CRUD operations for garments (create, read, update, delete).
- Custom exception handling.
- API documentation using Swagger.

## Technologies Used
- **Spring Boot**: Framework for building the REST API.
- **Spring Security**: For securing endpoints and managing authentication.
- **JWT (JSON Web Tokens)**: For secure token-based authentication.
- **H2 Database**: In-memory database for development and testing.
- **Maven**: Build tool for managing project dependencies.
- **ModelMapper**: For object mapping between DTOs and entities.
- **JUnit 5 & Mockito**: For unit testing.

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/second-hand-marketplace.git
   cd second-hand-marketplace
   ```
2. Make sure you have Maven installed.
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints
### User Registration
- **POST** `/api/register`
    - **Request Body**:
      ```json
      {
        "username": "user",
        "password": "password",
        "fullName": "Full Name",
        "address": "Address"
      }
      ```
    - **Response**:
        - Status 200: "User registered successfully"

### Authentication
- **POST** `/api/authenticate`
    - **Request Body**:
      ```json
      {
        "username": "user",
        "password": "password"
      }
      ```
    - **Response**:
        - Status 200:
          ```json
          {
            "token": "your_jwt_token",
            "expirationTime": 3600000
          }
          ```
        - Status 401: "Invalid username or password."

### Garment Management
- **GET** `/api/clothes`
    - **Response**: List of garments.

- **GET** `/api/clothes/{id}`
    - **Response**: Details of a garment.

- **POST** `/api/clothes/add`
    - **Request Body**:
      ```json
      {
        "type": "Shirt",
        "description": "Nice shirt",
        "size": "M",
        "price": 25.00
      }
      ```
    - **Response**: Status 201: Newly created garment.

- **PUT** `/api/clothes/{id}`
    - **Request Body**: Similar to POST.

- **DELETE** `/api/clothes/{id}`
    - **Response**: Status 200: "Garment with id {id} was deleted."

## Testing
- Run unit tests with:
  ```bash
  mvn test
  ```
- Tests are implemented using JUnit 5 and Mockito for mocking dependencies.

## Logging
- The application uses SLF4J for logging. Logs are stored in `var/log/marketplace-app.log`.

## Swagger Documentation
- Access the API documentation at `http://localhost:8080/swagger-ui/index.html`.


