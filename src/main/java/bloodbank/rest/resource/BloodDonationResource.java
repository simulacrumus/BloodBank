/**
 * File: BloodDonationResource.java Course materials (21W) CST 8277
 *
 * @author Darryl Galaraga 
 * @author Elyse Ntigirishari
 * @author Emrah Kinay
 * @author Jephte Francois
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
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;

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
	@RolesAllowed( { ADMIN_ROLE, USER_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getBloodDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific blood donation " + id);
		BloodDonation bloodDonation =service.getBloodDonationById(id);
		if(bloodDonation == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Donation found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.status( bloodDonation == null ? Status.NOT_FOUND : Status.OK).entity( bloodDonation).build();
		return response;
	}
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodDonationById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		BloodDonation bloodDonation = service.getBloodDonationById(id);
		if(bloodDonation == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Donation found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		BloodDonation deletedBloodDonation = service.deleteBloodDonationById(id);
		Response response = Response.ok( deletedBloodDonation).build();
		return response;
	}
	
	@POST
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH + "/{personId}" )
	public Response addDonationRecord(@PathParam( RESOURCE_PATH_ID_ELEMENT) int bloodDonationId, @PathParam( "personId") int personId, DonationRecord newDonationRecord) { 
		Person person = service.getPersonById(personId);
		if(person == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Person found with id " + personId);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		BloodDonation bloodDonation = service.getBloodDonationById(bloodDonationId);
		if(bloodDonation == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Donation found with id " + bloodDonationId);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		DonationRecord addedDonationRecord = service.persistDonationRecord( newDonationRecord, bloodDonationId, personId);
		Response response = Response.ok( addedDonationRecord).build();
		return response;
	}
	
}