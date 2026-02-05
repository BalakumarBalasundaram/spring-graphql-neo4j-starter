# Example GraphQL Queries and Mutations

This file contains example queries and mutations to test the GraphQL API.

## Create Persons

```graphql
# Create first person
mutation {
  createPerson(input: {
    name: "Alice Smith"
    email: "alice@example.com"
    age: 28
  }) {
    id
    name
    email
    age
  }
}

# Create second person
mutation {
  createPerson(input: {
    name: "Bob Johnson"
    email: "bob@example.com"
    age: 32
  }) {
    id
    name
    email
    age
  }
}

# Create third person
mutation {
  createPerson(input: {
    name: "Charlie Brown"
    email: "charlie@example.com"
    age: 25
  }) {
    id
    name
    email
    age
  }
}
```

## Query All Persons

```graphql
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
```

## Query Person by ID

```graphql
query {
  person(id: 0) {
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
```

## Search Persons by Name

```graphql
query {
  personsByName(name: "Alice") {
    id
    name
    email
    age
  }
}
```

## Create Friendships

```graphql
# Make Alice and Bob friends
mutation {
  createFriendship(personId: 0, friendId: 1) {
    id
    name
    friends {
      id
      name
    }
  }
}

# Make Bob and Charlie friends
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

# Make Alice and Charlie friends
mutation {
  createFriendship(personId: 0, friendId: 2) {
    id
    name
    friends {
      id
      name
    }
  }
}
```

## Query with Nested Friends

```graphql
query {
  persons {
    id
    name
    friends {
      id
      name
      friends {
        id
        name
      }
    }
  }
}
```

## Delete a Person

```graphql
mutation {
  deletePerson(id: 2)
}
```

## Full Example Workflow

1. Create some persons using the create mutations above
2. Create friendships between persons
3. Query all persons to see the graph structure
4. Search for specific persons by name
5. Query a single person with their friends

## cURL Examples

If you prefer using cURL instead of GraphiQL:

```bash
# Create a person
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createPerson(input: { name: \"Alice Smith\", email: \"alice@example.com\", age: 28 }) { id name email age } }"
  }'

# Query all persons
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { persons { id name email age friends { id name } } }"
  }'

# Create a friendship
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createFriendship(personId: 0, friendId: 1) { id name friends { id name } } }"
  }'
```
