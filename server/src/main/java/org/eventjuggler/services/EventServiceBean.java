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

import java.util.List;
import java.util.ListIterator;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.model.RSVP;
import org.eventjuggler.model.RSVP.Response;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class EventServiceBean implements EventService {

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @Override
    public RSVP attend(long eventId, String user) {
        Event event = getEvent(eventId);

        RSVP rsvp = new RSVP();
        rsvp.setResponse(Response.WILL_ATTEND);
        rsvp.setUser(user);
        em.persist(rsvp);

        event.getAttendance().add(rsvp);
        em.merge(event);

        return rsvp;
    }

    @Override
    public Event getEvent(long id) {
        return em.find(Event.class, id);
    }

    @Override
    public List<Event> getEvents() {
        return em.createQuery("from Event", Event.class).getResultList();
    }

    @Override
    public List<Event> getEvents(String user) {
        return em.createQuery("select e from Event e join e.attendance a where a.user = :user", Event.class)
                .setParameter("user", user).getResultList();
    }

    @Override
    public EventQueryImpl query() {
        return new EventQueryImpl(em);
    }

    @Override
    public void remove(Event event) {
        em.remove(em.merge(event));
    }

    @Override
    public void resign(long eventId, String user) {
        Event event = getEvent(eventId);

        ListIterator<RSVP> itr = event.getAttendance().listIterator();
        while (itr.hasNext()) {
            RSVP r = itr.next();
            if (r.getUser().equals(user)) {
                itr.remove();
                em.remove(r);
            }
        }

        em.merge(event);
    }

    private Address save(Address address) {
        return save(address, address.getId());
    }

    @Override
    public Event save(Event event) {
        if (event.getLocation() != null) {
            event.setLocation(save(event.getLocation()));
        }

        if (event.getAttendance() != null) {
            ListIterator<RSVP> itr = event.getAttendance().listIterator();
            while (itr.hasNext()) {
                RSVP r = itr.next();
                itr.set(save(r, r.getId()));
            }
        }

        save(event, event.getId());
        return event;
    }

    private <T> T save(T entity, Long id) {
        if (id == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        return entity;
    }

}
