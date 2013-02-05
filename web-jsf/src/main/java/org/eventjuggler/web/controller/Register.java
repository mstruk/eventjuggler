package org.eventjuggler.web.controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.eventjuggler.model.User;
import org.eventjuggler.services.DuplicateLoginException;
import org.eventjuggler.services.PasswordHashService;
import org.eventjuggler.services.UserService;
import org.eventjuggler.web.model.Credentials;
import org.eventjuggler.web.model.Registration;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ApplicationScoped
@Named
public class Register implements Serializable {

    @EJB
    private UserService userService;

    @Inject
    private Registration registration;

    @Inject
    private Credentials credentials;

    @Inject
    private PasswordHashService hashService;

    public String register() {
        User u = registration.toUser();

        if (credentials.isUsernameEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username has to be set!"));
        }

        if (credentials.isPasswordEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password has to be set!"));
        }

        if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
            return null;
        }

        u.setLogin(credentials.getUsername());
        u.setPassword(hashService.hash(credentials.getPassword()));

        try {
            userService.create(u);
            return "/login.xhtml";
        } catch (DuplicateLoginException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("The username is already taken. Try choose another."));
        }
        return null;
    }
}
