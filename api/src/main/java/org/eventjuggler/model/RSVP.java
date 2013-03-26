package org.eventjuggler.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Entity
public class RSVP {

    @Id
    @GeneratedValue
    private Long id;

    private String user;

    @Enumerated(EnumType.STRING)
    private Response response;

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static enum Response {
        WILL_ATTEND, MAYBE, WONT_ATTEND
    }
}
