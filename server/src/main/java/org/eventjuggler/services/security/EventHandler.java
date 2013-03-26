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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eventjuggler.services.UserService;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketlink.idm.model.User;

import java.security.Principal;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ApplicationScoped
public class EventHandler {

    private final Logger log = Logger.getLogger(EventHandler.class);

    @Inject
    private UserService userService;

    public void onUserAuthenticatedEvent(@Observes UserAuthenticatedEvent event) {

        UserContext subject = event.getUserContext();
        AuthenticationResult authenticationResult = subject.getAuthenticationResult();
        AuthenticationStatus status = authenticationResult.getStatus();

        if (status.equals(AuthenticationStatus.SUCCESS)) {
            Principal principal = authenticationResult.getPrincipal();
            User user = subject.getUser();

            if (userService.getUser(user.getLoginName()) == null) {
                org.eventjuggler.model.User u = new org.eventjuggler.model.User();

                u.setLogin(user.getLoginName());
                u.setName(user.getFirstName());
                u.setLastName(user.getLastName());

                userService.create(u);

                log.info("Created user '" + u.getLogin() + "'");
            }
        }
    }

}
