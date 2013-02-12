package org.eventjuggler.security;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer {

    @Produces
    @PersistenceContext(unitName = "eventjuggler-idm")
    private EntityManager em;

}
