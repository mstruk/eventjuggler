package org.eventjuggler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ejb.EJB;

import org.eventjuggler.model.Event;
import org.eventjuggler.services.EventQuery;
import org.eventjuggler.services.EventQueryImpl;
import org.eventjuggler.services.EventService;
import org.eventjuggler.services.EventServiceBean;
import org.eventjuggler.services.EventProperty;
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

    @Deployment
    @OverProtocol("Servlet 3.0")
    public static WebArchive createTestArchive() {
        return JPADeploymentTest.createTestArchive().addClasses(EventService.class, EventServiceBean.class, EventQuery.class,
                EventProperty.class, EventQueryImpl.class, DatabaseTools.class);
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

    @Test
    public void query() throws Exception {
        for (int i = 0; i < 10; i++) {
            Event e = new Event();
            e.setTitle("title" + i);
            e.setDescription("description" + i);
            e.setTime(i * 1000);
            e.setTags("tag" + i);
            service.create(e);
        }

        assertEquals(10, service.query().getEvents().size());

        assertEquals(1, service.query().query("title2").getEvents().size());
        assertEquals(1, service.query().query("TiTlE2").getEvents().size());
        assertEquals(1, service.query().query("description2").getEvents().size());
        assertEquals(1, service.query().tags("tag2").getEvents().size());

        List<Event> events = service.query().sortBy(EventProperty.TIME, false).firstResult(2).maxResult(2).getEvents();
        assertEquals(2, events.size());
        assertEquals("title7", events.get(0).getTitle());
        assertEquals("title6", events.get(1).getTitle());
    }
}
