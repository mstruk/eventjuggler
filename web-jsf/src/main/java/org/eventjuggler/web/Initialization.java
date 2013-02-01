package org.eventjuggler.web;

import org.eventjuggler.model.User;
import org.eventjuggler.services.UserService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Startup
@Singleton
public class Initialization {

    @Inject
    private UserService userService;

    @PostConstruct
    public void initialize() {

        User user = userService.getUser("test");
        if (user == null) {
            user = new User();
            user.setLogin("test");
            user.setPassword("tester");
            user.setDescription("Test user");
            user.setName("John");
            user.setLastName("Doe");
            userService.create(user);
        }
    }
}
