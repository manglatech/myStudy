package org.olat.institute.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.core.commons.persistence.OLATPersistenceObject;

@Entity
@Table(name = "o_institute")
public class InstituteImpl extends OLATPersistenceObject implements Institute {

	@Transient
	private static final long serialVersionUID = 7787885304726339141L;

	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "institute_internal_id", updatable = false, nullable = false)
	private Long key;

	@Column(name = "institute_id")
	private String instituteId;

	@Column(name = "name")
	private String name;
	
	@Column(name = "number_of_users")
	private Integer numberOfUser;

	@Column(name = "lastmodified")
	private Date lastModified;

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public Date getLastModified() {
		return lastModified;
	}
	@Override
	public void setLastModified(Date date) {
		this.lastModified = date;
	}
	@Override
	public String getInstituteId() {
		return instituteId;
	}
	@Override
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	@Override
	public Long getKey() {
		return key;
	}
	@Override
	protected void setKey(Long key) {
		this.key = key;
	}
	public Integer getNumberOfUser() {
		return numberOfUser;
	}
	public void setNumberOfUser(Integer numberOfUser) {
		this.numberOfUser = numberOfUser;
	}
	
	
}
