# E-commerce Service

A Spring Boot-based e-commerce microservice with OAuth2 security, event-driven architecture, and distributed systems patterns.

## Technology Stack

- Java 21
- Spring Boot 3.4.2
- Spring Security with OAuth2/Keycloak
- PostgreSQL
- MongoDB
- Redis
- RabbitMQ
- Apache Kafka
- Flyway for database migrations
- Resilience4j for circuit breaking
- Prometheus & Micrometer for monitoring
- SpringDoc OpenAPI for API documentation

## Prerequisites

- Java 21
- Docker and Docker Compose
- Maven 3.x
- Keycloak server (for authentication)

## Local Development Setup

### Environment Setup

1. Start the required services using Docker Compose:

```bash
docker-compose up -d postgres redis mongodb rabbitmq kafka keycloak
```

2. Configure Keycloak:
   - Access Keycloak admin console at `http://localhost:8080`
   - Create a new realm: `ecommerce`
   - Create a new client: `ecommerce-client`
   - Configure client secret and update in `application.yml`

### Building the Application

```bash
mvn clean package
```

### Running the Application

#### Local Profile
```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

#### Docker Profile
```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker
```

## Docker Deployment

### Building the Docker Image

```bash
docker build -t ecommerce-service .
```

### Running with Docker Compose

Create a `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    image: ecommerce-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres
      - redis
      - mongodb
      - rabbitmq
      - kafka
      - keycloak

  postgres:
    image: postgres:latest
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ecommerce-demo786
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d ecommerce"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:latest
    ports:
      - "6380:6379"

  mongodb:
    image: mongo:latest
    ports:
      - "27018:27017"

  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"

  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "29092:29092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Start the application:
```bash
docker-compose up -d
```

## API Documentation

Access the API documentation through:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Monitoring

The application exposes the following monitoring endpoints:
- Health check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

## Configuration

The application uses different configuration profiles:
- `application.yml`: Default configuration
- `application-local.yml`: Local development configuration
- `application-docker.yml`: Docker environment configuration

Key configuration areas:
- Database connections (PostgreSQL, MongoDB, Redis)
- Message brokers (RabbitMQ, Kafka)
- OAuth2/Security settings
- Circuit breaker parameters
- Server and performance tuning

## Security

The application uses OAuth2 with Keycloak for authentication and authorization:
- OAuth2 Resource Server configuration
- JWT token validation
- Role-based access control
- Secure API endpoints

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

[Add your license information here]