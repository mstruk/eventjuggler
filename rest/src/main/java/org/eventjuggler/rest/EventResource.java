/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.rest;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eventjuggler.model.User;
import org.eventjuggler.services.EventProperty;
import org.eventjuggler.services.EventQuery;
import org.eventjuggler.services.EventService;
import org.eventjuggler.services.UserService;
import org.picketlink.extensions.core.pbox.PicketBoxIdentity;
import org.picketlink.extensions.core.pbox.authorization.UserLoggedIn;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/event")
public class EventResource {

    @Inject
    private EventService eventService;

    @Inject
    private PicketBoxIdentity identity;

    @Inject
    private UserService userService;

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createEvent(Event event) {
        eventService.create(event.toInternal());
    }

    @GET
    @Path("/{id}/rsvp")
    @Produces(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public void createRSVP(@PathParam("id") long eventId) {
        eventService.attend(eventId, getUser());
    }

    @DELETE
    @Path("/{id}")
    public void deleteEvent(@PathParam("id") long eventId) {
        eventService.remove(eventService.getEvent(eventId));
    }

    @DELETE
    @Path("/{id}/rsvp")
    @UserLoggedIn
    public void deleteRSVP(@PathParam("id") long eventId) {
        eventService.resign(eventId, getUser());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public EventDetails getEvent(@PathParam("id") long eventId) {
        return new EventDetails(eventService.getEvent(eventId));
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEvents(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("query") String query, @QueryParam("tags") String tags, @QueryParam("sort") String sortBy,
            @QueryParam("order") String order) {
        EventQuery q = eventService.query();

        if (firstResult != null) {
            q.firstResult(firstResult);
        }

        if (maxResult != null) {
            q.maxResult(maxResult);
        }

        if (query != null) {
            q.query(query);
        }

        if (tags != null) {
            q.tags(ObjectFactory.createTags(tags));
        }

        if (sortBy != null) {
            q.sortBy(EventProperty.valueOf(sortBy.toUpperCase()), order == null || order.equals("asc"));
        }

        List<Event> events = new LinkedList<Event>();
        for (org.eventjuggler.model.Event e : q.getEvents()) {
            events.add(new Event(e));
        }
        return events;
    }

    @GET
    @Path("/mine")
    @Produces(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public List<Event> getMyEvents() {
        List<Event> events = new LinkedList<Event>();
        for (org.eventjuggler.model.Event e : eventService.getEvents(getUser())) {
            events.add(new Event(e));
        }
        return events;
    }

    private User getUser() {
        return userService.getUser(identity.getUser().getLoginName());
    }

}
