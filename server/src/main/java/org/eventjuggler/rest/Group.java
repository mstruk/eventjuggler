package org.eventjuggler.rest;

import org.eventjuggler.model.Address;

public class Group {

    private Long id;

    private String name;

    private String description;

    private String imageId;

    private Address location;

    public Group() {
    }

    public Group(org.eventjuggler.model.Group g) {
        this.id = g.getId();
        this.name = g.getName();
        this.description = g.getDescription();
        this.imageId = g.getImageId();
        this.location = g.getLocation();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageId() {
        return imageId;
    }

    public Address getLocation() {
        return location;
    }

}
