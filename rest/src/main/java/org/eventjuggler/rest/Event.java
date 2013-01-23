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
class Event {

    private String description;
    private Long id;
    private String imageId;
    private String organizer;
    private Long organizerGroup;

    private String[] tags;

    private long time;
    private String title;

    public Event() {
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public String getOrganizer() {
        return organizer;
    }

    public Long getOrganizerGroup() {
        return organizerGroup;
    }

    public String[] getTags() {
        return tags;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setOrganizerGroup(Long organizerGroup) {
        this.organizerGroup = organizerGroup;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
