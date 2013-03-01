package com.eventjuggler.test;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
                .resolve(expandCanonical("org.eventjuggler:eventjuggler-server:ear")).withoutTransitivity()
                .as(EnterpriseArchive.class)[0];
        return archive;
    }

    public static WebArchive getTestArchive(Class<?> test) throws IllegalArgumentException, Exception {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(test, BeanUtils.class)
                .addAsManifestResource(
                        new StringAsset("Dependencies: deployment." + Deployments.getEventJugglerServer().getName() + " \n"),
                        "MANIFEST.MF").addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
}
