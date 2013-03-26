package com.eventjuggler.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.eventjuggler.model.Group;
import org.eventjuggler.model.Role;
import org.eventjuggler.model.User;
import org.eventjuggler.model.UserRole;
import org.eventjuggler.services.DataService;
import org.eventjuggler.services.UserService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eventjuggler.test.BeanUtils;
import com.eventjuggler.test.Deployments;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@RunWith(Arquillian.class)
public class UserServiceIT {

    @Deployment(name = "eventjuggler-ear", order = 1, testable = false)
    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        return Deployments.getEventJugglerServer();
    }

    @Deployment(name = "eventjuggler-test", order = 2, testable = true)
    public static WebArchive getTestArchive() throws IllegalArgumentException, Exception {
        return Deployments.getTestArchive(UserServiceIT.class);
    }

    @EJB
    private UserService service;

    private DataService dataService;

    private Long userRoleId;

    @Before
    public void before() throws Exception {
        dataService = BeanUtils.lookupBean(DataService.class);
        dataService.clear();

        service = BeanUtils.lookupBean(UserService.class);
    }

    @After
    public void after() throws Exception {
        dataService.clear();
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
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
    @OperateOnDeployment("eventjuggler-test")
    public void createUser() {
        User user = new User();
        user.setLogin("login");
        service.create(user);
    }

    @Test
    @OperateOnDeployment("eventjuggler-test")
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
    @OperateOnDeployment("eventjuggler-test")
    public void getGroups() {
        createGroup();

        User user = service.getUser("login");
        assertNotNull(user);

        List<Group> groups = service.getGroups(user);

        assertEquals(1, groups.size());
    }
}
