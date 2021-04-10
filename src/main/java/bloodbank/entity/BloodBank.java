package bloodbank.entity;

import java.io.Serializable;
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bloodbank.rest.serializer.BloodBankSerializer;

/**
 * The persistent class for the blood_bank database table.
 */
@Entity
@Table( name = "blood_bank")
@NamedQuery( name = BloodBank.ALL_BLOODBANKS_QUERY_NAME, query = "SELECT distinct b FROM BloodBank b left JOIN FETCH b.donations")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE)
@AttributeOverride( name = "id", column = @Column( name = "bank_id"))
//columnDefinition, discriminatorType
@DiscriminatorColumn( columnDefinition = "bit(1)", name = "privately_owned", discriminatorType = DiscriminatorType.INTEGER)
@JsonSerialize(using = BloodBankSerializer.class)
public abstract class BloodBank extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_BLOODBANKS_QUERY_NAME = "BloodBank.findAll";
	public static final String DONATION_COUNT = "BloodBank.donationCOunt";

	@Basic( optional = false)
	@Column( nullable = false, length = 100)
	private String name;

	@OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "bank")
//	@JoinColumn( name = "bank_id", referencedColumnName = "bank_id")
	private Set< BloodDonation> donations;

	@Transient
	private boolean isPublic;
	
	protected BloodBank( boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	@JsonIgnore
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