package com.hellowiz.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.hellowiz.api.api.Person;
import com.hellowiz.api.db.PersonDAO;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private Validator validator;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);
    public static String ERROR_INVALID_ID = "ID greater than 0 is required.";
    public static String ERROR_ID_NOT_FOUND = "ID is not found";

    private final PersonDAO personDAO;
    public PersonResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.validator = factory.getValidator();
        } catch (Exception e) {
            LOGGER.error("Validator failed to build ", e);
        }

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
    public Response getPerson(@PathParam("personId") int personId) {
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
    public Response addPerson(Optional<Person.Request> personRequest) {
        if (personRequest.isPresent()) {
            Person.Request newPerson = personRequest.get();

            // Validate the person object
            Set<ConstraintViolation<Person.Request>> violations = validator.validate(newPerson);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("Validation failed", violations);
            }

            personDAO.insert(newPerson.getName(), newPerson.getEmail());
            return Response.status(Response.Status.CREATED)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .build();
    }

    @PUT
    @Path("/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("personId") int personId, Optional<Person.Request> personRequest) {
        // Validate person id and person request exists
        if (personId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR_INVALID_ID)
                    .build();
        }
        if (personRequest.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .build();
        }

        Person.Request personChanges = personRequest.get();

        // Validate change request
        Set<ConstraintViolation<Person.Request>> violations = validator.validate(personChanges);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }

        // Validate person exists to update
        Person existingPerson = personDAO.findById(personId);
        if (existingPerson == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ERROR_ID_NOT_FOUND)
                    .build();
        }

        // Perform Update
        int updated = personDAO.updateById(personId, personChanges.getName(), personChanges.getEmail());
        return Response.status(Response.Status.OK)
                .entity(updated)
                .build();
    }

    @DELETE
    @Path("/{personId}")
    public Response deletePerson(@PathParam("personId") Optional<Integer> personId) {
        // Validate person id and person exists
        if (personId.isEmpty() || personId.get() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR_INVALID_ID)
                    .build();
        }

        personDAO.deleteById(personId.get());
        return Response.status(Response.Status.OK)
                .build();
    }

    public boolean isHealthy() {
        return personDAO.isConnected() && validator != null;
    }

}
