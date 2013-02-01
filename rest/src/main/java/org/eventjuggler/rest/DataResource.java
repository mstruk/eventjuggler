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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eventjuggler.services.DataService;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/data")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class DataResource {

    @Inject
    private DataService dataService;

    @GET
    @Path("/clear")
    public String clear() {
        dataService.clear();
        return "";
    }

    @GET
    @Path("/")
    public String exportData() {
        return dataService.exportData();
    }

    @POST
    @Path("/")
    public void importData(String data) {
        dataService.importData(data);
    }

    @GET
    @Path("/steal")
    public List<Event> stealFromMeetup(@QueryParam("category") String category, @QueryParam("page") String page,
            @QueryParam("key") String key) {
        List<org.eventjuggler.model.Event> events = dataService.stealFromMeetup(category, page, key);

        List<Event> l = new LinkedList<Event>();
        for (org.eventjuggler.model.Event e : events) {
            l.add(new Event(e));
        }
        return l;
    }
}
