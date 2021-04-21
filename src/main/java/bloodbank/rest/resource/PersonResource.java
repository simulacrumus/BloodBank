/**
 * File: PersonResource.java Course materials (21W) CST 8277
 *
 * @author Darryl Galaraga 
 * @author Elyse Ntigirishari
 * @author Emrah Kinay
 * @author Jephte Francois
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.CUSTOMER_ADDRESS_RESOURCE_PATH;
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.Address;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.SecurityUser;

@Path( PERSON_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class PersonResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getPersons() {
		LOG.debug( "retrieving all persons ...");
		List< Person> persons = service.getAllPeople();
		Response response = Response.ok( persons).build();
		return response;
	}

	@GET
	@RolesAllowed( { ADMIN_ROLE, USER_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getPersonById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific person " + id);
		Response response = null;
		Person person = null;
		if ( sc.isCallerInRole( ADMIN_ROLE)) {
			person = service.getPersonById( id);
			if(person==null) {
				HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Person found with id " + id);
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
			response = Response.status(Status.OK).entity( person).build();
		} else if ( sc.isCallerInRole( USER_ROLE)) {
			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
			SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
			person = sUser.getPerson();
			if ( person != null && person.getId() == id) {
				response = Response.status( Status.OK).entity( person).build();
			} else {
				HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Person found with id " + id);
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
		} else {
			response = Response.status( Status.BAD_REQUEST).build();
		}
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addPerson( Person newPerson) {
		if(newPerson.getFirstName() == null || newPerson.getFirstName().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "First name is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newPerson.getLastName() == null || newPerson.getLastName().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Last name is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		service.buildUserForNewPerson( newPerson);
//		Person newPersonWithIdTimestamps = service.persistPerson( newPerson);
		Response response = Response.ok( newPerson).build();
		return response;
	}
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deletePersonById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "deleting specific person " + id);
		Person person = service.getPersonById(id);
		if(person == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Person found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Person deletedPerson = service.deletePersonById(id);
		Response response = Response.ok( deletedPerson).build();
		return response;
	}
	
	
	@POST
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH + "/donationrecord")
	public Response addDonationRecord(@PathParam( RESOURCE_PATH_ID_ELEMENT) int personId, @QueryParam("bloodDonationId") int bloodDonationId, DonationRecord newDonationRecord) { 
		Person person = service.getPersonById( personId);
		if(person == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Person found with id " + personId);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		if(bloodDonationId > 0) {
			BloodDonation bloodDonation = service.getBloodDonationById( bloodDonationId);
			if(bloodDonation == null) {
				HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Blood Donation found with id " + personId);
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
		}
		DonationRecord addedDonationRecord = service.persistDonationRecord( newDonationRecord, bloodDonationId > 0 ? bloodDonationId : 0, personId);
		Response response = Response.ok( addedDonationRecord).build();
		return response;
	}	
}