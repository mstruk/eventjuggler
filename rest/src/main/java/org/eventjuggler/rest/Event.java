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
    private Long organizerGroupId;
    private Long organizerId;

    private String[] tags;

    private long time;
    private String title;

    public Event() {
    }

    public Event(org.eventjuggler.model.Event e) {
        this.description = e.getDescription();
        this.id = e.getId();
        this.imageId = e.getImageId();
        this.organizerId = e.getOrganizer() != null ? e.getOrganizer().getId() : null;
        this.organizerGroupId = e.getOrganizerGroup() != null ? e.getOrganizerGroup().getId() : null;
        this.tags = ObjectFactory.createTags(e.getTags());
        this.time = e.getTime();
        this.title = e.getTitle();
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

    public Long getOrganizerGroupId() {
        return organizerGroupId;
    }

    public Long getOrganizerId() {
        return organizerId;
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

    public org.eventjuggler.model.Event toInternal() {
        org.eventjuggler.model.Event e = new org.eventjuggler.model.Event();
        e.setDescription(description);
        e.setImageId(imageId);
        e.setTags(ObjectFactory.createTags(tags));
        e.setTime(time);
        e.setTitle(title);
        return e;
    }

}
