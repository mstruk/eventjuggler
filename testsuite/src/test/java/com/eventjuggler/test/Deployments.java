package com.eventjuggler.test;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenResolverSystemBaseImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;

public class Deployments {

    /**
     * This is a hack to lookup the full canonical name for a dependency, as there seems to be a bug in shrinkwrap resolver
     */
    private static String expandCanonical(String canonical) throws Exception {
        @SuppressWarnings("rawtypes")
        MavenResolverSystemBaseImpl resolver = (MavenResolverSystemBaseImpl) Maven.resolver();
        resolver.loadPomFromFile("../pom.xml");

        Method method = MavenResolverSystemBaseImpl.class.getDeclaredMethod("getSession", new Class<?>[0]);
        method.setAccessible(true);

        MavenWorkingSessionImpl mavenSession = (MavenWorkingSessionImpl) method.invoke(resolver);

        for (Iterator<MavenDependency> itr = mavenSession.getDependencyManagement().iterator(); itr.hasNext();) {
            MavenDependency dep = itr.next();
            if (dep.toCanonicalForm().startsWith(canonical)) {
                return dep.toCanonicalForm();
            }
        }

        return null;
    }

    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        EnterpriseArchive archive = Maven.resolver().offline().loadPomFromFile("../pom.xml")
                .resolve(expandCanonical("org.eventjuggler:eventjuggler-ear:ear")).withoutTransitivity()
                .as(EnterpriseArchive.class)[0];
        return archive;
    }

}
