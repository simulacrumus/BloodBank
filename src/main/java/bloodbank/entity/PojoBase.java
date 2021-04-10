/***************************************************************************
 * File: PojoBase.java Course materials (21F) CST 8277
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 * @author Mike Norman
 * @date 2020 04
 */
package bloodbank.entity;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Abstract class that is base of (class) hierarchy for all @Entity classes
 */
@MappedSuperclass
@Access( AccessType.FIELD) // NOTE: by using this annotations, any annotation on a properties is ignored without warning
@EntityListeners( { PojoListener.class })
public abstract class PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Basic( optional = false)
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column( nullable = false, name = "id")
	protected int id;

	@Version
	protected int version;

	@Basic( optional = false)
	@Column( nullable = false, name = "created")
	protected long epochCreated;

	@Basic( optional = false)
	@Column( nullable = false, name = "updated")
	protected long epochUpdated;

	@Transient
	protected Instant created;

	@Transient
	protected Instant updated;

	public int getId() {
		return id;
	}

	public void setId( int id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion( int version) {
		this.version = version;
	}

	public Instant getCreated() {
		if ( created == null)
			setCreatedEpochMilli( epochCreated);
		return created;
	}

	public long getCreatedEpochMilli() {
		return created.toEpochMilli();
	}

	public void setCreated( Instant created) {
		setCreatedEpochMilli( created.toEpochMilli());
	}

	public void setCreatedEpochMilli( long created) {
		this.epochCreated = created;
		this.created = Instant.ofEpochMilli( created);
	}

	public void setUpdated( Instant updated) {
		setUpdatedEpochMilli( updated.toEpochMilli());
	}

	public void setUpdatedEpochMilli( long updated) {
		this.epochUpdated = updated;
		this.updated = Instant.ofEpochMilli( updated);
	}

	public Instant getUpdated() {
		if ( updated == null)
			setUpdatedEpochMilli( epochUpdated);
		return updated;
	}

	public long getUpdatedEpochMilli() {
		return updated.toEpochMilli();
	}

	/**
	 * @see <a href= "https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">
	 *      How to implement equals and hashCode using the JPA entity identifier (Primary Key) </a>
	 * @see <a href=
	 *      "https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/">
	 *      The best way to implement equals, hashCode, and toString with JPA and Hibernate </a>
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals( Object obj) {
		if ( this == obj) {
			return true;
		}
		if ( obj == null) {
			return false;
		}
		if ( !( obj instanceof PojoBase)) {
			return false;
		}
		PojoBase other = (PojoBase) obj;
		if ( id != other.id) {
			return false;
		}
		return true;
	}
}