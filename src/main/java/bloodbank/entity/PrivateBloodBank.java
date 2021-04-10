package bloodbank.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "1")
public class PrivateBloodBank extends BloodBank {
	private static final long serialVersionUID = 1L;

	public PrivateBloodBank() {
		super(false);
	}
}
