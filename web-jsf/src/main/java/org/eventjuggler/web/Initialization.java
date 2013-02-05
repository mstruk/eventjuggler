package org.eventjuggler.web;

import org.eventjuggler.model.User;
import org.eventjuggler.services.PasswordHashService;
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

    @Inject
    private PasswordHashService passwordHashService;

    @PostConstruct
    public void initialize() {

        passwordHashService.init();

        User user = userService.getUser("test");
        if (user == null) {
            user = new User();
            user.setLogin("test");
            user.setPassword(hash("tester"));
            user.setDescription("Test user");
            user.setName("John");
            user.setLastName("Doe");
            userService.create(user);
        }
    }

    private String hash(String password) {
        return passwordHashService.hash(password);
    }
}
