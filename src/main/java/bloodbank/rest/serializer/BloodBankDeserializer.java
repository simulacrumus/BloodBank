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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import bloodbank.entity.BloodBank;
import bloodbank.entity.PrivateBloodBank;
import bloodbank.entity.PublicBloodBank;

public class BloodBankDeserializer extends StdDeserializer< BloodBank> implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger();

	public BloodBankDeserializer() {
		this( null);
	}

	public BloodBankDeserializer( Class< BloodBank> t) {
		super( t);
	}

	/**
	 * This is to prevent back and forth serialization between Many to Many relations.<br>
	 * This is done by setting the relation to null.
	 */
	@Override
	public BloodBank deserialize( JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		TreeNode tn = p.readValueAsTree();
		BloodBank bank = new PublicBloodBank();
		if ( tn.get( "is_public") != null) {
			boolean isPublic = Boolean.parseBoolean( tn.get( "is_public").toString());
			bank = isPublic ? new PublicBloodBank() : new PrivateBloodBank();
		}

		if ( tn.get( "name") != null) {
			String name = tn.get( "name").toString().replace( "\"", "");
			bank.setName( name);
		}

		if ( tn.get( "id") != null) {
			// this can be used when creating another entity with bloodbank as dependency.
			// this id can be used to get the bloodbank from DB again and set it your object.
			bank.setId( Integer.valueOf( tn.get( "id").toString()));
		}
		return bank;
	}

}