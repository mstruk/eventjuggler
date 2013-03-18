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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.model.User;
import org.eventjuggler.services.AddressService;
import org.eventjuggler.services.EventProperty;
import org.eventjuggler.services.EventQuery;
import org.eventjuggler.services.EventService;
import org.eventjuggler.services.UserService;
import org.eventjuggler.services.analytics.Analytics;
import org.eventjuggler.services.analytics.AnalyticsQuery;
import org.picketlink.extensions.core.pbox.PicketBoxIdentity;
import org.picketlink.extensions.core.pbox.authorization.UserLoggedIn;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/")
public class EventResource {

    private Analytics analytics;

    @Inject
    private Instance<Analytics> analyticsInstance;

    @Inject
    private EventService eventService;

    @Inject
    private PicketBoxIdentity identity;

    @Inject
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @Inject
    AddressService addressService;

    @PUT
    @Path("/location")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createLocation(Address address) {
        createOrUpdate(address);
    }

    private org.eventjuggler.model.Address createOrUpdate(Address address) {
        if (address != null) {
            final org.eventjuggler.model.Address existing = addressService.getAddress(address.getId());
            final org.eventjuggler.model.Address result = address.toInternal();
            if (existing != null) {
                return addressService.update(result);
            } else {
                addressService.create(result);
                return result;
            }
        }

        return null;
    }

    @DELETE
    @Path("/location/{id}")
    public void deleteLocation(@PathParam("id") long locationId) {
        addressService.remove(addressService.getAddress(locationId));
    }

    @GET
    @Path("/location/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Address getLocation(@PathParam("id") long locationId, @Context HttpServletResponse response) {
        final org.eventjuggler.model.Address address = addressService.getAddress(locationId);
        if (address == null) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        } else {
            return new Address(address);
        }
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Address> getLocations(@Context HttpServletResponse response) {
        final List<org.eventjuggler.model.Address> addresses = addressService.getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        } else {
            List<Address> result = new ArrayList<Address>(addresses.size());
            for (org.eventjuggler.model.Address address : addresses) {
                result.add(new Address(address));
            }
            return result;
        }
    }

    @PUT
    @Path("/event")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createEvent(Event event) {
        if (event != null) {
            final org.eventjuggler.model.Event existing = eventService.getEvent(event.getId());

            // check that location exists and create it if needed
            final Address location = event.getLocation();
            final org.eventjuggler.model.Address address = createOrUpdate(location);

            // update the event with the persisted location
            final org.eventjuggler.model.Event modelEvent = event.toInternal();
            modelEvent.setLocation(address);

            if (existing != null) {
                eventService.update(modelEvent);
            } else {
                eventService.create(modelEvent);
            }
        }
    }

    @GET
    @Path("/rsvp/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public void createRSVP(@PathParam("id") long eventId) {
        eventService.attend(eventId, getUser());
    }

    @DELETE
    @Path("/event/{id}")
    public void deleteEvent(@PathParam("id") long eventId) {
        eventService.remove(eventService.getEvent(eventId));
    }

    @DELETE
    @Path("/rsvp/{id}")
    @UserLoggedIn
    public void deleteRSVP(@PathParam("id") long eventId) {
        eventService.resign(eventId, getUser());
    }

    @GET
    @Path("/event/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public EventDetails getEvent(@PathParam("id") long eventId, @Context HttpServletResponse response) {
        final org.eventjuggler.model.Event event = eventService.getEvent(eventId);
        if (event == null) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        } else {
            return new EventDetails(event);
        }
    }

    @GET
    @Path("/events/popular")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getPopular(@QueryParam("max") Integer maxResult) {
        List<Event> events = new LinkedList<Event>();

        if (analytics != null) {
            AnalyticsQuery query = analytics.createQuery().page(uriInfo.getBaseUri().getPath() + "event/%");

            if (maxResult == null) {
                maxResult = 5;
            }

            List<String> popularPage = query.getPopularPages();
            for (String p : popularPage) {
                long eventId = Long.parseLong(p.substring(p.lastIndexOf('/') + 1));
                org.eventjuggler.model.Event e = eventService.getEvent(eventId);
                if (e != null) {
                    events.add(new Event(e));
                }
            }
        }

        return events;
    }

    @GET
    @Path("/events/related/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getRelated(@PathParam("id") long eventId, @QueryParam("max") Integer maxResult) {
        List<Event> events = new LinkedList<Event>();

        if (analytics != null) {
            AnalyticsQuery query = analytics.createQuery().page(uriInfo.getBaseUri().getPath() + "event/%");

            if (maxResult == null) {
                maxResult = 5;
            }

            List<String> popularPage = query.getRelatedPages(uriInfo.getBaseUri().getPath() + "event/" + eventId);
            for (String p : popularPage) {
                long relatedEventId = Long.parseLong(p.substring(p.lastIndexOf('/') + 1));
                org.eventjuggler.model.Event e = eventService.getEvent(relatedEventId);
                if (e != null) {
                    events.add(new Event(e));
                }
            }
        }

        return events;
    }

    @GET
    @Path("/events")
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
    @Path("/events/mine")
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

    @PostConstruct
    public void init() {
        if (!analyticsInstance.isUnsatisfied()) {
            analytics = analyticsInstance.get();
        }
    }

}
