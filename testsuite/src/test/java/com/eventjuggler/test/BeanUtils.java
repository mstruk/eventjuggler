package com.eventjuggler.test;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

public class BeanUtils {

    public static <T> T lookupBean(Class<T> clazz) throws Exception {
        String appName = "eventjuggler-ear";

        NamingEnumeration<NameClassPair> list = new InitialContext().list("java:global");
        while (list.hasMore()) {
            String name = list.next().getName();
            if (name.startsWith(appName)) {
                appName = name;
            }
        }

        String lookup = "java:global/" + appName + "/eventjuggler-services/" + clazz.getSimpleName() + "Bean!"
                + clazz.getName();

        return clazz.cast(new InitialContext().lookup(lookup));
    }

}
