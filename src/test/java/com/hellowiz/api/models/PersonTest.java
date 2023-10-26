package com.hellowiz.api.models;

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
        System.out.println(person.toString());
        assertThat(
                MAPPER.readValue(getClass().getResource("/fixtures/models/person.json"), Person.class),
                equalTo(person));
    }
}
