package com.hellowiz.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.hellowiz.api.resources.PersonResource;

public class ResourceHealthCheck extends HealthCheck {

    final PersonResource personResource;

    public ResourceHealthCheck(PersonResource personResource) {
        this.personResource = personResource;
    }

    @Override
    protected Result check() throws Exception {
        if (personResource.isHealthy()) {
            return Result.healthy();
        }
        return Result.unhealthy("person resource is missing resources");

    }
}
