package com.example.graphql.controller;

import com.example.graphql.model.Person;
import com.example.graphql.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @QueryMapping
    public List<Person> persons() {
        return personService.getAllPersons();
    }

    @QueryMapping
    public Person person(@Argument Long id) {
        return personService.getPersonById(id).orElse(null);
    }

    @QueryMapping
    public List<Person> personsByName(@Argument String name) {
        return personService.getPersonsByName(name);
    }

    @MutationMapping
    public Person createPerson(@Argument CreatePersonInput input) {
        return personService.createPerson(
                input.name(),
                input.email(),
                input.age()
        );
    }

    @MutationMapping
    public Person createFriendship(@Argument Long personId, @Argument Long friendId) {
        return personService.createFriendship(personId, friendId);
    }

    @MutationMapping
    public Boolean deletePerson(@Argument Long id) {
        return personService.deletePerson(id);
    }

    /**
     * Input DTO for creating a person
     */
    public record CreatePersonInput(String name, String email, Integer age) {
    }
}
