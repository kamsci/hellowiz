package com.hellowiz.api.resources.middleware;

import com.hellowiz.api.api.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.postgresql.util.PSQLException;
import org.slf4j.LoggerFactory;

public class CustomExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception
        logException(exception);
        ErrorResponse errorResponse;

        if (exception instanceof UnableToExecuteStatementException) {
            // Database error, likely a PSQLException, don't pass pull SQL statement error to client
            if (exception.getCause() instanceof PSQLException) {
                // Detail exposes exact SQL query, leave it out
                String[] error = exception.getMessage().split("Detail");
                errorResponse = new ErrorResponse(error[0]);
            } else {
                errorResponse = new ErrorResponse("Unable to save. Service might be unavailable or your request may have errors.");
            }
        } else {
            errorResponse = new ErrorResponse(exception.getMessage());
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }

    private void logException(Throwable exception) {
        LoggerFactory.getLogger(exception.getClass()).error("Resource Error Occurred:", exception);
    }
}
