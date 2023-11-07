package com.hellowiz.api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonTest {
    private static final ObjectMapper MAPPER = newObjectMapper();

    private static Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void serializesToJSON_Person() throws Exception {
        final Person person = new Person(1,"Betty Sue Who", "sweaty-betty@whoville.com");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(getClass().getResource("/fixtures/representations/person.json"), Person.class));

        assertThat(
                MAPPER.writeValueAsString(person),
                equalTo(expected)
        );
    }
    @Test
    public void deserializesFromJSON_Person() throws Exception {
        final Person person = new Person(1, "Betty Sue Who", "sweaty-betty@whoville.com");
        assertThat(
                MAPPER.readValue(getClass().getResource("/fixtures/representations/person.json"), Person.class),
                equalTo(person));
    }

    @Test
    public void serializesToJSON_PersonRequest() throws Exception {
        final Person.Request person = new Person.Request("Betty Sue Who", "sweaty-betty@whoville.com");

        final String expected = MAPPER.writeValueAsString(
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest.json"), Person.Request.class));

        assertThat(
            MAPPER.writeValueAsString(person),
            equalTo(expected)
        );
    }

    @Test
    public void deserializesFromJSON_PersonRequest() throws Exception {
        final Person.Request person = new Person.Request("Betty Sue Who", "sweaty-betty@whoville.com");
        assertThat(
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest.json"), Person.Request.class),
            equalTo(person));
    }

    /*
        Person.Request Validations
     */
    @Test
    public void personRequest_EmailNull() throws Exception {
        Person.Request personNoEmail =
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest_EmailNull.json"), Person.Request.class);

        Set<ConstraintViolation<Person.Request>> constraintViolations =
            validator.validate( personNoEmail );

        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.iterator().next().getMessage(), equalTo("Email must not be null"));
    }

    @Test
    public void personRequest_EmailInvalid() throws Exception {
        Person.Request personNoEmail =
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest_EmailInvalid.json"), Person.Request.class);

        Set<ConstraintViolation<Person.Request>> constraintViolations =
            validator.validate( personNoEmail );

        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.iterator().next().getMessage(), equalTo("Please provide a valid email address"));
    }

    @Test
    public void personRequest_NameEmpty() throws Exception {
        Person.Request personNoName =
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest_NameEmpty.json"), Person.Request.class);

        Set<ConstraintViolation<Person.Request>> constraintViolations =
            validator.validate( personNoName );

        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.iterator().next().getMessage(), equalTo("Name must be between 2 and 100 characters long"));
    }

    @Test
    public void personRequest_NameNull() throws Exception {
        Person.Request personNoName =
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest_NameNull.json"), Person.Request.class);

        Set<ConstraintViolation<Person.Request>> constraintViolations =
            validator.validate( personNoName );

        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.iterator().next().getMessage(), equalTo("Name must not be null"));
    }

    @Test
    public void personRequest_NameTooLong() throws Exception {
        Person.Request personNoName =
            MAPPER.readValue(getClass().getResource("/fixtures/representations/personRequest_NameInvalid.json"), Person.Request.class);

        Set<ConstraintViolation<Person.Request>> constraintViolations =
            validator.validate( personNoName );

        assertThat(constraintViolations.size(), equalTo(1));
        assertThat(constraintViolations.iterator().next().getMessage(), equalTo("Name must be between 2 and 100 characters long"));
    }
}
