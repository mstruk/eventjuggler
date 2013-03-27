package org.eventjuggler.services.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.picketbox.jaxrs.model.AuthenticationResponse;

@Provider
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException arg0) {
        AuthenticationResponse authcResponse = new AuthenticationResponse();
        authcResponse.setLoggedIn(false);

        return Response.status(Status.FORBIDDEN).entity(authcResponse).build();
    }

}
