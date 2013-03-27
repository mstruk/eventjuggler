package com.eventjuggler.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.services.EventProperty;
import org.eventjuggler.services.EventService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eventjuggler.test.BeanUtils;
import com.eventjuggler.test.Deployments;

@RunWith(Arquillian.class)
public class EventServiceIT {

    @Deployment(name = "eventjuggler-server", order = 1, testable = false)
    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        return Deployments.getEventJugglerServer();
    }

    @Deployment(name = "eventjuggler-test", order = 2, testable = true)
    public static WebArchive getTestArchive() throws IllegalArgumentException, Exception {
        return Deployments.getTestArchive(EventServiceIT.class);
    }

    private Long eventId;

    private String user;

    private EventService eventService;

    @After
    public void after() throws Exception {
        for (Event e : eventService.query().getEvents()) {
            eventService.remove(e);
        }
    }

    @Before
    public void before() throws Exception {
        eventService = BeanUtils.lookupBean(EventService.class);
        user = "test-user";
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void createEvent() throws Exception {
        Event event = new Event();
        event.setDescription("description");
        event.setImageId("imageId");
        event.setTags("tag1", "tag2");
        event.setTime(System.currentTimeMillis());
        event.setTitle("title");

        Address address = new Address();
        address.setCity("Bergen");
        address.setCountry("NO");

        eventService.save(event);

        assertNotNull(event.getId());

        eventId = event.getId();
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void query() throws Exception {
        for (int i = 0; i < 10; i++) {
            Event e = new Event();
            e.setTitle("title" + i);
            e.setDescription("description" + i);
            e.setTime(i * 1000);
            e.setTags("tag" + i);
            eventService.save(e);
        }

        assertEquals(10, eventService.query().getEvents().size());

        assertEquals(1, eventService.query().query("title2").getEvents().size());
        assertEquals(1, eventService.query().query("TiTlE2").getEvents().size());
        assertEquals(1, eventService.query().query("description2").getEvents().size());
        assertEquals(1, eventService.query().tags("tag2").getEvents().size());

        List<Event> events = eventService.query().sortBy(EventProperty.TIME, false).firstResult(2).maxResult(2).getEvents();
        assertEquals(2, events.size());
        assertEquals("title7", events.get(0).getTitle());
        assertEquals("title6", events.get(1).getTitle());
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void rsvp() throws Exception {
        createEvent();

        eventService.attend(eventId, user);

        Event event = eventService.getEvent(eventId);
        assertEquals(1, event.getAttendance().size());
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void resign() throws Exception {
        rsvp();

        eventService.resign(eventId, user);

        Event event = eventService.getEvent(eventId);
        assertTrue(event.getAttendance().isEmpty());
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void removeEvent() throws Exception {
        rsvp();

        Event event = eventService.getEvent(eventId);
        assertNotNull(event);

        eventService.remove(event);

        event = eventService.getEvent(event.getId());
        assertNull(event);
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void queryMine() throws Exception {
        rsvp();

        List<Event> events = eventService.query().user(user).getEvents();
        assertEquals(1, events.size());

        eventService.resign(eventId, user);

        events = eventService.query().user(user).getEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
    public void updateEvent() throws Exception {
        createEvent();

        Event event = eventService.getEvent(eventId);
        assertNotNull(event);

        event.setTitle("new-title");
        eventService.save(event);

        event = eventService.getEvent(event.getId());
        assertEquals("new-title", event.getTitle());
    }

}
