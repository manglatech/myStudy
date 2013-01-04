package org.olat.finance.user.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.basesecurity.IdentityImpl;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.finance.user.util.PaidStatus;

@Entity
@Table(name = "o_user_account_summary_v")
@IdClass(UserAccountViewPK.class)
public class UserAccountViewImpl implements UserAccountView {

	private static final long serialVersionUID = -2792071329533697940L;

	@Id
	@ManyToOne(fetch=FetchType.LAZY, targetEntity = IdentityImpl.class)
    @JoinColumn(name="fk_identity_id")
	private Identity identity;
	
	@Id
	@ManyToOne(fetch=FetchType.LAZY, targetEntity = FeeCategoryImpl.class)
    @JoinColumn(name="fk_fee_category_id")
	private FeeCategory feeCategory;
	
	@Column(name = "user_paid_amount")
	private Long paidAmount;

	@Column(name = "total_amount")
	private Long totalAmount;

	@Column(name = "institute_id")
	private String instituteId;
	
	@Column(name = "paid_status")
	private Integer paidStatusId;
	
	@Column(name = "due_date")
	private Date dueDate;
	
	public Identity getIdentity() {
		return identity;
	}
	public FeeCategory getFeeCategory() {
		return feeCategory;
	}
	@Override
	public Long getPaidAmount() {
		if (paidAmount == null)
			return Long.valueOf(0);
		return paidAmount;
	}
	@Override
	@Transient
	public Long getRemainingAmount() {
		return getTotalAmount() - getPaidAmount();
	}
	@Override
	public Long getTotalAmount() {
		if (totalAmount == null)
			return Long.valueOf(0);
		return totalAmount;
	}
	@Override
	@Transient
	public String getPaidStatusAsStr() {
		if(paidStatusId != null){
			PaidStatus.find(paidStatusId).getValue();
		}
		return PaidStatus.NOT_DEFINE.toString();
	}
	@Override
	public String getInstituteId() {
		return instituteId;
	}
	@Override
	public void setInstituteId(String id) {
	}
	public Integer getPaidStatusId() {
		return paidStatusId;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public String getPaidStatus() {
		
		String status;
		if(PaidStatus.MARK_AS_PAID.getId() == getPaidStatusId()){
			status = PaidStatus.MARK_AS_PAID.getValue();
		}else if(getTotalAmount() == 0){
			status = PaidStatus.NO_FEE_FOUND.getValue();
		}else if(getPaidAmount() == 0){
			status = PaidStatus.NOT_PAID.getValue();
		}else if(getTotalAmount() > getPaidAmount()){
			status = PaidStatus.PARTIAL_PAID.getValue();
		}else if(getPaidAmount() > getTotalAmount()){
			status = PaidStatus.OVER_PAID.getValue();
		}else if(getTotalAmount().equals(getPaidAmount())){
			status = PaidStatus.PAID.getValue();
		}else{
			status = "NA";
		}
		return status;
	}
}

class UserAccountViewPK implements Serializable {

	private static final long serialVersionUID = -7097468964119139873L;
	public FeeCategory feeCategory;
	public Identity identity;

	public UserAccountViewPK() {
	}

	public UserAccountViewPK(FeeCategory feeCategory, Identity identity) {

		this.feeCategory = feeCategory;
		this.identity = identity;

	}
	public FeeCategory getFeeCategory() {
		return feeCategory;
	}

	public Identity getIdentity() {
		return identity;
	}

	public boolean equals(Object o) {
		return ((o instanceof UserAccountViewPK)
				&& feeCategory.getKey() == ((UserAccountViewPK) o).getFeeCategory().getKey() && identity.getKey() == ((UserAccountViewPK) o)
					.getIdentity().getKey());
	}

	@Override
	public int hashCode() {
		return Long.valueOf(feeCategory.getKey() + identity.getKey()).hashCode();
	}

}
