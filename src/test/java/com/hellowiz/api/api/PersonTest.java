package com.hellowiz.api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonTest {
    private static final ObjectMapper MAPPER = newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final Person person = new Person("Betty Sue Who", "sweaty-betty@whoville.com");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(getClass().getResource("/fixtures/models/person.json"), Person.class));

        assertThat(
                MAPPER.writeValueAsString(person),
                equalTo(expected)
        );
    }
    @Test
    public void deserializesFromJSON() throws Exception {
        final Person person = new Person("Betty Sue Who", "sweaty-betty@whoville.com");
        assertThat(
                MAPPER.readValue(getClass().getResource("/fixtures/models/person.json"), Person.class),
                equalTo(person));
    }

    @Test
    public void updatePersonName() throws Exception {
        Person existingPerson = new Person("Sam", "sam@memail.net");
        Person nameUpdate = MAPPER.readValue(getClass().getResource("/fixtures/models/personNameUpdate.json"), Person.class);

        Person updatedPerson = existingPerson.update(nameUpdate);
        assertThat(updatedPerson.getEmail(), equalTo("sam@memail.net"));
        assertThat(updatedPerson.getName(), equalTo("Samwell"));
        assertThat(existingPerson, equalTo(updatedPerson));
    }

    @Test
    public void updatePersonEmail() throws Exception {
        Person existingPerson = new Person("Sam", "sam@memail.net");
        Person nameUpdate = MAPPER.readValue(getClass().getResource("/fixtures/models/personEmailUpdate.json"), Person.class);

        Person updatedPerson = existingPerson.update(nameUpdate);
        assertThat(updatedPerson.getEmail(), equalTo("sam-is-cool@memail.net"));
        assertThat(updatedPerson.getName(), equalTo("Sam"));
        assertThat(existingPerson, equalTo(updatedPerson));
    }

    @Test
    public void updatePersonEmpty() throws Exception {
        Person existingPerson = new Person("Sam", "sam@memail.net");
        Person nameUpdate = MAPPER.readValue("{}", Person.class);

        Person updatedPerson = existingPerson.update(nameUpdate);
        assertThat(updatedPerson.getEmail(), equalTo("sam@memail.net"));
        assertThat(updatedPerson.getName(), equalTo("Sam"));
        assertThat(existingPerson, equalTo(updatedPerson));
    }
}
