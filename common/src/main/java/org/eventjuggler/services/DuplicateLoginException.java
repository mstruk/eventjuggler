package org.eventjuggler.services;

import javax.ejb.ApplicationException;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ApplicationException
public class DuplicateLoginException extends RuntimeException {

    public DuplicateLoginException() {}

    public DuplicateLoginException(String message) {
        super(message);
    }
}
