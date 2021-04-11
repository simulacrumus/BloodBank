/**
 * File: BloodDonationResource.java Course materials (21W) CST 8277
 *
 * @author Emrah Kinay
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
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
import bloodbank.entity.BloodDonation;

@Path(BLOOD_DONATION_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class BloodDonationResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
	public Response getBloodDonations() {
		LOG.debug( "retrieving all blood donations ...");
		List< BloodDonation> bloodDonations = service.getAllBloodDonations();
		Response response = Response.ok( bloodDonations).build();
		return response;
	}

	@GET
	@RolesAllowed( { USER_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getBloodDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific blood donation " + id);
		Response response = null;
		BloodDonation bloodDonation = null;
		if ( sc.isCallerInRole( ADMIN_ROLE)) {
			bloodDonation = service.getBloodDonationById(id);
			response = Response.status( bloodDonation == null ? Status.NOT_FOUND : Status.OK).entity( bloodDonation).build();
		} else {
			response = Response.status( Status.BAD_REQUEST).build();
			LOG.debug( "Admin role required!");
		}
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addBloodDonation( BloodDonation newBloodDonation) {
		Response response = null;
		BloodDonation addedBloodDonation = service.persistBloodDonation( newBloodDonation);
		response = Response.ok( addedBloodDonation).build();
		return response;
	}	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		Response response = null;
		BloodDonation deletedBloodDonation = service.deleteBloodDonationById(id);
		response = Response.ok( deletedBloodDonation).build();
		return response;
	}
	
	@PUT
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response updateBloodDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id,  BloodDonation newBloodDonation) {
		Response response = null;
		BloodDonation deletedBloodDonation = service.updateBloodDonationById(id,newBloodDonation);
		response = Response.ok( deletedBloodDonation).build();
		return response;
	}
	
}