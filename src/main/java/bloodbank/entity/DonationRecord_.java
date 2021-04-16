package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-04-16T13:00:34.286-0400")
@StaticMetamodel(DonationRecord.class)
public class DonationRecord_ {
	public static volatile SingularAttribute<DonationRecord, BloodDonation> donation;
	public static volatile SingularAttribute<DonationRecord, Person> owner;
	public static volatile SingularAttribute<DonationRecord, Byte> tested;
}
