package com.example.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    private Integer age;

    @Relationship(type = "FRIEND_OF", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private Set<Person> friends = new HashSet<>();

    public void addFriend(Person friend) {
        if (this.friends == null) {
            this.friends = new HashSet<>();
        }
        this.friends.add(friend);
    }
}
