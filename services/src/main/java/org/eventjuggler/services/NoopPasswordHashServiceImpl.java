package org.eventjuggler.services;

import javax.enterprise.inject.Alternative;

@Alternative
public class NoopPasswordHashServiceImpl implements PasswordHashService {

    public void init() {}

    public String hash(String cleartext) {
        return cleartext;
    }
}