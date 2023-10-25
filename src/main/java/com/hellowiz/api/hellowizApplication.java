package com.hellowiz.api;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class hellowizApplication extends Application<hellowizConfiguration> {

    public static void main(final String[] args) throws Exception {
        new hellowizApplication().run(args);
    }

    @Override
    public String getName() {
        return "hellowiz";
    }

    @Override
    public void initialize(final Bootstrap<hellowizConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final hellowizConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
