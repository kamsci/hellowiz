package com.hellowiz.api.resources.middleware;

import com.hellowiz.api.api.errors.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.slf4j.LoggerFactory;

public class ViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException e) {
        // Log the exception
        logException(e);

        ErrorResponse errorResponse = new ErrorResponse(
            e.getMessage(),
            ErrorResponse.getMessagesFromViolations(e.getConstraintViolations()));

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(errorResponse)
            .build();
    }

    private void logException(Throwable exception) {
        LoggerFactory.getLogger(exception.getClass()).error("Constraint Violation Occurred:", exception);
    }
}
