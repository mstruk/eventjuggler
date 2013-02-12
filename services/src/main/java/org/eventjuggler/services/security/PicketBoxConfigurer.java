package org.eventjuggler.services.security;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.identity.jpa.EntityManagerLookupStrategy;

public class PicketBoxConfigurer {

    @Inject
    private EntityManagerLookupStrategy entityManagerLookupStrategy;

    @Produces
    public ConfigurationBuilder produceConfiguration() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.identityManager().jpaStore().entityManagerLookupStrategy(this.entityManagerLookupStrategy);

        builder.sessionManager().inMemorySessionStore();

        return builder;
    }

}