package org.eventjuggler.web.controller;

import org.eventjuggler.model.User;
import org.eventjuggler.services.AuthenticationService;
import org.eventjuggler.web.model.Credentials;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static Logger log = Logger.getLogger(Login.class.getName());

    @Inject
    private Credentials credentials;

    @EJB
    private AuthenticationService authenticationService;

    private User user;

    private String redirectedUri;

    public void login() {
        boolean success = authenticationService.login(credentials.getUsername(), credentials.getPassword());
        if (success) {
            User user = new User();
            user.setLogin(credentials.getUsername());
            this.user = user;
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(redirectedUri);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed to redirect back from login", e);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Authentication failed!"));
        }
    }

    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().invalidateSession();

        return "/home.xhtml?faces-redirect=true";
    }

    public boolean isAuthRequired(HttpServletRequest request) {
        String uri = getUri(request);
        boolean authRequired = !uri.equals("/") &&
            !uri.startsWith("/login") &&
            !uri.startsWith("/index") &&
            !uri.startsWith("/register") &&
            !uri.startsWith("/home") &&
            !uri.startsWith("/javax.faces.resource/");
        return authRequired;
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