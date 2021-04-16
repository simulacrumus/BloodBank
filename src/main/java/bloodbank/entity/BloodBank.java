package bloodbank.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bloodbank.rest.serializer.BloodBankDeserializer;
import bloodbank.rest.serializer.BloodBankSerializer;

/**
 * The persistent class for the blood_bank database table.
 */
@Entity
@AttributeOverride( name = "id", column = @Column( name = "bank_id"))
@Table( name = "blood_bank")
@NamedQuery( name = BloodBank.ALL_BLOODBANKS_QUERY_NAME, query = "SELECT distinct b FROM BloodBank b left JOIN FETCH b.donations")
@NamedQuery( name = BloodBank.IS_DUPLICATE_QUERY_NAME, query = "SELECT count(b) FROM BloodBank b where b.name=:param1")
@NamedQuery( name = BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, query = "SELECT distinct b FROM BloodBank b left JOIN FETCH b.donations where b.id=:param1")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE)
//columnDefinition, discriminatorType
@DiscriminatorColumn( columnDefinition = "bit(1)", name = "privately_owned", discriminatorType = DiscriminatorType.INTEGER)
@JsonSerialize(using = BloodBankSerializer.class)
@JsonDeserialize(using = BloodBankDeserializer.class)
public abstract class BloodBank extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_BLOODBANKS_QUERY_NAME = "BloodBank.findAll";
	public static final String IS_DUPLICATE_QUERY_NAME = "BloodBank.isDuplicate";
	public static final String GET_BLOODBANK_BY_ID_QUERY_NAME = "BloodBank.findById";
	public static final String DONATION_COUNT = "BloodBank.donationCount";

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String name;

	@OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "bank")
//	@JoinColumn( name = "bank_id", referencedColumnName = "bank_id")
	@JsonIgnore
	private Set< BloodDonation> donations = new HashSet<BloodDonation>();

	@Transient
	private boolean isPublic;
	
	protected BloodBank() {
		this.isPublic = true;
	}
	
	protected BloodBank( boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	public Set< BloodDonation> getDonations() {
		return donations;
	}

	public void setDonations( Set< BloodDonation> donations) {
		this.donations = donations;
	}

	public void setName( String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Objects.hash( getName());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		BloodBank other = (BloodBank) obj;
		return Objects.equals( getName(), other.getName());
	}

}