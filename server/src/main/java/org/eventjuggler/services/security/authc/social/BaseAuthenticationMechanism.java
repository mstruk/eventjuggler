package org.eventjuggler.services.security.authc.social;

import org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public abstract class BaseAuthenticationMechanism extends AbstractAuthenticationMechanism {

    protected static String getRequiredProperty(String suffix, String key) {
        String longKey = trimSlashes(suffix) + "." + key;
        String val = System.getProperty(longKey);
        if (val == null)
            val = System.getProperty(key);
        if (val == null)
            throw new IllegalStateException("A required system property is not defined: '" + longKey + "' or '" + key + "'");
        return val;
    }

    protected static String trimSlashes(String val) {
        if (val.startsWith("/"))
            val = val.substring(1);
        if (val.endsWith("/"))
            val = val.substring(0, val.length()-1);
        return val;
    }
}
