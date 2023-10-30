package com.hellowiz.api;

import com.hellowiz.api.db.PersonInMemory;
import com.hellowiz.api.health.ResourceHealthCheck;
import com.hellowiz.api.health.TemplateHealthCheck;
import com.hellowiz.api.resources.HellowizResource;
import com.hellowiz.api.resources.PersonResource;
import com.hellowiz.api.resources.middleware.CustomExceptionMapper;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

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
        // TODO: application initialization
    }

    @Override
    public void run(final hellowizConfiguration configuration,
                    final Environment environment) {
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
        PersonInMemory personDao = new PersonInMemory();
        PersonResource personResource = new PersonResource(personDao);
        environment.jersey().register(personResource);
        // HealthCheck - Persons
        ResourceHealthCheck personResourceHealthCheck = new ResourceHealthCheck(personResource);
        environment.healthChecks().register("personResource", personResourceHealthCheck);

        // Resource Middleware - catch and log Exceptions then return standardized error response
        environment.jersey().register(CustomExceptionMapper.class);

    }

}
