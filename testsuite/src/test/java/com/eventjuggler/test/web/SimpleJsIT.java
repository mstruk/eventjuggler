package com.eventjuggler.test.web;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eventjuggler.test.Deployments;
import com.eventjuggler.test.HttpUtils;

@RunWith(Arquillian.class)
public class SimpleJsIT {

    @Deployment(name = "eventjuggler-ear", testable = false)
    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        return Deployments.getEventJugglerServer();
    }

    @Test
    @RunAsClient
    public void index() throws Exception {
        String response = HttpUtils.get("/eventjuggler-web-js");
        Assert.assertTrue(response.contains("<title>EventJuggler</title>"));
    }

}
