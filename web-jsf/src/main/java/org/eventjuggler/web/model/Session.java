package org.eventjuggler.web.model;

import org.eventjuggler.model.User;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@SessionScoped
public class Session implements Serializable {

    private User user;

    private String redirectedUri;

    @Produces
    @Named("currentUser")
    public User getCurrentUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public String getRedirectedUri() {
        return redirectedUri;
    }

    public void setRedirectedUri(String redirectedUri) {
        this.redirectedUri = redirectedUri;
    }
}
