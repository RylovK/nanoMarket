# Nanomarket

**Nanomarket** is my own educational project for an online store built using a microservices architecture.

## Project Overview

The system based on Spring Cloud framework and consists of the following services:
- **ConfigServer**: Centralized configuration service.
- **EurekaServer**: Service registry and discovery server.
- **GatewayServer**: API Gateway that routes requests to appropriate microservices, providing authentication, rate limiting, and request filtering.
- **ProductService**: Manages products, categories, brands, product images, and inventory updates during order placement.
- **CartService**: Handles user shopping carts, leveraging Redis for data storage.
- **OrderService**: Processes customer orders from the cart, verifies product availability, assigns order numbers, and clears the cart upon completion.

## Technologies

The project utilizes the following technologies and tools:
- **Languages and Frameworks**: Java, Spring Boot, Spring Data, Spring Cloud.
- **Containerization**: Docker, Docker Compose.
- **Monitoring and Resilience**: Actuator, Resilience4j.
- **Data Storage**: PostgreSQL, Redis, MinIO.
- **Development Utilities**: AOP, Lombok, MapStruct, Liquibase, Swagger, OpenAPI 3.0.



## Setup Instructions

### Prerequisites

To run the project, you need:
- **Docker** installed on your system.
- Java 17

1. Clone the repository:
   ```bash
   git clone https://github.com/RylovK/nanoMarket.git
   cd nanomarket
   
2. Build the project:
    ```bash
    mvn clean package

3. Start the services using Docker Compose:
    ```bash
    docker-compose up --build

## Current State

The project is currently under development. The following services are fully implemented:
- **ConfigServer**: Centralized configuration management for all microservices using Spring Cloud Config.
- **EurekaServer**: Service discovery and registration, allowing microservices to dynamically locate each other.
- **GatewayServer**: API Gateway built with Spring Cloud Gateway, utilizing Project Reactor for reactive request handling and Resilience4j for circuit breaking, rate limiting, and fault tolerance.
- **ProductService**: Handles products, categories, brands, and inventory updates.
- **CartService**: Manages user carts using Redis.
- **OrderService**: Processes orders, verifies product availability, and clears carts after order completion.

## Future Plans
Integrate **Keycloak with OAuth2** for user authentication and authorization.
Add centralized logging and tracing using **ELK stack**.
Transition to an event-driven architecture with **Kafka** or **RabbitMQ**.