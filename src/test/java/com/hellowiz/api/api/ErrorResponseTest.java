package com.hellowiz.api.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellowiz.api.api.errors.ErrorResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ErrorResponseTest {
    private static final ObjectMapper MAPPER = newObjectMapper();

    @Test
    public void serializesToJSON_ErrorResponse() throws Exception {
        final ErrorResponse errorResponse = new ErrorResponse("Test error message", Collections.emptySet());

        final String expected = MAPPER.writeValueAsString(
            MAPPER.readValue(getClass().getResource("/fixtures/representations/errorResponse_emptyViolations.json"), ErrorResponse.class));

        assertThat(
            MAPPER.writeValueAsString(errorResponse),
            equalTo(expected)
        );
    }
//    @Test
//    public void deserializesFromJSON_ErrorResponse() throws Exception {
//        final ErrorResponse errorResponse = new ErrorResponse("Test error message", Collections.emptySet());
//
//        assertThat(
//            MAPPER.readValue(getClass().getResource("/fixtures/representations/errorResponse_emptyViolations.json"), ErrorResponse.class),
//            equalTo(errorResponse));
//    }
}
