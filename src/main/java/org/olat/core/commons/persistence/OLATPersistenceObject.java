package org.olat.core.commons.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.Persistable;

@MappedSuperclass
public abstract class OLATPersistenceObject implements CreateInfo, Persistable {

	@Version
	@Column(name = "version")
	private int version = 0;

	@Version
	@Column(name = "creationDate")
	protected Date creationDate;

	/**
	 * @see org.olat.core.commons.persistence.Auditable#getCreationDate()
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * For Hibernate only
	 * 
	 * @param version
	 */
	private void setVersion(int version) {
		this.version = version;
	}

	/**
	 * For Hibernate only
	 * 
	 * @param date
	 */
	private void setCreationDate(Date date) {
		creationDate = date;
	}

	/**
	 * @return Long
	 */
	public abstract Long getKey();
	protected abstract void setKey(Long key);
	
	/**
	 * @see org.olat.core.commons.persistence.Persistable#equalsByPersistableKey(org.olat.core.commons.persistence.Persistable)
	 */
	public boolean equalsByPersistableKey(Persistable persistable) {
		if (this.getKey().compareTo(persistable.getKey()) == 0)
			return true;
		else
			return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "key:" + getKey() + "=" + super.toString();
	}

}
