package bloodbank.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "0")
public class PublicBloodBank extends BloodBank {

	private static final long serialVersionUID = 1L;

	public PublicBloodBank() {
		super(true);
	}
}
