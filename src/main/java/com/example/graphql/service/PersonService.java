package com.example.graphql.service;

import com.example.graphql.model.Person;
import com.example.graphql.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;

    /**
     * Get all persons
     */
    @Transactional(readOnly = true)
    public List<Person> getAllPersons() {
        log.info("Fetching all persons");
        return personRepository.findAll();
    }

    /**
     * Get person by ID
     */
    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Long id) {
        log.info("Fetching person with id: {}", id);
        return personRepository.findById(id);
    }

    /**
     * Search persons by name
     */
    @Transactional(readOnly = true)
    public List<Person> getPersonsByName(String name) {
        log.info("Searching persons by name: {}", name);
        return personRepository.findByNameContaining(name);
    }

    /**
     * Create a new person
     */
    @Transactional
    public Person createPerson(String name, String email, Integer age) {
        log.info("Creating new person: {}", name);
        Person person = Person.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();
        return personRepository.save(person);
    }

    /**
     * Create a friendship between two persons
     */
    @Transactional
    public Person createFriendship(Long personId, Long friendId) {
        log.info("Creating friendship between {} and {}", personId, friendId);
        
        // Verify both persons exist
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + personId));
        Person friend = personRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found: " + friendId));
        
        // Create friendship using Cypher query
        return personRepository.createFriendship(personId, friendId);
    }

    /**
     * Delete a person
     */
    @Transactional
    public boolean deletePerson(Long id) {
        log.info("Deleting person with id: {}", id);
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
