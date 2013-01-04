package org.olat.finance.user.payment.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.olat.core.commons.persistence.OLATPersistenceObject;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.fee.model.FeeIdentityMappingImpl;

@Entity
@Table(name="o_fee_identity_paid")
public class UserPaymentInfoImpl extends OLATPersistenceObject implements UserPaymentInfo {

	private static final long serialVersionUID = -7900459148646641749L;

	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "fee_identity_paid_id", updatable = false, nullable = false)
	private Long key;
	
	@Column(name = "paid_amount")
	private Integer paidAmount;
	
	@Column( name = "lastmodified")
	private Date lastModified;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity = FeeIdentityMappingImpl.class)
    @JoinColumn(name="fk_fee_identity_mapping_id")
	private FeeIdentityMapping feeIdentityMapping;
	
	@Override
	public Integer getPaidAmount() {
		return paidAmount;
	}

	@Override
	public void setPaidAmount(Integer paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	@Override
	public Long getKey() {
		return key;
	}
	@Override
	protected void setKey(Long key) {
		this.key = key;
	}

	@Override
	public FeeIdentityMapping getFeeIdentityMapping() {
		return feeIdentityMapping;
	}
	
	@Override
	public void setFeeIdentityMapping(FeeIdentityMapping feeIdentityMapping) {
		this.feeIdentityMapping = feeIdentityMapping;
	}
}
