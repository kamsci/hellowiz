package com.hellowiz.api.resources;

import com.hellowiz.api.api.Person;
import com.hellowiz.api.api.errors.ErrorResponse;
import com.hellowiz.api.db.PersonDAO;
import com.hellowiz.api.resources.middleware.DefaultExceptionMapper;
import com.hellowiz.api.resources.middleware.ExecuteExceptionMapper;
import com.hellowiz.api.resources.middleware.ViolationExceptionMapper;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.oneOf;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class PersonResourceTest {
    private final int testId = 1;
    private final String testName = "Sam";
    private final String testEmail = "sam@test.com";
    private static final PersonDAO DAO = mock(PersonDAO.class);
    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new PersonResource(DAO))
            .addProvider(ViolationExceptionMapper.class)
            .addProvider(ExecuteExceptionMapper.class)
            .addProvider(DefaultExceptionMapper.class)
            .build();
    private Person person;

    @BeforeEach
    void setup() {
        person = new Person(testName, testEmail);
        person.setId(testId);
    }

    @AfterEach
    void tearDown() {
        reset(DAO);
    }

    @Test
    void getPersonsSuccess() {
        when(DAO.findAll()).thenReturn(Collections.singletonList(person));

        final Response response = EXT.target("/persons").request().get();
        List<Person> found = response.readEntity(new GenericType<List<Person>>() {});

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(found.size(), equalTo(1));
        assertThat(found.get(0).getId(), equalTo(person.getId()));
        verify(DAO).findAll();
    }

    @Test
    void getPersonByIdSuccess() {
        when(DAO.findById(testId)).thenReturn(person);

        final Response response = EXT.target("/persons/" + testId).request().get();
        Person found = response.readEntity(Person.class);

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(found.getId(), equalTo(person.getId()));
        verify(DAO).findById(testId);
    }

    @Test
    void getPersonByIdNotFound() {
        when(DAO.findById(2)).thenReturn(null);
        final Response response = EXT.target("/persons/2").request().get();

        assertThat(
                response.getStatusInfo().getStatusCode(),
                equalTo(Response.Status.NOT_FOUND.getStatusCode())
        );
        verify(DAO).findById(2);
    }

    @Test
    void getPersonByEmailSuccess() {
        when(DAO.findByEmail(testEmail)).thenReturn(person);

        Response response = EXT.target("/persons")
                .queryParam("email", testEmail)
                .request().get();
        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        List<Person> found = response.readEntity(new GenericType<List<Person>>() {
        });

        assertThat(found.size(), equalTo(1));
        assertThat(found.get(0).getId(), equalTo(person.getId()));
        verify(DAO).findByEmail(testEmail);
    }

    @Test
    void getPersonByEmailNotFound() {
        when(DAO.findByEmail("not@found.net")).thenReturn(null);

        final Response response = EXT.target("/persons")
                .queryParam("email", "not@found.net")
                .request().get();

        assertThat(
                response.getStatusInfo().getStatusCode(),
                equalTo(Response.Status.NOT_FOUND.getStatusCode())
        );
        verify(DAO).findByEmail("not@found.net");
    }

    @Test
    void getPerson_DBError() {
        when(DAO.findByEmail(testEmail)).thenThrow(new RuntimeException("database unavailable"));

        Response response = EXT.target("/persons")
                .queryParam("email", testEmail)
                .request().get();
        assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        ErrorResponse body = response.readEntity(ErrorResponse.class);
        assertThat(body.getError(), equalTo("database unavailable"));

        verify(DAO).findByEmail(testEmail);
    }

    @Test
    void addPersonSuccess() {
        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person.Request(testName, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));

        verify(DAO).insert(testName, testEmail);
    }

    @Test
    void addPersonBadRequest_Body() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new HashMap<>(), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);

        assertThat(errorResponse.getError(), equalTo("Validation failed"));
        assertThat(errorResponse.getViolations(), is(notNullValue()));
        assertThat(errorResponse.getViolations().size(), equalTo(2));
        errorResponse.getViolations().forEach(violation ->
            assertThat(
                violation,
                oneOf(Person.Request.NAME_NULL_ERROR, Person.Request.EMAIL_NULL_ERROR)
            )
        );
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_MissingAllData() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person.Request("", ""), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);

        assertThat(errorResponse.getError(), equalTo("Validation failed"));
        assertThat(errorResponse.getViolations(), is(notNullValue()));
        assertThat(errorResponse.getViolations().size(), equalTo(3));
        errorResponse.getViolations().forEach(violation ->
            assertThat(
                violation,
                oneOf(Person.Request.NAME_LENGTH_ERROR, Person.Request.EMAIL_LENGTH_ERROR, Person.Request.EMAIL_VALIDATION_ERROR)
            )
        );
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_MissingEmail() {

        final Response response = EXT.target("/persons")
                .request()
                .post(
                    Entity.entity(getClass().getResource("/fixtures/representations/personRequest_EmailNull.json"),
                    MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_EmailNull() {
        final Response response = EXT.target("/persons")
            .request()
            .post(Entity.entity(
                new Person.Request(testName, null),
                MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);

        assertThat(errorResponse.getError(), equalTo("Validation failed"));
        assertThat(errorResponse.getViolations(), is(notNullValue()));
        assertThat(errorResponse.getViolations().size(), equalTo(1));
        assertThat(
            errorResponse.getViolations().contains(Person.Request.EMAIL_NULL_ERROR),
            is(true)
        );
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_NameNull() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person.Request(null, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);

        assertThat(errorResponse.getError(), equalTo("Validation failed"));
        assertThat(errorResponse.getViolations(), is(notNullValue()));
        assertThat(errorResponse.getViolations().size(), equalTo(1));
        assertThat(
            errorResponse.getViolations().contains(Person.Request.NAME_NULL_ERROR),
            is(true)
        );
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void updatePersonSuccess() {
        String updatedName = "Samantha";

        when(DAO.findById(eq(testId))).thenReturn(person);
        when(DAO.updateById(eq(testId), eq(updatedName), eq(testEmail))).thenReturn(1);

        final Response response = EXT.target("/persons/" + testId)
                .request()
//                    .put(
//                        Entity.entity(
//                            getClass().getResource("/fixtures/representations/personRequest.json"),
//                            MediaType.APPLICATION_JSON_TYPE));
                .put(Entity.entity(new Person.Request(updatedName, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        int updated = response.readEntity(Integer.class);

        assertThat(updated, equalTo(1));
        verify(DAO).findById(eq(testId));
        verify(DAO).updateById(eq(testId), eq(updatedName), eq(testEmail));
    }

    @Test
    void updatePersonBadRequest_Body() {
        final Response response = EXT.target("/persons/" + testId)
                .request()
                .put(Entity.entity(new Person.Request(null, null), MediaType.APPLICATION_JSON_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);

        assertThat(errorResponse.getError(), equalTo("Validation failed"));
        assertThat(errorResponse.getViolations(), is(notNullValue()));
        assertThat(errorResponse.getViolations().size(), equalTo(2));
        errorResponse.getViolations().forEach(violation ->
            assertThat(
                violation,
                oneOf(Person.Request.NAME_NULL_ERROR, Person.Request.EMAIL_NULL_ERROR)
            )
        );
        verify(DAO, never()).updateById(anyInt(), anyString(), anyString());
        verify(DAO, never()).findById(anyInt());
    }

    @Test
    void updatePersonBadRequest_ID() {
        String updatedEmail = "Sam@newemail.com";
        final Response response = EXT.target("/persons/0")
                .request()
                .put(Entity.entity(new Person.Request("", updatedEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_INVALID_ID));
        verify(DAO, never()).updateById(anyInt(), anyString(), anyString());
        verify(DAO, never()).findById(anyInt());
    }

    @Test
    void updatePersonNotFound() {
        String updatedName = "Samwell";
        when(DAO.findById(eq(testId))).thenReturn(null);

        final Response response = EXT.target("/persons/" + "2")
                .request()
                .put(Entity.entity(new Person.Request(updatedName, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_ID_NOT_FOUND));
        verify(DAO).findById(eq(2));
        verify(DAO, never()).updateById(anyInt(), anyString(), anyString());
    }

    @Test
    void deletePersonSuccess() {
        final Response response = EXT.target("/persons/" + testId)
                .request().delete();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        verify(DAO).deleteById(eq(testId));
    }

    @Test
    void deletePersonBadRequest_ID() {
        final Response response = EXT.target("/persons/-1")
                .request().delete();

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_INVALID_ID));
        verify(DAO, never()).findById(anyInt());
        verify(DAO, never()).deleteById(eq(testId));
    }
}