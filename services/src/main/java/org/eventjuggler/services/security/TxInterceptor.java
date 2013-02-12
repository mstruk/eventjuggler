/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.services.security;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.picketlink.extensions.core.rest.AccountRegistrationEndpoint;

/**
 * If picketlink extensions are included in the ear and not the war there's an issue where a tx is not started for the
 * picketlink extension rest endpoints. Looking at AccountRegistrationEndpoint it should have a tx started as it's a stateless
 * bean and there's no transaction attribute (tx required). Until this issue is resolved this is a work-around
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@RequestScoped
@ServerInterceptor
public class TxInterceptor implements PreProcessInterceptor, PostProcessInterceptor {

    @Inject
    private UserTransaction tx;

    private boolean txStarted;

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        if (requiresTx(method)) {
            try {
                tx.begin();
                txStarted = true;
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }
        return null;
    }

    @Override
    public void postProcess(ServerResponse response) {
        if (txStarted) {
            try {
                tx.commit();
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }
    }

    private boolean requiresTx(ResourceMethod method) {
        return method.getMethod().getDeclaringClass().getPackage().equals(AccountRegistrationEndpoint.class.getPackage());
    }

}
