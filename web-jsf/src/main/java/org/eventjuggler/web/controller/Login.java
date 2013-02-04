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
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@SessionScoped
@Named
public class Login implements Serializable {

    @Inject
    private Credentials credentials;

    @EJB
    private AuthenticationService authenticationService;

    private User user;

    private String redirectedUri;

    public String login() {

        boolean success = authenticationService.login(credentials.getUsername(), credentials.getPassword());
        if (success) {
            User user = new User();
            user.setLogin(credentials.getUsername());
            this.user = user;
            return redirectedUri;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Authentication failed!"));
        }
        return null;
    }

    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().invalidateSession();

        return "/home.xhtml?faces-redirect=true";
    }

    public boolean isAuthRequired(HttpServletRequest request) {
        String uri = getUri(request);
        return !uri.startsWith("/login") &&
            !uri.startsWith("/register") &&
            !uri.startsWith("/home") &&
            !uri.startsWith("/javax.faces.resource/");
    }

    @Produces
    @Named("currentUser")
    public User getCurrentUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void setRedirectedUri(HttpServletRequest req) {
        redirectedUri = req.getRequestURI();
    }

    private String getUri(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }
}