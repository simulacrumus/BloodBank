package bloodbank.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.Hibernate;

/**
 * The primary key class for the contact database table.
 * 
 */
@Embeddable
@Access( AccessType.FIELD)
public class ContactPK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Basic( optional = false)
	@Column( name = "person_id", nullable = false)
	private int personId;

	@Basic( optional = false)
	@Column( name = "phone_id", nullable = false)
	private int phoneId;

	public ContactPK() {
	}

	public ContactPK( int personId, int phoneId) {
		setPersonId( personId);
		setPhoneId( phoneId);
	}

	public int getPersonId() {
		return this.personId;
	}

	public void setPersonId( int personId) {
		this.personId = personId;
	}

	public int getPhoneId() {
		return this.phoneId;
	}

	public void setPhoneId( int phoneId) {
		this.phoneId = phoneId;
	}

	@Override
	public String toString() {
		return String.format( "[Person.id:%d, Phone.id:%d]", personId, phoneId);
	}

	@Override
	public int hashCode() {
		return Objects.hash( getPhoneId(), getPersonId());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		ContactPK other = (ContactPK) obj;
		return getPhoneId() == other.getPhoneId() && getPersonId() == other.getPersonId();
	}

}