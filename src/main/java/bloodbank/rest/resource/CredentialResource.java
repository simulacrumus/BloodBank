/**
 * File: CredentialResource.java
 * Course materials (21W) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * Note: students do NOT need to change anything in this class
 *
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.CREDENTIAL_RESOURCE_NAME;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.security.Principal;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.soteria.WrappingCallerPrincipal;

import bloodbank.entity.SecurityUser;

@Path(CREDENTIAL_RESOURCE_NAME)
@Produces(MediaType.APPLICATION_JSON)
public class CredentialResource {

    @Inject
    protected ServletContext servletContext;

    @Inject
    protected SecurityContext securityContent;

    @GET
    public Response getCredentials() {
        servletContext.log("testing credentials ...");
        Response response = null;
        Principal callerPrincipal = securityContent.getCallerPrincipal();
        if (callerPrincipal == null) {
            response = Response.status(UNAUTHORIZED).build();
        }
        else {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal)callerPrincipal;
            SecurityUser sUser = (SecurityUser)wCallerPrincipal.getWrapped();
            response = Response.ok(sUser).build();
        }
        return response;
    }

}