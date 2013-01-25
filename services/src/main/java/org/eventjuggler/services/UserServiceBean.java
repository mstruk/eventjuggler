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

import org.eventjuggler.model.Group;
import org.eventjuggler.model.User;
import org.eventjuggler.model.UserRole;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class UserServiceBean implements UserService {

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @Override
    public void create(Group group) {
        em.persist(group);
    }

    @Override
    public void create(User user) {
        em.persist(user);
    }

    @Override
    public void create(UserRole userRole) {
        em.persist(userRole);
    }

    @Override
    public Group getGroup(Long id) {
        return em.find(Group.class, id);
    }

    @Override
    public List<Group> getGroups() {
        return em.createQuery("from Group g", Group.class).getResultList();
    }

    @Override
    public List<Group> getGroups(User user) {
        return em.createQuery("select g from Group g join g.members m where m.user = :user", Group.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public User getUser(String login) {
        List<User> users = em.createQuery("select u from User u where u.login = :login", User.class)
            .setParameter("login", login)
            .getResultList();

        if (users.size() > 1)
            throw new IllegalStateException("More than one user for login: " + login);

        return users.size() == 0 ? null : users.get(0);
    }

    @Override
    public UserRole getUserRole(Long id) {
        return em.find(UserRole.class, id);
    }

    @Override
    public void remove(Group group) {
        em.remove(group);
    }

    @Override
    public void remove(User user) {
        em.remove(user);
    }

    @Override
    public void remove(UserRole userRole) {
        em.remove(userRole);
    }

    @Override
    public void update(Group group) {
        em.merge(group);
    }

    @Override
    public void update(User user) {
        em.merge(user);
    }

    @Override
    public void update(UserRole userRole) {
        em.merge(userRole);
    }
}
