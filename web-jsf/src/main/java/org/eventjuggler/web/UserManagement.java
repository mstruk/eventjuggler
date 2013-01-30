package org.eventjuggler.web;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ApplicationScoped
public class UserManagement implements Serializable {

    private Map<String, String> usersMap = new ConcurrentHashMap<String, String>();

    public UserManagement() {
        usersMap.put("test", "tester");
    }

    public boolean login(String username, String password) {
        String pass = usersMap.get(username);
        return password.equals(pass);
    }
}
