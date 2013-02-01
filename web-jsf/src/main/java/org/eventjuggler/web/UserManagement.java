package org.eventjuggler.web;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@Stateless
public class UserManagement implements Serializable {

    @PersistenceContext
    private EntityManager em;

    public boolean login(String username, String password) {
        List rs = em.createQuery("select u FROM User u WHERE u.login=?1 AND u.password=?2")
            .setParameter(1, username)
            .setParameter(2, password)
            .setMaxResults(1)
            .getResultList();

        return rs.size() == 1;
    }
}
