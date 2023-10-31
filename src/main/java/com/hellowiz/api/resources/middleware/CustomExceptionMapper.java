package com.hellowiz.api.resources.middleware;

import com.hellowiz.api.api.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.slf4j.LoggerFactory;

public class CustomExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception
        logException(exception);

        // You can customize the response entity and status here
        // For example, return a JSON error response
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }

    private void logException(Throwable exception) {
        LoggerFactory.getLogger(exception.getClass()).error("Resource Error Occurred:", exception);
    }
}
