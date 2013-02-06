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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@XmlRootElement
public class User {

    private String description;
    private String imageId;
    private String lastName;
    private String login;
    private String name;
    private String password;

    public User() {
    }

    public User(org.eventjuggler.model.User u) {
        this.description = u.getDescription();
        this.imageId = u.getImageId();
        this.lastName = u.getLastName();
        this.login = u.getLogin();
        this.name = u.getName();
        this.password = u.getPassword();
    }

    public String getDescription() {
        return description;
    }

    public String getImageId() {
        return imageId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }


    public org.eventjuggler.model.User toInternal() {
        org.eventjuggler.model.User u = new org.eventjuggler.model.User();
        u.setDescription(description);
        u.setImageId(imageId);
        u.setLastName(lastName);
        u.setLogin(login);
        u.setName(name);
        u.setPassword(password);
        return u;
    }

}
