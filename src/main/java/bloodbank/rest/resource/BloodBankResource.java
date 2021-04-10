/**
 * File: BloodBankResource.java Course materials (21W) CST 8277
 *
 * @author Emrah Kinay
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
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
import bloodbank.entity.BloodBank;

@Path(BLOODBANK_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodBankResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
	public Response getBloodBanks() {
		LOG.debug( "retrieving all blood banks ...");
		List< BloodBank> bloodBanks = service.getAllBloodBanks();
		Response response = Response.ok( bloodBanks).build();
		return response;
	}

	@GET
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific blood bank " + id);
		Response response = null;
		BloodBank bloodBank = null;
		if ( sc.isCallerInRole( ADMIN_ROLE)) {
			bloodBank = service.getBloodBankById(id);
			response = Response.status( bloodBank == null ? Status.NOT_FOUND : Status.OK).entity( bloodBank).build();
		} else {
			response = Response.status( Status.BAD_REQUEST).build();
			LOG.debug( "Admin role required!");
		}
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addBloodBank( BloodBank newBloodBank) {
		Response response = null;
		BloodBank addedBloodbank = service.persistBloodBank( newBloodBank);
		response = Response.ok( addedBloodbank).build();
		return response;
	}	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		BloodBank deletedBloodBank = service.deleteBloodBankById(id);
		response = Response.ok( deletedBloodBank).build();
		return response;
	}
	
	@PUT
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response updateBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id,  BloodBank newBloodBank) {
		Response response = null;
		BloodBank deletedBloodBank = service.updateBloodBankById(id,newBloodBank);
		response = Response.ok( deletedBloodBank).build();
		return response;
	}
	
}