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

package org.eventjuggler.services.security.authc.social.fb;

import org.picketbox.core.UserCredential;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleGroup;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;
import org.picketlink.social.standalone.fb.FacebookPrincipal;
import org.picketlink.social.standalone.fb.FacebookProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An authentication mechanism for Facebook SignIn
 *
 * @author Anil Saldhana
 * @author Pedro Silva
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class FacebookAuthenticationMechanism extends AbstractAuthenticationMechanism {

    private static final String FB_AUTH_STATE_SESSION_ATTRIBUTE = "FB_AUTH_STATE_SESSION_ATTRIBUTE";

    protected FacebookProcessor processor;

    private enum STATES {
        AUTH, AUTHZ, FINISH
    }

    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        ArrayList<AuthenticationInfo> info = new ArrayList<AuthenticationInfo>();

        info.add(new AuthenticationInfo("oAuth Authentication", "Provides oAuth authentication.", FacebookCredential.class));

        return info;
    }

    @Override
    protected Principal doAuthenticate(UserCredential credential, AuthenticationResult result) throws AuthenticationException {
        FacebookCredential oAuthCredential = (FacebookCredential) credential;

        HttpServletRequest request = oAuthCredential.getRequest();
        HttpServletResponse response = oAuthCredential.getResponse();
        HttpSession session = request.getSession();

        FacebookProcessor fb = getFacebookProcessor(oAuthCredential);
        Principal principal = fb.getPrincipal(request, response);
        if (principal == null || isFirstInteraction(session)) {
            try {
                fb.initialInteraction(request, response);
            } catch (IOException e) {
                throw new AuthenticationException("Error while initiating Facebook authentication interaction.", e);
            }
        } else if (isAuthenticationInteraction(session)) {
            if (!response.isCommitted()) {
                fb.handleAuthStage(request, response);
            }
        } else if (isAuthorizationInteraction(session)) {
            session.removeAttribute(FB_AUTH_STATE_SESSION_ATTRIBUTE);
            provisionNewUser((FacebookPrincipal) principal);
        }

        return principal;
    }

    private boolean isAuthorizationInteraction(HttpSession session) {
        return getCurrentAuthenticationState(session).equals(STATES.AUTHZ.name());
    }

    private boolean isAuthenticationInteraction(HttpSession session) {
        return getCurrentAuthenticationState(session).equals(STATES.AUTH.name());
    }

    private boolean isFirstInteraction(HttpSession session) {
        return getCurrentAuthenticationState(session) == null || getCurrentAuthenticationState(session).isEmpty();
    }

    private String getCurrentAuthenticationState(HttpSession session) {
        return (String) session.getAttribute(FB_AUTH_STATE_SESSION_ATTRIBUTE);
    }

    @SuppressWarnings("unchecked")
    private FacebookProcessor getFacebookProcessor(FacebookCredential oAuthCredential) {
        if (this.processor == null) {
            String ctxPath = oAuthCredential.getRequest().getContextPath();
            String clientID = getRequiredProperty(ctxPath, "FB_CLIENT_ID");
            String clientSecret = getRequiredProperty(ctxPath, "FB_CLIENT_SECRET");
            String returnURL = getRequiredProperty(ctxPath, "FB_RETURN_URL");
            String scope = "email";
            this.processor = new FacebookProcessor(clientID, clientSecret, scope, returnURL, Collections.EMPTY_LIST);
        }
        return this.processor;
    }

    private static String getRequiredProperty(String suffix, String key) {
        String longKey = trimSlashes(suffix) + "." + key;
        String val = System.getProperty(longKey);
        if (val == null)
            val = System.getProperty(key);
        if (val == null)
            throw new IllegalStateException("A required system property is not defined: [" + longKey + ", " + key + "]");
        return val;
    }

    private static String trimSlashes(String val) {
        if (val.startsWith("/"))
            val = val.substring(1);
        if (val.endsWith("/"))
            val = val.substring(0, val.length()-1);
        return val;
    }

    /**
     * <p>
     * Provision the authenticated user if he is not stored yes.
     * </p>
     * <p/>
     * TODO: user provisioning feature should be provided by PicketBox ?
     */
    private void provisionNewUser(FacebookPrincipal principal) {
        // Check if the user exists in DB
        IdentityManager identityManager = getIdentityManager();

        User storedUser = identityManager.getUser(principal.getEmail());

        if (storedUser == null) {
            storedUser = new SimpleUser(principal.getEmail());

            storedUser.setFirstName(principal.getFirstName());
            storedUser.setLastName(principal.getLastName());

            identityManager.add(storedUser);

            // necessary because we need to show the user info at the main page. Otherwise the informations will be show only
            // after the second login.
            Role guest = new SimpleRole("guest");

            identityManager.add(guest);

            Group guests = new SimpleGroup("Guests");

            identityManager.add(guests);

            identityManager.grantRole(storedUser, guest);
            identityManager.addToGroup(storedUser, guests);
        }
    }

}
