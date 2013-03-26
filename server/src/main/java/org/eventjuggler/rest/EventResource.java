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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.services.AddressService;
import org.eventjuggler.services.EventProperty;
import org.eventjuggler.services.EventQuery;
import org.eventjuggler.services.EventService;
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

    @Context
    private UriInfo uriInfo;

    @Inject
    AddressService addressService;

    @POST
    @Path("/location")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createLocation(Address address) {
        save(address);
    }

    private Address save(Address address) {
        if (address != null) {
            if (address.getId() != null) {
                return addressService.update(address);
            } else {
                addressService.create(address);
                return address;
            }
        } else {
            return null;
        }
    }

    private void save(Event event) {
        if (event != null) {
            if (event.getId() != null) {
                eventService.update(event);
            } else {
                eventService.create(event);
            }
        }
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
        Address address = addressService.getAddress(locationId);
        if (address == null) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        } else {
            return address;
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
            return addresses;
        }
    }

    @POST
    @Path("/event")
    @Consumes(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public void createEvent(Event event) {
        if (event != null) {
            event.setOrganizer(identity.getUser().getLoginName());
            Address address = save(event.getLocation());
            event.setLocation(address);
            save(event);
        }
    }

    @GET
    @Path("/rsvp/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public void createRSVP(@PathParam("id") long eventId) {
        eventService.attend(eventId, identity.getUser().getLoginName());
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
        eventService.resign(eventId, identity.getUser().getLoginName());
    }

    @GET
    @Path("/event/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event getEvent(@PathParam("id") long eventId, @Context HttpServletResponse response) {
        Event event = eventService.getEvent(eventId);
        if (event == null) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return null;
        } else {
            return event;
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
                Event e = eventService.getEvent(eventId);
                if (e != null) {
                    events.add(e);
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
                Event e = eventService.getEvent(relatedEventId);
                if (e != null) {
                    events.add(e);
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
            q.tags(Event.convertTags(tags));
        }

        if (sortBy != null) {
            q.sortBy(EventProperty.valueOf(sortBy.toUpperCase()), order == null || order.equals("asc"));
        }

        return q.getEvents();
    }

    @GET
    @Path("/events/mine")
    @Produces(MediaType.APPLICATION_JSON)
    @UserLoggedIn
    public List<Event> getMyEvents() {
        return eventService.getEvents(identity.getUser().getLoginName());
    }

    @PostConstruct
    public void init() {
        if (!analyticsInstance.isUnsatisfied()) {
            analytics = analyticsInstance.get();
        }
    }

}
