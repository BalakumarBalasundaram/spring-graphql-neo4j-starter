# Spring GraphQL Neo4j Starter

A minimal Spring Boot project showcasing how to integrate Spring GraphQL with Neo4j. This project demonstrates best practices for building GraphQL APIs backed by a graph database.

## 📑 Table of Contents

- [Core Design Principles](#-core-design-principles)
- [Architecture](#️-architecture)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [GraphQL Schema](#-graphql-schema)
- [Docker Deployment](#-docker-deployment)
- [OpenShift Deployment](#️-openshift-deployment)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Technologies Used](#-technologies-used)

## 🎯 Core Design Principles

- **Schema-First GraphQL**: GraphQL schema defined in `.graphqls` files
- **Thin Resolvers**: Controllers delegate to service layer
- **Business Logic in Services**: Service layer contains all business logic
- **Graph Traversal via Cypher**: Neo4j queries using Cypher for efficient graph operations
- **Minimal DataLoader Usage**: Direct Neo4j relationships for graph traversal

## 🏗️ Architecture

```
┌─────────────────┐
│   GraphQL API   │  <- Schema-first approach
│   (Controllers) │  <- Thin resolvers
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Service Layer  │  <- Business logic
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Repositories   │  <- Cypher queries
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Neo4j DB      │  <- Graph database
└─────────────────┘
```

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6+
- Docker and Docker Compose (for local development)
- OpenShift CLI (for deployment)

## 🚀 Getting Started

### 1. Start Neo4j Database

```bash
docker-compose up -d
```

This will start Neo4j on:
- Bolt: `bolt://localhost:7687`
- HTTP: `http://localhost:7474`
- Default credentials: `neo4j/password`

### 2. Build the Application

```bash
mvn clean package
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/spring-graphql-neo4j-starter-1.0.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 4. Access GraphiQL

Navigate to `http://localhost:8080/graphiql` to interact with the GraphQL API.

## 📝 GraphQL Schema

The API exposes the following operations:

### Queries

```graphql
# Get all persons
query {
  persons {
    id
    name
    email
    age
    friends {
      id
      name
    }
  }
}

# Get person by ID
query {
  person(id: 1) {
    id
    name
    email
    age
  }
}

# Search persons by name
query {
  personsByName(name: "John") {
    id
    name
    email
  }
}
```

### Mutations

```graphql
# Create a new person
mutation {
  createPerson(input: {
    name: "John Doe"
    email: "john@example.com"
    age: 30
  }) {
    id
    name
    email
    age
  }
}

# Create a friendship
mutation {
  createFriendship(personId: 1, friendId: 2) {
    id
    name
    friends {
      id
      name
    }
  }
}

# Delete a person
mutation {
  deletePerson(id: 1)
}
```

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t spring-graphql-neo4j-starter:latest .
```

### Run with Docker Compose

```bash
docker-compose up
```

## ☁️ OpenShift Deployment

### Prerequisites

- OpenShift cluster access
- `oc` CLI tool installed and configured

### Deploy to OpenShift

1. Login to your OpenShift cluster:

```bash
oc login <your-openshift-cluster-url>
```

2. Create a new project:

```bash
oc new-project spring-graphql-neo4j
```

3. Build the application image:

```bash
docker build -t spring-graphql-neo4j-starter:latest .
docker tag spring-graphql-neo4j-starter:latest <your-registry>/spring-graphql-neo4j-starter:latest
docker push <your-registry>/spring-graphql-neo4j-starter:latest
```

4. Deploy using the template:

```bash
oc process -f openshift/deployment-template.yaml \
  -p APPLICATION_IMAGE=<your-registry>/spring-graphql-neo4j-starter:latest \
  | oc apply -f -
```

5. Get the application URL:

```bash
oc get route spring-graphql-route -o jsonpath='{.spec.host}'
```

### Deployment on Windows

For Windows environments, ensure:
- Docker Desktop is installed and running
- WSL2 is enabled (recommended for better performance)
- OpenShift CLI is installed

```powershell
# Windows PowerShell commands
docker build -t spring-graphql-neo4j-starter:latest .
oc login <your-openshift-cluster-url>
oc new-project spring-graphql-neo4j
oc process -f openshift/deployment-template.yaml | oc apply -f -
```

## 🧪 Testing

Run tests with:

```bash
mvn test
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/example/graphql/
│   │   ├── GraphQLNeo4jApplication.java   # Main application
│   │   ├── controller/
│   │   │   └── PersonController.java      # Thin GraphQL resolvers
│   │   ├── service/
│   │   │   └── PersonService.java         # Business logic
│   │   ├── repository/
│   │   │   └── PersonRepository.java      # Neo4j repository with Cypher
│   │   └── model/
│   │       └── Person.java                # Domain entity
│   └── resources/
│       ├── graphql/
│       │   └── schema.graphqls            # GraphQL schema
│       └── application.yml                # Application configuration
└── test/
    └── java/com/example/graphql/          # Tests
```

## 🔧 Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password
  graphql:
    graphiql:
      enabled: true
      path: /graphiql
```

For production, use environment variables:
- `SPRING_NEO4J_URI`
- `SPRING_NEO4J_AUTHENTICATION_USERNAME`
- `SPRING_NEO4J_AUTHENTICATION_PASSWORD`

## 📚 Technologies Used

- **Spring Boot 3.2.0**: Application framework
- **Spring GraphQL**: GraphQL implementation
- **Spring Data Neo4j**: Neo4j integration
- **Neo4j 5.13**: Graph database
- **Lombok**: Reduce boilerplate code
- **Maven**: Build tool

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 🚀 Quick Reference

### Start Locally
```bash
docker-compose up -d    # Start Neo4j
mvn spring-boot:run     # Start application
```
Access GraphiQL at: http://localhost:8080/graphiql

### Deploy to OpenShift (Windows)
```powershell
oc login <cluster-url>
oc new-project spring-graphql-neo4j
docker build -t spring-graphql-neo4j-starter:latest .
oc process -f openshift/deployment-template.yaml | oc apply -f -
```

### Example GraphQL Query
```graphql
query {
  persons {
    id
    name
    friends {
      name
    }
  }
}
```

See [examples/queries.md](examples/queries.md) for more examples and [openshift/DEPLOYMENT.md](openshift/DEPLOYMENT.md) for detailed deployment instructions.

## 📄 License

This project is open source and available under the MIT License.
