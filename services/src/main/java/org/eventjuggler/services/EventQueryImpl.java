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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.eventjuggler.model.Event;
import org.eventjuggler.model.Event_;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class EventQueryImpl implements EventQuery {

    private boolean ascending;

    private EntityManager em;
    private int firstResult = -1;

    private int maxResult = -1;
    private String query;
    private EventProperty sort;

    private String[] tags;

    public EventQueryImpl(EntityManager em) {
        this.em = em;
    }

    public EventQueryImpl firstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public List<Event> getEvents() {
        CriteriaBuilder b = em.getCriteriaBuilder();
        CriteriaQuery<Event> c = b.createQuery(Event.class);
        Root<Event> e = c.from(Event.class);

        if (query != null) {
            c.where(b.or(b.like(b.upper(e.get(Event_.title)), query), b.like(b.upper(e.get(Event_.description)), query)));
        }

        if (tags != null) {
            for (String t : tags) {
                c.where(b.like(b.upper(e.get(Event_.tags)), "%" + t.toUpperCase() + "%"));
            }
        }

        if (sort != null) {
            Path<?> p = null;
            switch (sort) {
                case TIME:
                    p = e.get(Event_.time);
                    break;
                case TITLE:
                    p = e.get(Event_.title);
                    break;
            }

            if (p != null) {
                c.orderBy(ascending ? b.asc(p) : b.desc(p));
            }
        }

        TypedQuery<Event> q = em.createQuery(c);

        if (firstResult != -1) {
            q.setFirstResult(firstResult);
        }

        if (maxResult != -1) {
            q.setMaxResults(maxResult);
        }

        return q.getResultList();
    }

    public EventQueryImpl maxResult(int maxResult) {
        this.maxResult = maxResult;
        return this;
    }

    public EventQueryImpl query(String query) {
        this.query = "%" + query.toUpperCase() + "%";
        return this;
    }

    public EventQueryImpl sortBy(EventProperty sort, boolean ascending) {
        this.sort = sort;
        this.ascending = ascending;
        return this;
    }

    @Override
    public EventQuery tags(String... tags) {
        this.tags = tags;
        return this;
    }

}
