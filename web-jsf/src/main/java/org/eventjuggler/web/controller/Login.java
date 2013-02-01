package org.eventjuggler.web.controller;

import org.eventjuggler.model.User;
import org.eventjuggler.services.AuthenticationService;
import org.eventjuggler.web.model.Credentials;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@SessionScoped
@Named("login")
public class Login implements Serializable {

    @Inject
    private Credentials credentials;

    @EJB
    private AuthenticationService authenticationService;

    private User user;

    public void login() {

        boolean success = authenticationService.login(credentials.getUsername(), credentials.getPassword());
        if (success) {
            User user = new User();
            user.setLogin(credentials.getUsername());
            this.user = user;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Authentication failed!"));
        }

    }

    @Produces
    @Named("currentUser")
    public User getCurrentUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }
}