package org.eventjuggler.services;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public interface AuthenticationService {

    boolean login(String username, String password);

}
