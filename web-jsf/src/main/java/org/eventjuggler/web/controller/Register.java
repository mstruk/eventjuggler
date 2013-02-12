package org.eventjuggler.web.controller;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.eventjuggler.web.model.Credentials;
import org.eventjuggler.web.model.Registration;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.internal.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class Register implements Serializable {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private Registration registration;

    @Inject
    private Credentials credentials;

    public String register() {
        if (credentials.isUsernameEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username has to be set!"));
        }

        if (credentials.isPasswordEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password has to be set!"));
        }

        if (FacesContext.getCurrentInstance().getMessageList().size() > 0) {
            return null;
        }

        if (identityManager.getUser(credentials.getUsername()) == null) {
            User user = new SimpleUser(credentials.getUsername());

            user.setFirstName(registration.getName());
            user.setLastName(registration.getLastName());
            user.setAttribute(new Attribute<String>("city", registration.getCountry()));

            identityManager.add(user);
            identityManager.updateCredential(user, new Password(credentials.getPassword()));

            return "/login.xhtml";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("The username is already taken. Try choose another."));
        }
        return null;
    }
}
