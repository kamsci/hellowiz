package com.hellowiz.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.hellowiz.api.api.Person;
import com.hellowiz.api.db.PersonDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    final PersonDAO personDAO;
    public PersonResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GET
    @Timed
    public Response getPersons(@QueryParam("email") String email) {

        if (email != null) {
            Person foundPerson = personDAO.findByEmail(email);
            if (foundPerson == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
            return Response.status(Response.Status.OK)
                    .entity(Collections.singletonList(foundPerson))
                    .build();
        }
        List<Person> foundPersons = personDAO.findAll();
        return Response.status(Response.Status.OK)
                .entity(foundPersons)
                .build();
    }

    @GET
    @Path("/{personId}")
    @Timed
    public Response getPerson(@PathParam("personId") long personId) {
        Person foundPerson = personDAO.findById(personId);
        if (foundPerson == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(foundPerson)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response addPerson(Optional<Person> person) {
        if (person.isPresent()) {
            Person newPerson = person.get();
            if (newPerson.getName() != null && !newPerson.getName().isEmpty()
                    && newPerson.getEmail() != null && !newPerson.getEmail().isEmpty()
            ) {
                Person insertedPerson = personDAO.insert(newPerson.getName(), newPerson.getEmail());

                return Response.status(Response.Status.CREATED)
                        .entity(insertedPerson)
                        .build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }

    @PUT
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("personId") Long personId, Optional<Person> person) {
        // Validate changes exist
        if (missingName(person) && missingEmail(person)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR_NO_VALID_FIELDS)
                    .build();
        }

        // Validate person id and person exists
        if (personId == null || personId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR_INVALID_ID)
                    .build();
        }
        Person existingPerson = personDAO.findById(personId);
        if (existingPerson == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ERROR_ID_NOT_FOUND)
                    .build();
        }
z
        // Perform Update
        Person personChanges = person.get();
        Person updatedPerson = personDAO.updateById(personId, personChanges.getName(), personChanges.getEmail());
        return Response.status(Response.Status.OK)
                .entity(updatedPerson)
                .build();
    }

    @DELETE
    @Path("/{personId}")
    public Response deletePerson(@PathParam("personId") Optional<Long> personId) {
        // Validate person id and person exists
        if (personId.isEmpty() || personId.get() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR_INVALID_ID)
                    .build();
        }

        Person deletedPerson = personDAO.deleteById(personId.get());
        return Response.status(Response.Status.OK)
                .entity(deletedPerson)
                .build();
    }

    public boolean isHealthy() {
        return personDAO.isConnected();
    }

    private boolean missingName(Optional<Person> person) {
        return person.map(Person::getName)
                .filter(n -> n != null)
                .filter(n -> !n.isEmpty())
                .isEmpty();
    }

    private boolean missingEmail(Optional<Person> person) {
        return person.map(Person::getEmail)
                .filter(e -> e != null)
                .filter(e -> !e.isEmpty())
                .isEmpty();
    }

    public static String ERROR_NO_VALID_FIELDS = "No valid fields to update";
    public static String ERROR_INVALID_ID = "ID greater than 0 is required.";
    public static String ERROR_ID_NOT_FOUND = "ID is not found";
}
