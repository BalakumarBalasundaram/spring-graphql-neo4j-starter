package com.example.graphql.repository;

import com.example.graphql.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    /**
     * Find persons by name using Cypher query
     */
    @Query("MATCH (p:Person) WHERE p.name CONTAINS $name RETURN p")
    List<Person> findByNameContaining(String name);

    /**
     * Create friendship relationship using Cypher
     */
    @Query("MATCH (p1:Person), (p2:Person) " +
           "WHERE id(p1) = $personId AND id(p2) = $friendId " +
           "MERGE (p1)-[:FRIEND_OF]->(p2) " +
           "RETURN p1")
    Person createFriendship(Long personId, Long friendId);
}
