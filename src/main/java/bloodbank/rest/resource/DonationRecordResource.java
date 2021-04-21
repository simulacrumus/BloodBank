/**
 * File: DonationRecordResource.java Course materials (21W) CST 8277
 *
 * @author Darryl Galaraga 
 * @author Elyse Ntigirishari
 * @author Emrah Kinay
 * @author Jephte Francois
 */
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.DONATION_RECORD_RESOURCE_NAME;
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
import bloodbank.entity.DonationRecord;

@Path(DONATION_RECORD_RESOURCE_NAME)
@Consumes( MediaType.APPLICATION_JSON)
@Produces( MediaType.APPLICATION_JSON)
public class DonationRecordResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected BloodBankService service;

	@Inject
	protected SecurityContext sc;

	@GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getDonationRecords() {
		LOG.debug( "retrieving all donation records ...");
		List< DonationRecord> donationRecords = service.getAllDonationRecords();
		Response response = Response.ok( donationRecords).build();
		return response;
	}

	@GET
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response getDonationRecordById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug( "try to retrieve specific donation record " + id);
		DonationRecord donationRecord = service.getDonationRecordById(id);
		if(donationRecord == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Donation Record found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		Response response = Response.status(Status.OK).entity( donationRecord).build();;
		return response;
	}
	
	@DELETE
	@RolesAllowed( { ADMIN_ROLE })
	@Path( RESOURCE_PATH_ID_PATH)
	public Response deleteDonationRecordById( @PathParam( RESOURCE_PATH_ID_ELEMENT) int id) {
		DonationRecord donationRecord = service.getDonationRecordById(id);
		if(donationRecord == null) {
			HttpErrorResponse error = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No Donation Record found with id " + id);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		DonationRecord deletedDonationRecord = service.deleteDonationRecordById(id);
		Response response = Response.ok( deletedDonationRecord).build();
		return response;
	}
}