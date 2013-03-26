package org.eventjuggler.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Entity
@XmlRootElement
public class Event {

    @Id @GeneratedValue
    private Long id;

    private String organizer;

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

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
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

    public String[] getTags() {
        return convertTags(tags);
    }

    public void setTags(String[] tags) {
        this.tags = convertTags(tags);
    }

    public static String[] convertTags(String tags) {
        if (tags == null) {
            return null;
        }

        String[] t = tags.split(",");
        for (int i = 0; i < t.length; i++) {
            t[i] = t[i].trim();
        }
        return t;
    }

    public static String convertTags(String[] tags) {
        if (tags == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(tags[i]);
        }
        return sb.toString();
    }
}
