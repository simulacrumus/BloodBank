package bloodbank.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Hibernate;

/**
 * The persistent class for the phone database table.
 */
@Entity
@Table( name = "phone")
@NamedQuery( name = "Phone.findAll", query = "SELECT p FROM Phone p")
@AttributeOverride( name = "id", column = @Column( name = "phone_id"))
public class Phone extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Basic( optional = false)
	@Column( name = "area_code", nullable = false, length = 10)
	private String areaCode;

	@Basic( optional = false)
	@Column( name = "country_code", nullable = false, length = 10)
	private String countryCode;

	@Basic( optional = false)
	@Column( nullable = false, length = 10)
	private String number;

	@OneToMany( cascade = { CascadeType.MERGE, CascadeType.REFRESH }, mappedBy = "phone", fetch = FetchType.LAZY)
//	@JoinColumn( name = "phone_id", referencedColumnName = "phone_id", insertable = false, updatable = false)
	private Set< Contact> contacts = new HashSet<>();

	public Phone setNumber( String countryCode, String areaCode, String number) {
		setCountryCode( countryCode);
		setAreaCode( areaCode);
		setNumber( number);

		return this;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode( String areaCode) {
		this.areaCode = areaCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode( String countryCode) {
		this.countryCode = countryCode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber( String number) {
		this.number = number;
	}

	public Set< Contact> getContacts() {
		return contacts;
	}

	public void setContacts( Set< Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Objects.hash( getAreaCode(), getCountryCode(), getNumber());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		Phone other = (Phone) obj;
		return Objects.equals( getAreaCode(), other.getAreaCode())
				&& Objects.equals( getCountryCode(), other.getCountryCode())
				&& Objects.equals( getNumber(), other.getNumber());
	}

}