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
package org.eventjuggler.services;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.model.Group;
import org.eventjuggler.model.User;

@Singleton
@Startup
public class InitializeData {

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @PostConstruct
    public void init() {
        Calendar cal = Calendar.getInstance();

        User u = new User();
        u.setName("Jack");
        u.setLastName("Black");
        u.setLogin("jack");
        u.setPassword("password");
        u.setDescription("The owner of the Red Hat group");
        u.setImageId("user-1");
        em.persist(u);

        Address a = new Address();
        a.setCity("London");
        a.setCountry("United Kingdom");
        a.setImageId("address-1");
        a.setStreet("Featherstone Street");
        a.setStreetNum("49");
        a.setZip("EC1Y 8SY");
        em.persist(a);

        Group g = new Group();
        g.setDescription("Red Hat");
        g.setLocation(a);
        g.setImageId("group-1");
        g.setName("Red Hat");
        g.setOwner(u);
        em.persist(g);

        Event e = new Event();
        e.setTitle("Welcome to EventJuggler");
        e.setDescription("An introduction session to EventJuggler. What is this awesome project all about?");
        e.setImageId("event-1");
        e.setLocation(a);
        e.setOrganizer(u);
        e.setOrganizerGroup(g);
        e.setTags("meetings, events, tech");
        cal.set(2013, 06, 01, 12, 00);
        e.setTime(cal.getTimeInMillis());
        em.persist(e);

        u = new User();
        u.setName("Bob");
        u.setLastName("Barn");
        u.setLogin("bob");
        u.setPassword("password");
        u.setDescription("The owner of the Fedora group");
        u.setImageId("user-2");
        em.persist(u);

        a = new Address();
        a.setCity("Dallas, Texas");
        a.setCountry("United States");
        a.setName("Dallas Office");
        a.setImageId("address-2");
        a.setStreet("LBJ Freeway");
        a.setStreetNum("1501");
        a.setZip("75234");
        em.persist(a);

        g = new Group();
        g.setDescription("Fedora");
        g.setLocation(a);
        g.setName("Fedora");
        g.setImageId("group-2");
        g.setOwner(u);
        em.persist(g);

        e = new Event();
        e.setTitle("Fedora 18");
        e.setDescription("An introduction session to Fedora 18");
        e.setImageId("event-2");
        e.setLocation(a);
        e.setOrganizer(u);
        e.setOrganizerGroup(g);
        e.setTags("os, linux, tech");
        cal.set(2013, 03, 07, 16, 30);
        e.setTime(cal.getTimeInMillis());
        em.persist(e);

        u = new User();
        u.setName("Random");
        u.setLastName("Cat");
        u.setLogin("random");
        u.setPassword("password");
        u.setDescription("Some random person");
        u.setImageId("user-3");
        em.persist(u);
    }

}
