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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.picketlink.authentication.AuthenticationException;
import org.picketlink.extensions.core.pbox.PicketBoxIdentity;

/**
 * There's an implementation of this provided in picketlink-extensions (
 * {@link org.picketlink.extensions.core.rest.interceptors.SecurityInterceptor}), but the requiresAuthentication method is not
 * valid as it causes all rest resources (except the picketlink-extensions) to require authentication. This is a copy of that
 * class with a fixed implementation of the requiresAuthentication method. Until this issue is resolved this is a work-around
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@ApplicationScoped
@ServerInterceptor
public class SecurityInterceptor implements PreProcessInterceptor {

    private static final String AUTH_TOKEN_HEADER_NAME = "Auth-Token";

    @Inject
    private PicketBoxIdentity identity;

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        if (!this.identity.isLoggedIn()) {
            String token = getToken(request);
            if (token != null) {
                try {
                    identity.restoreSession(token);
                } catch (AuthenticationException e) {
                }
            }
        }

        return null;
    }

    private String getToken(HttpRequest request) {
        List<String> tokenHeader = request.getHttpHeaders().getRequestHeader(AUTH_TOKEN_HEADER_NAME);
        String token = null;

        if (tokenHeader != null && !tokenHeader.isEmpty()) {
            token = tokenHeader.get(0);
        }

        return token;
    }

}
