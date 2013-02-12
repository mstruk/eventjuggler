package org.eventjuggler.services.security;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eventjuggler.services.UserService;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketlink.idm.model.User;

@ApplicationScoped
public class EventHandler {

    private final Logger log = Logger.getLogger(EventHandler.class);

    @Inject
    private UserService userService;

    public void onUserAuthenticatedEvent(@Observes UserAuthenticatedEvent event) {
        User user = event.getUserContext().getUser();

        if (userService.getUser(user.getLoginName()) == null) {
            org.eventjuggler.model.User u = new org.eventjuggler.model.User();

            u.setLogin(user.getLoginName());
            u.setName(user.getFirstName());
            u.setLastName(user.getLastName());
            u.setPassword("NOT USED");

            userService.create(u);

            log.info("Created user '" + u.getLogin() + "'");
        }
    }

}
