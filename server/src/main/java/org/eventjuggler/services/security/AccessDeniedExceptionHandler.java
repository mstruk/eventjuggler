package org.eventjuggler.services.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.core.ServerResponse;
import org.picketbox.jaxrs.model.AuthenticationResponse;

@Provider
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException arg0) {
        ServerResponse response = new ServerResponse();

        AuthenticationResponse authcResponse = new AuthenticationResponse();

        authcResponse.setLoggedIn(false);

        response = new ServerResponse();
        response.setEntity(authcResponse);
        response.setStatus(HttpStatus.SC_FORBIDDEN);

        return response;
    }

}
