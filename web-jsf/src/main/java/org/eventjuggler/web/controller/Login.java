package org.eventjuggler.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.eventjuggler.model.User;
import org.eventjuggler.web.model.Credentials;
import org.eventjuggler.web.model.Session;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketlink.extensions.core.pbox.LoginCredential;
import org.picketlink.extensions.core.pbox.PicketBoxIdentity;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ApplicationScoped
@Named
public class Login implements Serializable {

    private static Logger log = Logger.getLogger(Login.class.getName());

    @Inject
    private Credentials credentials;

    @Inject
    private Session session;

    @Inject
    private PicketBoxIdentity identity;

    @Inject
    private LoginCredential credential;

    public void login() {
        this.credential.setCredential(new UsernamePasswordCredential(credentials.getUsername(), credentials.getPassword()));

        this.identity.login();

        if (this.identity.isLoggedIn()) {
            User user = new User();
            user.setLogin(credentials.getUsername());
            session.setUser(user);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(session.getRedirectedUri());
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
        boolean authRequired = !uri.equals("/") && !uri.startsWith("/login") && !uri.startsWith("/index")
                && !uri.startsWith("/register") && !uri.startsWith("/home") && !uri.startsWith("/javax.faces.resource/");
        return authRequired;
    }

    public boolean isLoggedIn() {
        return session.getCurrentUser() != null;
    }

    public void setRedirectedUri(HttpServletRequest req) {
        session.setRedirectedUri(req.getRequestURI());
    }

    private String getUri(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }
}