package org.eventjuggler.tests;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.model.Group;
import org.eventjuggler.model.RSVP;
import org.eventjuggler.model.Role;
import org.eventjuggler.model.Tag;
import org.eventjuggler.model.User;
import org.eventjuggler.model.UserRole;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@RunWith(Arquillian.class)
public class JPADeploymentTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClasses(Address.class, Event.class, Group.class, Role.class, RSVP.class, Tag.class, User.class, UserRole.class)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
            .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @Test
    public void testDeployed() {

    }
}
