package org.eventjuggler.tests;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
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

    @Deployment @OverProtocol("Servlet 3.0")
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(org.eventjuggler.model.Event.class.getPackage())
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
            .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @Test
    public void testDeployed() {
        assertNotNull(em);
    }
}
