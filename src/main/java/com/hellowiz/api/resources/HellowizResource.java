package com.hellowiz.api.resources;

import com.codahale.metrics.annotation.Timed;
import com.hellowiz.api.api.Saying;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-wiz")
@Produces(MediaType.APPLICATION_JSON)
public class HellowizResource {
    // Jersey resource. Each resource class is associated with a URI template -> /hello-wiz
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public HellowizResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
