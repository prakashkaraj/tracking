Scalable Tracking Number Generator App

A high-performance, concurrent-safe, and horizontally scalable REST API that generates unique tracking numbers for parcels. Designed for real-world logistics operations with observability, traceability, and best practices.

Features

- Generates unique tracking numbers (^[A-Z0-9]{1,16}$)
- Validates request parameters strictly
- Efficient and thread-safe generation logic
- Exposes metrics for Prometheus
- Distributed tracing with Zipkin
- H2 in-memory DB for easy local testing
- Swagger UI for API documentation

Tech Stack

- Java 8
- Spring Boot 2.7.x
- Spring Web, Data JPA, Validation
- H2 Database
- Spring Boot Actuator
- Micrometer Prometheus
- Spring Cloud Sleuth + Zipkin
- Springdoc OpenAPI (Swagger)

Getting Started

Prerequisites

- Java 8+
- Maven 3+

Build & Run

# Clone the repo
git clone https://github.com/prakashkaraj/tracking

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

API Reference

GET /next-tracking-number

Query Parameters (required):

Parameter                 Type     Description
origin_country_id         String   ISO alpha-2 (e.g., MY)
destination_country_id    String   ISO alpha-2 (e.g., ID)
weight                    Double   Weight in kilograms (0.001 - 999.999)
created_at                RFC3339  Creation timestamp (e.g., 2025-06-16T10:30:00+05:30)
customer_id               UUID     Customer UUID
customer_name             String   Customer name
customer_slug             Slug     Slug-case name (e.g., redbox-logistics)

Sample Response

{
  "tracking_number": "F83A7B6C1E9D2A4B",
  "created_at": "2025-06-16T10:30:00+05:30"
}

Developer Tools

H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:trackingdb
- Username: sa
- Password: (blank)

Swagger UI

- http://localhost:8080/swagger-ui.html

Monitoring

Distributed Tracing with Zipkin

Start Zipkin:
docker run -d -p 9411:9411 openzipkin/zipkin

Access at: http://localhost:9411

Prometheus Metrics

- Prometheus scraping: http://localhost:8080/actuator/prometheus
- Metrics exposed via Spring Boot Actuator

Actuator Endpoints

Examples:
- /actuator/health
- /actuator/metrics
- /actuator/prometheus

Configuration Notes

- In-memory H2 DB is default. For PostgreSQL, uncomment properties in application.properties.
- Sampling probability is set to 100% for tracing.

Docker (Optional)

FROM openjdk:8-jdk-alpine
COPY target/tracking.war app.war
ENTRYPOINT ["java","-jar","/app.war"]

Contact

Developed by Prakash Raj. For any query mail to prakashkaraj@gmail.com.