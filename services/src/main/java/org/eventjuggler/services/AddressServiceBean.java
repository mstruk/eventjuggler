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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eventjuggler.model.Address;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class AddressServiceBean implements AddressService {

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @Override
    public void create(Address event) {
        em.persist(event);
    }

    @Override
    public Address getAddress(long id) {
        return em.find(Address.class, id);
    }

    @Override
    public List<Address> getAddresses() {
        return em.createQuery("from Address", Address.class).getResultList();
    }

    @Override
    public void remove(Address event) {
        em.remove(em.merge(event));
    }

    @Override
    public void update(Address event) {
        em.merge(event);
    }

}
