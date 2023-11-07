package com.hellowiz.api;

import com.hellowiz.api.api.Person;
import com.hellowiz.api.db.PersonDAO;
import com.hellowiz.api.db.PersonMapper;
import com.hellowiz.api.health.ResourceHealthCheck;
import com.hellowiz.api.health.TemplateHealthCheck;
import com.hellowiz.api.resources.HellowizResource;
import com.hellowiz.api.resources.PersonResource;
import com.hellowiz.api.resources.middleware.DefaultExceptionMapper;
import com.hellowiz.api.resources.middleware.ExecuteExceptionMapper;
import com.hellowiz.api.resources.middleware.ViolationExceptionMapper;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import org.jdbi.v3.core.Jdbi;

public class hellowizApplication extends Application<hellowizConfiguration> {

    public static void main(final String[] args) throws Exception {
        new hellowizApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello wiz";
    }

    @Override
    public void initialize(final Bootstrap<hellowizConfiguration> bootstrap) {
        // Application initialization

        // Automatically unwrap any thrown SQLException or JdbiException instances.
        // This is critical for debugging, since otherwise only the common wrapper exception’s stack trace is logged.
        bootstrap.addBundle(new JdbiExceptionsBundle());
    }

    @Override
    public void run(final hellowizConfiguration configuration,
                    final Environment environment) {
        // Database connection
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        // On-demand instances have an open-ended lifecycle, as they obtain and release a connection for each method call.
        // They are thread-safe, and may be reused across an application.
        PersonDAO personDao = jdbi.onDemand(PersonDAO.class);
        jdbi.registerRowMapper(Person.class, new PersonMapper());

        // Resource - HelloWiz
        HellowizResource hellowizResource = new HellowizResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(hellowizResource);
        // HealthCheck - HelloWiz
        TemplateHealthCheck hellowizHealthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", hellowizHealthCheck);

        // Resource - Persons
        PersonResource personResource = new PersonResource(personDao);
        environment.jersey().register(personResource);
        // HealthCheck - Persons
        ResourceHealthCheck personResourceHealthCheck = new ResourceHealthCheck(personResource);
        environment.healthChecks().register("personResource", personResourceHealthCheck);

        // Resource Middleware - catch and log Exceptions then return standardized error response
        environment.jersey().register(ViolationExceptionMapper.class);
        environment.jersey().register(ExecuteExceptionMapper.class);
        environment.jersey().register(DefaultExceptionMapper.class);
    }
}
