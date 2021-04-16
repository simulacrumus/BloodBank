/**
 * File: BloodBankResource.java Course materials (21W) CST 8277
 *
 * @author Emrah Kinay
 * @author (original) Mike Norman update by : I. Am. A. Student 040nnnnnnn
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.USER_ROLE;
import static bloodbank.utility.MyConstants.BLOODBANK_BLOODDONATION_RESOURCE_PATH;

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
import bloodbank.entity.BloodDonation;
import bloodbank.entity.Phone;

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
	@RolesAllowed( { ADMIN_ROLE, USER_ROLE})
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific blood bank " + id);	
		BloodBank bloodBank = service.getBloodBankById(id);
		if(bloodBank == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Bank found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.status( bloodBank == null ? Status.NOT_FOUND : Status.OK).entity( bloodBank).build();
		return response;
	}
	
	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addBloodBank( BloodBank newBloodBank) {
		LOG.debug( "add a new bloodbank");
		if(newBloodBank.getName() == null || newBloodBank.getName().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Blood Bank name is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(service.isBloodBankDuplicated(newBloodBank)) {
			HttpErrorResponse error = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "entity already exists");
			return Response.status(Status.CONFLICT).entity(error).build();
		} else {
			BloodBank addedBloodbank = service.persistBloodBank( newBloodBank);
			Response response = Response.ok( addedBloodbank).build();
			return response;
		}
	}
	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteBloodBankById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "deleteing bloodbank " + id);
		BloodBank deletedBloodBank = service.deleteBloodBankById(id);
		if(deletedBloodBank == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Bank found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.ok( deletedBloodBank).build();
		return response;
	}	

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	@Path( BLOODBANK_BLOODDONATION_RESOURCE_PATH)
	public Response addBloodDonationByBankId( @PathParam( RESOURCE_PATH_ID_ELEMENT) int bankId, BloodDonation newBloodDonation) {
		LOG.debug( "add a new blood donation");
		if(newBloodDonation.getBloodType() == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Blood Type is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newBloodDonation.getBloodType().getBloodGroup() == null || newBloodDonation.getBloodType().getBloodGroup().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Blood Group is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		BloodBank bloodBank = service.getBloodBankById(bankId);
		if(bloodBank == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "blood bank with id " + bankId + " does not exist");
			return Response.status(Status.NOT_FOUND).entity(error).build();
		} else {
			BloodDonation addedBloodDonation = service.persistBloodDonation(newBloodDonation, bankId);
			Response response = Response.ok( addedBloodDonation).build();
			return response;
		}
	}
	
	@GET
	@RolesAllowed( { ADMIN_ROLE, USER_ROLE})
	@Path( "/"+BLOOD_DONATION_RESOURCE_NAME)
	public Response getAllBloodDonations() {
		LOG.debug( "retrieving all blood donations ...");
		List< BloodDonation> bloodDonations = service.getAllBloodDonations();
		Response response = Response.ok( bloodDonations).build();
		return response;
	}
	
	@GET
	@RolesAllowed( { ADMIN_ROLE })
	@Path( "/"+BLOOD_DONATION_RESOURCE_NAME+RESOURCE_PATH_ID_PATH)
	public Response getBloodDonationById(@PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
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
	@Path( "/"+BLOOD_DONATION_RESOURCE_NAME+RESOURCE_PATH_ID_PATH)
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
}