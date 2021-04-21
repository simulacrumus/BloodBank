/**
 * File: AddressResource.java Course materials (21W) CST 8277

 *
 * @author Darryl Galaraga 
 * @author Elyse Ntigirishari
 * @author Emrah Kinay
 * @author Jephte Francois
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADDRESS_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;
import static bloodbank.utility.MyConstants.ADMIN_ROLE;

import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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

@Path(ADDRESS_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class AddressResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;
	
	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
	public Response getAddresses() {
		LOG.debug( "retrieving all addresses ...");
		List< Address> addresses = service.getAllAddresses();
		Response response = Response.status( addresses == null || addresses.isEmpty() ? Status.NOT_FOUND : Status.OK).entity( addresses).build();
		return response;
	}

	@GET
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getAddressById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific address " + id);	
		Address address = service.getAddressById(id);
		if(address == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Address found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.status( address == null ? Status.NOT_FOUND : Status.OK).entity( address).build();
		return response;
	}

	@POST
	@RolesAllowed( { ADMIN_ROLE })
	public Response addAddress( Address newAddress) {
		LOG.debug( "add a new address");
		Response response = null;
		if(newAddress.getCountry() == null || newAddress.getCountry().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Country is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		if(newAddress.getProvince() == null || newAddress.getProvince().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Province is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		if(newAddress.getCity() == null || newAddress.getCity().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "City is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		if(newAddress.getStreet() == null || newAddress.getStreet().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Street is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		if(newAddress.getStreet() == null || newAddress.getStreet().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Street is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		} 
		
		if(newAddress.getStreetNumber() == null || newAddress.getStreetNumber().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "Street Number is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		} 
		
		if(newAddress.getZipcode() == null || newAddress.getZipcode().equals("")) {
			HttpErrorResponse error = new HttpErrorResponse(Status.BAD_REQUEST.getStatusCode(), "ZIP Code is missing");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		} 
			
		Address addedAddress = service.persistAddress( newAddress);
		response = Response.ok( addedAddress).build();
		return response;
	}	
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteAddressById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		Address address = service.getAddressById(id);
		if(address == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Address found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Address deletedAddress = service.deleteAddressById(id);
		Response response = Response.ok( deletedAddress).build();
		return response;
	}
	
	@PUT
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response updateAddressById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id,  Address updatingAddress) {
		Response response = null;
		Address updatedAddress = service.updateAddressById(id, updatingAddress);
		if(updatedAddress == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Address found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		response = Response.ok( updatedAddress).build();
		return response;
	}
	
}