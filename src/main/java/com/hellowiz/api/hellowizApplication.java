package com.hellowiz.api;

import com.hellowiz.api.health.ResourceHealthCheck;
import com.hellowiz.api.health.TemplateHealthCheck;
import com.hellowiz.api.resources.HellowizResource;
import com.hellowiz.api.resources.PersonResource;
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
        PersonResource personResource = new PersonResource();
        environment.jersey().register(personResource);
        // HealthCheck - Persons
        ResourceHealthCheck personResourceHealthCheck = new ResourceHealthCheck(personResource);
        environment.healthChecks().register("personResource", personResourceHealthCheck);
    }

}
