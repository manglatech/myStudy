package org.olat.finance.fee.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.basesecurity.IdentityImpl;
import org.olat.core.commons.persistence.OLATPersistenceObject;
import org.olat.core.id.Identity;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.payment.model.UserPaymentInfoImpl;

@Entity
@Table(name = "o_fee_identity_mapping")
public class FeeIdentityMappingImpl extends OLATPersistenceObject implements FeeIdentityMapping {

	@Transient
	private static final long serialVersionUID = -5689654389902102067L;

	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "fee_identity_mapping_id", updatable = false, nullable = false)
	private Long key;

	@Column(name = "paid_status")
	private Integer paid;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity = IdentityImpl.class)
    @JoinColumn(name="fk_identity_id")
	private Identity identity;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity = FeeCategoryImpl.class)
    @JoinColumn(name="fk_fee_category_id")
	private FeeCategory feeCategory;
	
	@OneToMany(mappedBy="feeIdentityMapping", targetEntity = UserPaymentInfoImpl.class, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<UserPaymentInfo> userPayments;
	
	@Column(name = "lastmodified")
	private Date lastModified;
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}
	
	public FeeCategory getFeeCategory() {
		return feeCategory;
	}

	public void setFeeCategory(FeeCategory feeCategory) {
		this.feeCategory = feeCategory;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Integer getPaid() {
		return paid;
	}

	public void setPaid(Integer paid) {
		this.paid = paid;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public Set<UserPaymentInfo> getUserPayments() {
		return userPayments;
	}

	public void setUserPayments(Set<UserPaymentInfo> userPayments) {
		this.userPayments = userPayments;
	}
	

}
