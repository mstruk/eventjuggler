package org.eventjuggler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.eventjuggler.model.Group;
import org.eventjuggler.model.Role;
import org.eventjuggler.model.User;
import org.eventjuggler.model.UserRole;
import org.eventjuggler.services.UserService;
import org.eventjuggler.services.UserServiceBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@RunWith(Arquillian.class)
public class UserServiceBeanTest {

    @Deployment
    @OverProtocol("Servlet 3.0")
    public static WebArchive createTestArchive() {
        return JPADeploymentTest.createTestArchive().addClasses(UserService.class, UserServiceBean.class, DatabaseTools.class);
    }

    @EJB
    private DatabaseTools databaseTools;

    @EJB
    private UserService service;

    private Long userRoleId;

    @After
    public void after() throws Exception {
        databaseTools.cleanDatabase();
    }

    @Test
    public void createGroup() {
        createUserRole();

        UserRole userRole = service.getUserRole(userRoleId);

        Group group = new Group();
        group.setName("group");

        List<UserRole> members = new LinkedList<UserRole>();
        members.add(userRole);

        group.setMembers(members);

        service.create(group);

        assertNotNull(group.getId());
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setLogin("login");
        service.create(user);
    }

    @Test
    public void createUserRole() {
        createUser();
        User user = service.getUser("login");

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ADMIN);
        userRole.setUser(user);
        service.create(userRole);

        assertNotNull(userRole.getId());

        userRoleId = userRole.getId();
    }

    @Test
    public void getGroups() {
        createGroup();

        User user = service.getUser("login");
        assertNotNull(user);

        List<Group> groups = service.getGroups(user);

        assertEquals(1, groups.size());
    }
}
