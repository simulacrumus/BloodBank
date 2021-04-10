package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-03-31T23:31:44.675-0400")
@StaticMetamodel(Phone.class)
public class Phone_ extends PojoBase_ {
	public static volatile SingularAttribute<Phone, String> areaCode;
	public static volatile SingularAttribute<Phone, String> countryCode;
	public static volatile SingularAttribute<Phone, String> number;
	public static volatile SetAttribute<Phone, Contact> contacts;
}
