# OpenShift Deployment Guide

This guide provides step-by-step instructions for deploying the Spring GraphQL Neo4j application to OpenShift.

## Prerequisites

- Access to an OpenShift cluster (tested on OpenShift 4.x)
- OpenShift CLI (`oc`) installed
- Docker or Podman for building images
- Container registry access (Docker Hub, Quay.io, or OpenShift internal registry)

## Deployment on Windows

### 1. Install Prerequisites

```powershell
# Install OpenShift CLI
# Download from: https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/

# Verify installation
oc version
```

### 2. Login to OpenShift Cluster

```powershell
# Login to your OpenShift cluster
oc login <your-openshift-cluster-url>

# Enter your username and password when prompted
```

### 3. Create a New Project

```powershell
# Create a new project/namespace
oc new-project spring-graphql-neo4j

# Verify current project
oc project
```

## Building and Pushing the Container Image

### Option 1: Using Docker Desktop on Windows

```powershell
# Build the Docker image
docker build -t spring-graphql-neo4j-starter:latest .

# Tag the image for your registry
docker tag spring-graphql-neo4j-starter:latest <your-registry>/spring-graphql-neo4j-starter:latest

# Login to your container registry
docker login <your-registry>

# Push the image
docker push <your-registry>/spring-graphql-neo4j-starter:latest
```

### Option 2: Using OpenShift Internal Registry

```powershell
# Get the internal registry URL
$REGISTRY = oc get route default-route -n openshift-image-registry -o jsonpath='{.spec.host}'

# Get your token
$TOKEN = oc whoami -t

# Login to the internal registry
docker login -u $(oc whoami) -p $TOKEN $REGISTRY

# Build and tag
docker build -t "$REGISTRY/spring-graphql-neo4j/spring-graphql-neo4j-starter:latest" .

# Push to internal registry
docker push "$REGISTRY/spring-graphql-neo4j/spring-graphql-neo4j-starter:latest"
```

### Option 3: Using Source-to-Image (S2I)

```powershell
# Create a new build from source
oc new-build java:17 --name=spring-graphql-neo4j --binary=true

# Start the build from the local directory
oc start-build spring-graphql-neo4j --from-dir=. --follow

# This will build the application using OpenShift's built-in Java builder
```

## Deploy to OpenShift

### Using the Provided Template

```powershell
# Deploy using the template
oc process -f openshift/deployment-template.yaml `
  -p APPLICATION_IMAGE=<your-registry>/spring-graphql-neo4j-starter:latest `
  | oc apply -f -

# Check deployment status
oc get pods -w
```

### Manual Deployment Steps

If you prefer to deploy manually:

```powershell
# 1. Deploy Neo4j
oc new-app neo4j:5.13.0 `
  -e NEO4J_AUTH=neo4j/password `
  --name=neo4j

# Expose Neo4j service
oc expose svc/neo4j --port=7474

# 2. Deploy the Spring Boot application
oc new-app <your-registry>/spring-graphql-neo4j-starter:latest `
  --name=spring-graphql-app `
  -e SPRING_NEO4J_URI=bolt://neo4j:7687 `
  -e SPRING_NEO4J_AUTHENTICATION_USERNAME=neo4j `
  -e SPRING_NEO4J_AUTHENTICATION_PASSWORD=password

# 3. Expose the application
oc expose svc/spring-graphql-app

# 4. Get the application URL
oc get route spring-graphql-app
```

## Configure Persistent Storage

To ensure Neo4j data persists across pod restarts:

```powershell
# Create a YAML file for the PVC
@'
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: neo4j-data
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
'@ | Out-File -FilePath neo4j-pvc.yaml -Encoding utf8

# Apply the PVC
oc apply -f neo4j-pvc.yaml

# Update Neo4j deployment to use the PVC
oc set volume deployment/neo4j `
  --add `
  --name=neo4j-data `
  --type=persistentVolumeClaim `
  --claim-name=neo4j-data `
  --mount-path=/data
```

## Scaling the Application

```powershell
# Scale the Spring Boot application
oc scale deployment/spring-graphql-app --replicas=3

# Check pod status
oc get pods -l app=spring-graphql-app
```

## Monitoring and Logs

```powershell
# View application logs
oc logs -f deployment/spring-graphql-app

# View Neo4j logs
oc logs -f deployment/neo4j

# Get application status
oc status

# Describe the deployment
oc describe deployment spring-graphql-app
```

## Health Checks

The application includes Spring Boot Actuator endpoints for health checks:

```powershell
# Test the health endpoint
$APP_URL=$(oc get route spring-graphql-app -o jsonpath='{.spec.host}')
curl http://$APP_URL/actuator/health
```

## Accessing the Application

```powershell
# Get the application URL
$APP_URL=$(oc get route spring-graphql-app -o jsonpath='{.spec.host}')

# Access GraphiQL interface
start http://$APP_URL/graphiql

# Test with cURL
curl -X POST http://$APP_URL/graphql `
  -H "Content-Type: application/json" `
  -d '{\"query\": \"{ persons { id name } }\"}'
```

## Environment Variables

Configure the application using environment variables:

```powershell
# Set environment variables
oc set env deployment/spring-graphql-app `
  SPRING_NEO4J_URI=bolt://neo4j:7687 `
  SPRING_NEO4J_AUTHENTICATION_USERNAME=neo4j `
  SPRING_NEO4J_AUTHENTICATION_PASSWORD=your-secure-password `
  SPRING_PROFILES_ACTIVE=production

# View current environment variables
oc set env deployment/spring-graphql-app --list
```

## Secrets Management

For production, use OpenShift secrets:

```powershell
# Create a secret for Neo4j credentials
oc create secret generic neo4j-credentials `
  --from-literal=username=neo4j `
  --from-literal=password=your-secure-password

# Mount the secret as environment variables
oc set env deployment/spring-graphql-app `
  --from=secret/neo4j-credentials `
  --prefix=SPRING_NEO4J_AUTHENTICATION_
```

## Troubleshooting

### Application won't start

```powershell
# Check pod events
oc get events --sort-by='.lastTimestamp'

# Check pod logs
oc logs deployment/spring-graphql-app --previous

# Describe the pod
oc describe pod -l app=spring-graphql-app
```

### Can't connect to Neo4j

```powershell
# Check Neo4j service
oc get svc neo4j

# Test Neo4j connectivity from app pod
oc rsh deployment/spring-graphql-app
curl neo4j:7474
```

### DNS resolution issues

```powershell
# Verify service exists
oc get svc

# Check if services can resolve each other
oc rsh deployment/spring-graphql-app
nslookup neo4j
```

## Resource Limits

Configure resource requests and limits:

```powershell
# Set resource limits
oc set resources deployment/spring-graphql-app `
  --requests=cpu=500m,memory=512Mi `
  --limits=cpu=1,memory=1Gi

oc set resources deployment/neo4j `
  --requests=cpu=500m,memory=1Gi `
  --limits=cpu=1,memory=2Gi
```

## Cleanup

To remove the deployment:

```powershell
# Delete all resources
oc delete all -l app=spring-graphql-app
oc delete all -l app=neo4j
oc delete pvc neo4j-data

# Or delete the entire project
oc delete project spring-graphql-neo4j
```

## Next Steps

- Configure HTTPS/TLS for the route
- Set up monitoring with Prometheus
- Configure auto-scaling based on metrics
- Implement backup strategies for Neo4j data
- Set up CI/CD pipeline for automated deployments
