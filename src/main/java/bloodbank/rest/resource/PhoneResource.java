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
import bloodbank.entity.Address;
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
    @RolesAllowed({ADMIN_ROLE})
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
		Phone phone =service.getPhoneById(id);
		if(phone == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Phone found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.status( phone == null ? Status.NOT_FOUND : Status.OK).entity( phone).build();
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addPhone( Phone newPhone) {
		if(newPhone.getAreaCode() == null || newPhone.getAreaCode().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Area code is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newPhone.getCountryCode() == null || newPhone.getCountryCode().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Country code is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newPhone.getNumber() == null || newPhone.getNumber().trim().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Number is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		Phone addedPhone = service.persistPhone( newPhone);
		Response response = Response.ok( addedPhone).build();
		return response;
	}	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deletePhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		Phone phone = service.getPhoneById(id);
		if(phone == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Phone found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Phone deletedPhone = service.deletePhoneById(id);
		Response response = Response.ok( deletedPhone).build();
		return response;
	}
	
	@PUT
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response updatePhoneById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id,  Phone newPhone) {
		Response response = null;
		Phone phone = service.getPhoneById(id);
		if(phone == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Phone found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		if(newPhone.getAreaCode() == null || newPhone.getAreaCode().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Area code is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newPhone.getCountryCode() == null || newPhone.getCountryCode().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Country code is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		if(newPhone.getNumber() == null || newPhone.getNumber().trim().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Number is required");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		Phone deletedPhone = service.updatePhoneById(id, newPhone);
		response = Response.ok( deletedPhone).build();
		return response;
	}
}