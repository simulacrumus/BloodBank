package bloodbank.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the blood_donation database table.
 */
@Entity
@Table( name = "blood_donation")
@AttributeOverride( name = "id", column = @Column( name = "donation_id"))
@NamedQuery( name = BloodDonation.ALL_BLOOD_DONATION_QUERY_NAME, query = "SELECT distinct d FROM BloodDonation d left JOIN FETCH d.bank")
@NamedQuery( name = BloodDonation.BLOOD_DONATION_BY_ID_QUERY_NAME, query = "SELECT distinct d FROM BloodDonation d left JOIN FETCH d.bank where d.id=:param1")
public class BloodDonation extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_BLOOD_DONATION_QUERY_NAME = "BloodDonation.findAll";
	public static final String BLOOD_DONATION_BY_ID_QUERY_NAME = "BloodDonation.findById";
	
	@ManyToOne( optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinColumn( name = "bank_id", referencedColumnName = "bank_id")
	@JsonIgnore
	private BloodBank bank;

	@OneToOne( fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH}, optional = true, mappedBy = "donation")
//	@JoinColumn( name = "donation_id", referencedColumnName = "donation_id", nullable = true, insertable = false, updatable = false)
	@JsonIgnore
	private DonationRecord record;

	@Basic( optional = false)
	@Column( nullable = false)
	private int milliliters;

	@Embedded
	private BloodType bloodType;

	public BloodBank getBank() {
		return bank;
	}

	public void setBank( BloodBank bank) {
		this.bank = bank;
	}

	public DonationRecord getRecord() {
		return record;
	}

	public void setRecord( DonationRecord record) {
		this.record = record;
	}

	public int getMilliliters() {
		return milliliters;
	}

	public void setMilliliters( int milliliters) {
		this.milliliters = milliliters;
	}

	public BloodType getBloodType() {
		return bloodType;
	}

	public void setBloodType( BloodType bloodType) {
		this.bloodType = bloodType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Objects.hash( getBank().getId(), getBloodType(), getMilliliters());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		BloodDonation other = (BloodDonation) obj;
		return Objects.equals( getBank().getId(), other.getBank().getId())
				&& Objects.equals( getBloodType(), other.getBloodType()) && getMilliliters() == other.getMilliliters();
	}
}