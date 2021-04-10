/**
 * File: RestConfig.java Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @date Mar 31, 2021
 * @author Mike Norman
 * @date 2020 10
 */
package bloodbank.rest.serializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import bloodbank.ejb.BloodBankService;
import bloodbank.entity.BloodBank;

public class BloodBankSerializer extends StdSerializer< BloodBank> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private static final Logger LOG = LogManager.getLogger();
	
    @EJB
    protected BloodBankService service;

	public BloodBankSerializer() {
		this( null);
	}

	public BloodBankSerializer( Class< BloodBank> t) {
		super( t);
	}

	/**
	 * This is to prevent back and forth serialization between Many to Many relations.<br>
	 * This is done by setting the relation to null.
	 */
	@Override
	public void serialize( BloodBank original, JsonGenerator generator, SerializerProvider provider)
			throws IOException {
		LOG.trace("serializeing={}",original);
		generator.writeStartObject();
		generator.writeNumberField( "id", original.getId());
		generator.writeStringField( "name", original.getName());
		generator.writeNumberField( "donation_count",  original.getDonations().size());
		generator.writeObjectField( "created", original.getCreated());
		generator.writeObjectField( "updated", original.getUpdated());
		generator.writeNumberField( "updated", original.getVersion());
		generator.writeEndObject();
	}
}