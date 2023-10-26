package com.hellowiz.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.hellowiz.api.api.Person;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private final AtomicLong counter;

    private final HashMap<Long, Person> mockDB;

    public PersonResource() {
        this.counter = new AtomicLong();
        this. mockDB = new HashMap<>();

        this.mockDB.put(this.counter.incrementAndGet(), new Person("Sam", "sam@memail.net"));
        this.mockDB.put(this.counter.incrementAndGet(), new Person("Sal", "salutations@hi.com"));
        this.mockDB.put(this.counter.incrementAndGet(), new Person("Sula", "s@la.io"));
    }

    @GET
    @Timed
    public List<Person> getPersons(@QueryParam("email") Optional<String> email) {
        if (email.isPresent()) {
            return mockDB.values().stream().filter(person ->
                    person.getEmail().equals(email.get())).collect(Collectors.toList());
        } else {
            return new ArrayList<>(mockDB.values());
        }
    }

    @GET
    @Path("/{personId}")
    @Timed
    public Person getPerson(@PathParam("personId") Optional<Long> personId) {
        if (personId.isPresent() && mockDB.containsKey(personId.get())) {
            return mockDB.get(personId.get());
        }
        throw new WebApplicationException(404);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Person addPerson(Optional<Person> person) {
        if (person.isPresent()) {
            Person newPerson = person.get();
            if (newPerson.getId() != null && mockDB.containsKey(newPerson.getId())) {
                throw new WebApplicationException("person already exists", 400);
            }
            newPerson.setId(counter.incrementAndGet());
            mockDB.put(newPerson.getId(), newPerson);
            return newPerson;
        }
        throw new WebApplicationException(400);
    }

    @PUT
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Person updatePerson(@PathParam("personId") Long personId, Optional<Person> person) {
        if (person.isEmpty()) {
            throw new WebApplicationException("need person id to update", 400);
        }
        Person personChanges = person.get();
        if (mockDB.containsKey(personId)) {
            Person existingPerson = mockDB.get(personChanges.getId());
            existingPerson.update(personChanges);
            return existingPerson;
        }
        throw new WebApplicationException(404);
    }

    @DELETE
    @Path("/{personId}")
    public Person deletePerson(@PathParam("personId") Long personId) {
        if (mockDB.containsKey(personId)) {
            Person person = mockDB.get(personId);
            mockDB.remove(personId);
            return person;
        }
        throw new WebApplicationException(404);
    }

    public boolean isHealthy() {
        return this.counter != null && this.mockDB != null;
    }
}
