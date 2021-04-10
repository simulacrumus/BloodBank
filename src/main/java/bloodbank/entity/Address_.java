package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-03-31T23:31:31.138-0400")
@StaticMetamodel(Address.class)
public class Address_ {
	public static volatile SingularAttribute<Address, String> streetNumber;
	public static volatile SingularAttribute<Address, String> city;
	public static volatile SingularAttribute<Address, String> country;
	public static volatile SingularAttribute<Address, String> province;
	public static volatile SingularAttribute<Address, String> street;
	public static volatile SingularAttribute<Address, String> zipcode;
	public static volatile SetAttribute<Address, Contact> contacts;
}
