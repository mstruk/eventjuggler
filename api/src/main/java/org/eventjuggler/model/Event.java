package org.eventjuggler.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Entity
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Group organizerGroup;

    @ManyToOne(fetch=FetchType.EAGER)
    private User organizer;

    @ManyToOne(fetch=FetchType.EAGER)
    private Address location;

    private long time;

    @ManyToMany(fetch=FetchType.EAGER)
    private List<RSVP> attendance;

    private String title;

    @Column(length = 20000)
    private String description;

    private String imageId;

    @Column(length = 2000)
    private String tags;

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Group getOrganizerGroup() {
        return organizerGroup;
    }

    public void setOrganizerGroup(Group organizerGroup) {
        this.organizerGroup = organizerGroup;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<RSVP> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<RSVP> attendance) {
        this.attendance = attendance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
