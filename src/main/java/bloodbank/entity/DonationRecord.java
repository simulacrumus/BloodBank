package bloodbank.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.Hibernate;

/**
 * The persistent class for the donation_record database table.
 * 
 */
@Entity
@Table( name = "donation_record")
@NamedQuery( name = DonationRecord.ALL_RECORDS_QUERY_NAME, query = "SELECT d FROM DonationRecord d")
@AttributeOverride( name = "id", column = @Column( name = "record_id"))
public class DonationRecord extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_RECORDS_QUERY_NAME = "DonationRecord.findAll";

	@OneToOne( fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn( name = "donation_id", referencedColumnName = "donation_id")
	private BloodDonation donation;

	@ManyToOne( fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
	@JoinColumn( name = "person_id", referencedColumnName = "id", nullable = false)
	private Person owner;

	private byte tested;

	public DonationRecord() {

	}

	public BloodDonation getDonation() {
		return donation;
	}

	public void setDonation( BloodDonation donation) {
		this.donation = donation;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner( Person owner) {
		this.owner = owner;
	}

	public byte getTested() {
		return tested;
	}

	public void setTested( boolean tested) {
		this.tested = (byte) ( tested ? 0b0001 : 0b0000);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + Objects.hash( getId(), getOwner().getId(), getTested());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		DonationRecord other = (DonationRecord) obj;
		return Objects.equals( getId(), other.getId()) && Objects.equals( getOwner().getId(), other.getOwner().getId())
				&& getTested() == other.getTested();
	}

}