/**
 * File: PhoneResource.java Course materials (21W) CST 8277
 *
 * @author Emrah Kinay
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.PHONE_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;

import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Phone;

@Path(PHONE_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class PhoneResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
	public Response getPhones() {
		LOG.debug( "retrieving all phones ...");
		List<Phone > phones = service.getAllPhones();
		Response response = Response.ok( phones).build();
		return response;
	}

	@GET
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getPhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific phone " + id);
		Response response = null;
		Phone phone = null;
		if ( sc.isCallerInRole( ADMIN_ROLE)) {
			phone = service.getPhoneById(id);
			response = Response.status( phone == null ? Status.NOT_FOUND : Status.OK).entity( phone).build();
		} else {
			response = Response.status( Status.BAD_REQUEST).build();
			LOG.debug( "Admin role required!");
		}
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addPhone( Phone newPhone) {
		Response response = null;
		Phone addedPhone = service.persistPhone( newPhone);
		response = Response.ok( addedPhone).build();
		return response;
	}	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deletePhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		Phone deletedPhone = service.deletePhoneById(id);
		response = Response.ok( deletedPhone).build();
		return response;
	}
	
	@PUT
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response updatePhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id,  Phone newPhone) {
		Response response = null;
		Phone deletedPhone = service.updatePhoneById(id, newPhone);
		response = Response.ok( deletedPhone).build();
		return response;
	}
	
}