package com.eventjuggler.test.rest;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;
import org.eventjuggler.model.Event;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eventjuggler.test.Deployments;
import com.eventjuggler.test.HttpUtils;

@RunWith(Arquillian.class)
public class EventResourceIT {

    @Deployment(name = "eventjuggler-server", order = 1)
    public static EnterpriseArchive getEventJugglerServer() throws Exception {
        return Deployments.getEventJugglerServer();
    }

    @Test
    @RunAsClient
    public void events() throws Exception {
        List<Event> list = HttpUtils.get("/eventjuggler-rest/events", new TypeReference<List<Event>>() {
        });

        Assert.assertEquals(0, list.size());
    }

}
