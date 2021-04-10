/***************************************************************************
 * File: PojoListener.java Course materials (21F) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 *
 * @author (original) Mike Norman
 * @date 2020 04
 */
package bloodbank.entity;

import java.time.Instant;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class PojoListener {

	@PrePersist
	public void setCreatedOnDate( PojoBase pojo) {
		Instant now = Instant.now();
		pojo.setCreated( now);
		pojo.setUpdated( now);
	}

	@PreUpdate
	public void setUpdatedDate( PojoBase pojo) {
		pojo.setCreated( Instant.now());
	}

}