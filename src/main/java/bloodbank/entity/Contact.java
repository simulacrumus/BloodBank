package bloodbank.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.Hibernate;

/**
 * The persistent class for the contact database table.
 */
@Entity
@Table( name = "contact")
@NamedQuery( name = "Contact.findAll", query = "SELECT c FROM Contact c")
public class Contact extends PojoBaseCompositeKey< ContactPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ContactPK id;

	@MapsId( "personId")
	@ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, optional = false, fetch = FetchType.LAZY)
	@JoinColumn( name = "person_id", referencedColumnName = "id", nullable = false)
	private Person owner;

	@MapsId( "phoneId")
	@ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, optional = false, fetch = FetchType.LAZY)
	@JoinColumn( name = "phone_id", referencedColumnName = "phone_id", nullable = false)
	private Phone phone;

	@ManyToOne( cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.LAZY)
	@JoinColumn( name = "address_id", referencedColumnName = "address_id", nullable = true)
	private Address address;

	@Column( length = 100, name = "email")
	private String email;

	@Basic( optional = false)
	@Column( length = 10, name = "contact_type", nullable = false)
	private String contactType;

	public Contact() {
		id = new ContactPK();
	}

	@Override
	public ContactPK getId() {
		return id;
	}

	@Override
	public void setId( ContactPK id) {
		this.id = id;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner( Person owner) {
		id.setPersonId( owner.id);
		this.owner = owner;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone( Phone phone) {
		id.setPhoneId( phone.id);
		this.phone = phone;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress( Address address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email) {
		this.email = email;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType( String contactType) {
		this.contactType = contactType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Objects.hash( getAddress(), getContactType(), getEmail(), getId());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		Contact other = (Contact) obj;
		return Objects.equals( getAddress(), other.getAddress())
				&& Objects.equals( getContactType(), other.getContactType())
				&& Objects.equals( getEmail(), other.getEmail()) && Objects.equals( getId(), other.getId());
	}

}