package org.eventjuggler.tests;

import org.eventjuggler.model.User;
import org.eventjuggler.services.AuthenticationService;
import org.eventjuggler.services.AuthenticationServiceBean;
import org.eventjuggler.services.DuplicateLoginException;
import org.eventjuggler.services.NoopPasswordHashServiceImpl;
import org.eventjuggler.services.PasswordHashService;
import org.eventjuggler.services.SaltedSHA512PasswordHashServiceImpl;
import org.eventjuggler.services.UserService;
import org.eventjuggler.services.UserServiceBean;
import org.eventjuggler.util.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.util.Base64;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@RunWith(Arquillian.class)
public class AuthenticationServiceBeanTest {

    @Deployment
    @OverProtocol("Servlet 3.0")
    public static WebArchive createTestArchive() {
        return JPADeploymentTest.createTestArchive().addClasses(UserService.class, UserServiceBean.class, DuplicateLoginException.class,
            AuthenticationService.class, AuthenticationServiceBean.class, DatabaseTools.class,
            PasswordHashService.class, NoopPasswordHashServiceImpl.class, SaltedSHA512PasswordHashServiceImpl.class,
            IOUtils.class, Base64.class);
    }

    @EJB
    private AuthenticationService authService;

    @EJB
    private UserService userService;

    @EJB
    private DatabaseTools databaseTools;

    @Inject
    private PasswordHashService passwordHashService;

    @Before
    public void init() throws Exception {
        User user = new User();
        user.setLogin("user1");
        user.setPassword(hash("password"));
        userService.create(user);
    }

    private String hash(String password) {
        return passwordHashService.hash(password);
    }

    @After
    public void after() throws Exception {
        databaseTools.cleanDatabase();
    }

    @Test
    public void authenticate() {
        boolean success = authService.login("user1", "");
        Assert.assertFalse("Login with incorrect credentials should return false", success);

        success = authService.login("user1", hash("password"));
        Assert.assertTrue("Login with correct credentials should return true", success);
    }
}
