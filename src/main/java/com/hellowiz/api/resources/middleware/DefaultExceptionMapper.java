package com.hellowiz.api.resources.middleware;

import com.hellowiz.api.api.errors.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.slf4j.LoggerFactory;

public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception
        logException(exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());;

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }

    private void logException(Throwable exception) {
        LoggerFactory.getLogger(exception.getClass()).error("Resource Error Occurred:", exception);
    }
}
