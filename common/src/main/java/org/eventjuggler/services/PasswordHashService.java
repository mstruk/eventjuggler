package org.eventjuggler.services;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public interface PasswordHashService {

    void init();

    String hash(String cleartext);
}