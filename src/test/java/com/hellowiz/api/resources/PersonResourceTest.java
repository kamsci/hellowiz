package com.hellowiz.api.resources;

import com.hellowiz.api.api.ErrorResponse;
import com.hellowiz.api.api.Person;
import com.hellowiz.api.db.PersonDAO;
import com.hellowiz.api.resources.middleware.CustomExceptionMapper;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class PersonResourceTest {
    private final long testId = 1L;
    private final String testName = "Sam";
    private final String testEmail = "sam@test.com";
    private static final PersonDAO DAO = mock(PersonDAO.class);
    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new PersonResource(DAO))
            .addProvider(CustomExceptionMapper.class)
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
        List<Person> found = response.readEntity(new GenericType<List<Person>>() {
        });

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
        when(DAO.findById(2L)).thenReturn(null);
        final Response response = EXT.target("/persons/2").request().get();

        assertThat(
                response.getStatusInfo().getStatusCode(),
                equalTo(Response.Status.NOT_FOUND.getStatusCode())
        );
        verify(DAO).findById(2L);
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
        when(DAO.insert(eq(testName), eq(testEmail))).thenReturn(person);

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person(testName, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));

        Person created = response.readEntity(Person.class);

        assertThat(created, is(notNullValue()));
        assertThat(created.getName(), equalTo(testName));
        assertThat(created.getEmail(), equalTo(testEmail));
        assertThat(created.getId(), equalTo(testId));
        verify(DAO).insert(testName, testEmail);
    }

    @Test
    void addPersonBadRequest_Body() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new HashMap<>(), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        Person created = response.readEntity(Person.class);

        assertThat(created, is(nullValue()));
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_MissingAllData() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person("", ""), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        Person created = response.readEntity(Person.class);

        assertThat(created, is(nullValue()));
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_MissingEmail() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person(testName, null), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        Person created = response.readEntity(Person.class);

        assertThat(created, is(nullValue()));
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void addPersonBadRequest_MissingName() {

        final Response response = EXT.target("/persons")
                .request()
                .post(Entity.entity(new Person(null, testEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        Person created = response.readEntity(Person.class);

        assertThat(created, is(nullValue()));
        verify(DAO, never()).insert(anyString(), anyString());
    }

    @Test
    void updatePersonSuccess() {
        String updatedName = "Samantha";
        Person updatedPerson = person;
        updatedPerson.setName(updatedName);

        when(DAO.findById(eq(testId))).thenReturn(person);
        when(DAO.updateById(eq(testId), eq(updatedName), eq(""))).thenReturn(updatedPerson);

        final Response response = EXT.target("/persons/" + testId)
                .request()
                .put(Entity.entity(new Person(updatedName, ""), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        Person updated = response.readEntity(Person.class);

        assertThat(updated, is(notNullValue()));
        assertThat(updated.getName(), equalTo(updatedName));
        assertThat(updated.getEmail(), equalTo(testEmail));
        assertThat(updated.getId(), equalTo(testId));
        verify(DAO).findById(eq(testId));
        verify(DAO).updateById(eq(testId), eq(updatedName), eq(""));
    }

    @Test
    void updatePersonBadRequest_Body() {
        final Response response = EXT.target("/persons/" + testId)
                .request()
                .put(Entity.entity(new Person(null, null), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_NO_VALID_FIELDS));
        verify(DAO, never()).updateById(anyLong(), anyString(), anyString());
        verify(DAO, never()).findById(anyLong());
    }

    @Test
    void updatePersonBadRequest_ID() {
        String updatedEmail = "Sam@newemail.com";
        final Response response = EXT.target("/persons/0")
                .request()
                .put(Entity.entity(new Person("", updatedEmail), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_INVALID_ID));
        verify(DAO, never()).updateById(anyLong(), anyString(), anyString());
        verify(DAO, never()).findById(anyLong());
    }

    @Test
    void updatePersonNotFound() {
        String updatedName = "Samwell";
        when(DAO.findById(eq(testId))).thenReturn(null);

        final Response response = EXT.target("/persons/" + "2")
                .request()
                .put(Entity.entity(new Person(updatedName, ""), MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_ID_NOT_FOUND));
        verify(DAO).findById(eq(2L));
        verify(DAO, never()).updateById(anyLong(), anyString(), anyString());
    }

    @Test
    void deletePersonSuccess() {
        when(DAO.deleteById(eq(testId))).thenReturn(person);

        final Response response = EXT.target("/persons/" + testId)
                .request().delete();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        Person deleted = response.readEntity(Person.class);

        assertThat(deleted, is(notNullValue()));
        assertThat(deleted.getName(), equalTo(testName));
        assertThat(deleted.getEmail(), equalTo(testEmail));
        assertThat(deleted.getId(), equalTo(testId));
        verify(DAO).deleteById(eq(testId));
    }

    @Test
    void deletePersonBadRequest_ID() {
        final Response response = EXT.target("/persons/-1")
                .request().delete();

        assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

        String updateMessage = response.readEntity(String.class);

        assertThat(updateMessage, equalTo(PersonResource.ERROR_INVALID_ID));;
        verify(DAO, never()).findById(anyLong());
        verify(DAO, never()).deleteById(eq(testId));
    }
}