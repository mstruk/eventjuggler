package org.eventjuggler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ejb.EJB;

import org.eventjuggler.model.Event;
import org.eventjuggler.services.EventService;
import org.eventjuggler.services.EventServiceBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@RunWith(Arquillian.class)
public class EventServiceBeanTest {

    @Deployment @OverProtocol("Servlet 3.0")
    public static WebArchive createTestArchive() {
        return JPADeploymentTest.createTestArchive()
                .addClasses(EventService.class, EventServiceBean.class, DatabaseTools.class);
    }

    @EJB
    private DatabaseTools databaseTools;

    private Long eventId;

    @EJB
    private EventService service;

    @After
    public void after() throws Exception {
        databaseTools.cleanDatabase();
    }

    @Test
    public void createEvent() throws Exception {
        Event event = new Event();
        event.setDescription("description");
        event.setImageId("imageId");
        event.setTags("tag1, tag2");
        event.setTime(System.currentTimeMillis());
        event.setTitle("title");

        service.create(event);

        assertNotNull(event.getId());

        eventId = event.getId();
    }

    @Test
    public void removeEvent() throws Exception {
        createEvent();

        Event event = service.getEvent(eventId);
        assertNotNull(event);

        service.remove(event);

        event = service.getEvent(event.getId());
        assertNull(event);
    }

    @Test
    public void updateEvent() throws Exception {
        createEvent();

        Event event = service.getEvent(eventId);
        assertNotNull(event);

        event.setTitle("new-title");
        service.update(event);

        event = service.getEvent(event.getId());
        assertEquals("new-title", event.getTitle());
    }
}
