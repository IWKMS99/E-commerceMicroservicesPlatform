# Advanced E-commerce Platform: A Modular Monolith

[![Java CI with Gradle](https://github.com/iwkms99/E-commerceMicroservicesPlatform/actions/workflows/main.yml/badge.svg)](https://github.com/iwkms99/E-commerceMicroservicesPlatform/actions/workflows/main.yml)

This project serves as a comprehensive blueprint for building modern, scalable, and maintainable backend systems. It's a fully functional **e-commerce platform MVP** meticulously designed with a **modular monolith architecture**. This pragmatic approach offers the development velocity of a traditional monolith while laying a solid foundation for a future-proof, seamless evolution into a microservices-based system.

The platform implements the entire customer lifecycle, from user authentication and product browsing to order processing and payment simulation, all while ensuring data integrity and high performance through a thoughtful technology stack.

---

### High-Level Architecture

[![](https://mermaid.ink/img/pako:eNqtVtlu4zYU_RVCwQwcgPZos2UrxQDO0mmApHXjtA-t-0BJlE1EElWSSuIG-fdeUrS8xG77UAGGKfLccxceXurNSXlGndj59And9FNellSkFM0KonIuSjQV6YopmqpGUNRH37OKFOgzuuJlTRRLCop-pUIyXi0qYJg2asVFjOaEZWhaMPqMei9MrdD0Fk2lZFKRSp0bqDWLUTRwgXjOyrpgOaMZsD_wpJFqAZxLQeoVuntYVAgeMOv3--hHiBhd05xVTAGF1JMt4ApcVqq3cNoB6l0K_iKp-HLPEwaxTuv6fOGca2YNl03SOlg4x3Lv3fOsKYhA97ziBaQBtq2hfqaz2x780Dei6AtZoy_o4Wb-CJWplOBFAel1fvZ8QQwFS4mOHMCCbiHm-QXChQT0HzL-qQl4D3NFFCn4UufZjv4BKUw54O8k5ieRGZfm_yTqtnqGinKxBmQ3PomekXXZ7oQdHUPSKjtaoWtICs20PqSiVXpQoRmXaimovL78XdO3b_Of775LxFdtmRCp3fyxb_VAM2ZNzFCjr0i6OoCakA5i21XdXK0LVi23iksLIiVoEQZGcTkrivgsd_MwH2MJSnii8VlIhsPR5rX_wjK1iv36Fae8gNNy5hHf9dOLA0JSs_-RrTTl3xDSPE3yjjCJoomXnyT0syAKx4eEmS21paSjPM9JR-mnEYmSf6fcIbWH1xZy150-aroce3P6gGArf6z1jY1-cadNvFFem_qe8VZD2Aqjy8cEtbfxd6x6Ot5uAOANNnF_1mHutiHAQWP54fFxNgf5_dlQaGkOTH5tgR2FPzAJKm7agT0oEvXm6ypdCWg9jbSHRsMMKYE-a7l0HQ5WE9P07Lot0QGkJBVZUpRC3TocDPdBdUGgHXJdVgsyJf5A9URR3dbawmzld3IMBtBAFBX9Njt9f5RNZdtgi2rbj6EUlGTSBGe25UOEO9C6SQomV1TG7ewV2Cqa3Txvo-kU0RpvZHFobuf1zVbQAwqbd5dOOGib1DRNqZTttOnY5ukPdA06iVlN2E59YrlN6aT1tuMeXd7kdNK3UDuhWckfk_mmv_WSNWJVRl8xWjZEwK1N4WYGib5w8XS-dwJ2ZAqOigKUe1k01EoW2J40KUUu9rCPAxziIR6hrlEkozRxPzSKi45-Knf4zZ6AA8gAww7AxmUf_ER43LGnw8ANPrJvJjJgIEKQdTxEw63Lna1FvW-wDL64Ukd8TbAHaXnY87EXYC_sHEfe2J2M_oNjHwUXDnaWgmVOrERDsQNfISXRr86b9rdw1IqWcJxjGGZEPOmPkHewqUn1G-flxkzwZrly4pwUEt6aGk4OvWYErtQtBG41OCK8qZQTR5PAcDjxm_PqxH1vMhoMgyhyw3AURVEQhthZO7E3Gg_GvhdN_JHrD4OJO3rHzl_GrzfwYGo8dr0w9IMgdMECpAUyvW-_Kc2n5fvf-bAwWA?type=png)](https://mermaid.live/edit#pako:eNqtVtlu4zYU_RVCwQwcgPZos2UrxQDO0mmApHXjtA-t-0BJlE1EElWSSuIG-fdeUrS8xG77UAGGKfLccxceXurNSXlGndj59And9FNellSkFM0KonIuSjQV6YopmqpGUNRH37OKFOgzuuJlTRRLCop-pUIyXi0qYJg2asVFjOaEZWhaMPqMei9MrdD0Fk2lZFKRSp0bqDWLUTRwgXjOyrpgOaMZsD_wpJFqAZxLQeoVuntYVAgeMOv3--hHiBhd05xVTAGF1JMt4ApcVqq3cNoB6l0K_iKp-HLPEwaxTuv6fOGca2YNl03SOlg4x3Lv3fOsKYhA97ziBaQBtq2hfqaz2x780Dei6AtZoy_o4Wb-CJWplOBFAel1fvZ8QQwFS4mOHMCCbiHm-QXChQT0HzL-qQl4D3NFFCn4UufZjv4BKUw54O8k5ieRGZfm_yTqtnqGinKxBmQ3PomekXXZ7oQdHUPSKjtaoWtICs20PqSiVXpQoRmXaimovL78XdO3b_Of775LxFdtmRCp3fyxb_VAM2ZNzFCjr0i6OoCakA5i21XdXK0LVi23iksLIiVoEQZGcTkrivgsd_MwH2MJSnii8VlIhsPR5rX_wjK1iv36Fae8gNNy5hHf9dOLA0JSs_-RrTTl3xDSPE3yjjCJoomXnyT0syAKx4eEmS21paSjPM9JR-mnEYmSf6fcIbWH1xZy150-aroce3P6gGArf6z1jY1-cadNvFFem_qe8VZD2Aqjy8cEtbfxd6x6Ot5uAOANNnF_1mHutiHAQWP54fFxNgf5_dlQaGkOTH5tgR2FPzAJKm7agT0oEvXm6ypdCWg9jbSHRsMMKYE-a7l0HQ5WE9P07Lot0QGkJBVZUpRC3TocDPdBdUGgHXJdVgsyJf5A9URR3dbawmzld3IMBtBAFBX9Njt9f5RNZdtgi2rbj6EUlGTSBGe25UOEO9C6SQomV1TG7ewV2Cqa3Txvo-kU0RpvZHFobuf1zVbQAwqbd5dOOGib1DRNqZTttOnY5ukPdA06iVlN2E59YrlN6aT1tuMeXd7kdNK3UDuhWckfk_mmv_WSNWJVRl8xWjZEwK1N4WYGib5w8XS-dwJ2ZAqOigKUe1k01EoW2J40KUUu9rCPAxziIR6hrlEkozRxPzSKi45-Knf4zZ6AA8gAww7AxmUf_ER43LGnw8ANPrJvJjJgIEKQdTxEw63Lna1FvW-wDL64Ukd8TbAHaXnY87EXYC_sHEfe2J2M_oNjHwUXDnaWgmVOrERDsQNfISXRr86b9rdw1IqWcJxjGGZEPOmPkHewqUn1G-flxkzwZrly4pwUEt6aGk4OvWYErtQtBG41OCK8qZQTR5PAcDjxm_PqxH1vMhoMgyhyw3AURVEQhthZO7E3Gg_GvhdN_JHrD4OJO3rHzl_GrzfwYGo8dr0w9IMgdMECpAUyvW-_Kc2n5fvf-bAwWA)

---

## ✨ Key Features & Concepts Demonstrated

-   **Event-Driven Core**: Modules communicate asynchronously via an internal Spring `ApplicationEventBus`. This decouples domains (e.g., `Order` service doesn't know about `Inventory` stock logic) and eliminates runtime dependencies.
-   **Optimized Data Persistence**: A strategic use of **PostgreSQL** for transactional, relational data (users, orders) and **Redis** for high-throughput, ephemeral data (shopping carts), ensuring both data integrity and performance.
-   **Robust Security Model**: End-to-end security with Spring Security, featuring JWT-based authentication and role-based authorization (`USER`/`ADMIN`) to protect sensitive endpoints.
-   **Production-Grade Testing Strategy**: A multi-layered testing approach ensures reliability:
    -   **Unit Tests (Mockito)** for isolated business logic.
    -   **Integration Tests (MockMvc)** for the API layer.
    -   **Data-Layer Tests (Testcontainers)** for true-to-production database interactions.
    -   **Full Context Integration Tests** to verify the event-driven flows across modules.
-   **Automated & Reliable Development Pipeline**: A complete **CI/CD workflow** using GitHub Actions automatically builds the project, runs the entire test suite, and provides immediate feedback, ensuring that the main branch is always stable.
-   **Database Lifecycle Management**: Schema changes are managed, versioned, and applied automatically and safely using **Flyway**.

---

## 🛠️ Technology Stack

| Category               | Technology / Library                      | Purpose                                                              |
| ---------------------- |-------------------------------------------| -------------------------------------------------------------------- |
| **Core Framework**     | Java 21, Spring Boot 3.5                  | Foundation for the entire application.                               |
| **Data & Persistence** | Spring Data JPA, Hibernate, PostgreSQL    | Relational data storage and ORM.                                     |
| **In-Memory & Cache**  | Spring Data Redis, Redis                  | High-performance storage for user carts.                             |
| **Security**           | Spring Security, JSON Web Tokens (jjwt)   | Authentication, authorization, and securing API endpoints.           |
| **Database Mgmt**      | Flyway                                    | Version-controlled database migrations.                              |
| **API & Web**          | Spring Web (MVC), RESTful Principles      | Exposing the application's functionality via a clean API.            |
| **DevOps & CI/CD**     | Docker, Docker Compose, GitHub Actions    | Containerization, local environment setup, and automated CI pipeline.|
| **Testing**            | JUnit 5, Mockito, Testcontainers, MockMvc | Ensuring code quality and reliability at all levels.                 |
| **Utilities**          | Lombok, MapStruct                         | Reducing boilerplate code for entities and DTO mapping.              |

---

## 🚀 Getting Started

### Prerequisites

-   Java 21 (or higher)
-   Docker & Docker Compose
-   An API client like [Postman](https://www.postman.com/) or `curl`.

### Local Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iwkms99/E-commerceMicroservicesPlatform.git
    cd E-commerceMicroservicesPlatform
    ```

2.  **Launch infrastructure:**
    This single command starts PostgreSQL and Redis containers with all the necessary configurations.
    ```bash
    docker-compose up -d
    ```

3.  **Run the application:**
    You can run the app from your favorite IDE by executing the `main` method in `EcommerceApplication.java` or via the Gradle wrapper.
    ```bash
    ./gradlew bootRun
    ```
    The server will start on `http://localhost:8080`.

### Quick Test Drive with `curl`

1.  **Register a user:**
    ```bash
    curl -X POST http://localhost:8080/api/v1/users/register \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com", "password":"password123", "firstName":"Test", "lastName":"User"}'
    ```

2.  **Log in to get a token:**
    ```bash
    TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com", "password":"password123"}' | jq -r .token)
    
    echo "Your JWT is: $TOKEN"
    ```
    *(Requires `jq` to be installed. Otherwise, copy the token manually from the response.)*

3.  **Get your user details using the token:**
    ```bash
    curl -X GET http://localhost:8080/api/v1/users/me \
    -H "Authorization: Bearer $TOKEN"
    ```

---

## 🗺️ Project Roadmap (Future Work)

This project provides a solid foundation. Here are some potential next steps to evolve it further:

-   [ ] **Implement Compensating Transactions (Saga Pattern):** Enhance reliability by creating compensating actions for failed events (e.g., automatically refunding a payment if stock reservation fails).
-   [ ] **Extract First Microservice:** Choose a module (e.g., `User` or `Catalog`) and extract it into a standalone Spring Boot service, replacing internal calls with REST or message queue communication.
-   [ ] **Introduce a Message Broker:** Replace the internal Spring Event Bus with a robust message broker like **RabbitMQ** or **Kafka** to prepare for a distributed environment.
-   [ ] **Add Advanced Catalog Features:** Implement full-text search with **Elasticsearch** and add filtering/pagination.
-   [ ] **Containerize the Application:** Write a `Dockerfile` for the application itself and orchestrate the entire stack with a production-ready `docker-compose.yml`.

---
*This project was developed as a deep dive into modern software architecture and best practices. Feel free to explore, fork, and use it as a reference for your own work.*