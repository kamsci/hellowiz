package com.hellowiz.api.resources.middleware;

import com.hellowiz.api.api.errors.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.postgresql.util.PSQLException;
import org.slf4j.LoggerFactory;

public class ExecuteExceptionMapper implements ExceptionMapper<UnableToExecuteStatementException> {
    @Override
    public Response toResponse(UnableToExecuteStatementException e) {
        // Log the exception
        logException(e);
        ErrorResponse errorResponse = null;

        // Database error, likely a PSQLException, don't pass full SQL statement error to client
        if (e.getCause() instanceof PSQLException) {
            // Detail exposes exact SQL query, leave it out
            String[] error = e.getMessage().split("Detail");
            errorResponse = new ErrorResponse(error[0]);
        } else {
            errorResponse = new ErrorResponse("Unable to save. Service might be unavailable or your request may have errors.");
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(errorResponse)
            .build();
    }


    private void logException(Throwable exception) {
        LoggerFactory.getLogger(exception.getClass()).error("Database Error Occurred:", exception);
    }
}
