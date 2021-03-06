package org.olat.finance.fee.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.core.commons.persistence.OLATPersistenceObject;

@Entity
@Table(name = "o_fee_category")
public class FeeCategoryImpl extends OLATPersistenceObject implements FeeCategory {

	@Transient
	private static final long serialVersionUID = 7787885304726339141L;

	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "fee_category_id", updatable = false, nullable = false)
	private Long key;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "lastmodified")
	private Date lastModified;

	@Column(name = "fk_institute_id")
	private String instituteId;

	@Column(name = "due_date")
	private Date dueDate;
	
	@OneToMany(mappedBy="feeCategory",targetEntity = FeeMappingImpl.class, cascade=CascadeType.ALL)
    private Set<FeeMapping> feeMapping = new HashSet<FeeMapping>();
	
	@OneToMany(mappedBy="feeCategory", targetEntity = FeeIdentityMappingImpl.class, cascade=CascadeType.ALL)
    private Set<FeeIdentityMapping> feeIdentityMapping = new HashSet<FeeIdentityMapping>();
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public void setDescription(String description) {
		this.description = description;
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
	public Set<FeeMapping> getFeeMapping() {
		return feeMapping;
	}
	public void setFeeMapping(Set<FeeMapping> feeMapping) {
		this.feeMapping = feeMapping;
	}
	public Set<FeeIdentityMapping> getFeeIdentityMapping() {
		return feeIdentityMapping;
	}
	public void setFeeIdentityMapping(Set<FeeIdentityMapping> feeIdentityMapping) {
		this.feeIdentityMapping = feeIdentityMapping;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
