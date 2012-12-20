package org.olat.finance.user.model;

import java.io.Serializable;

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
import org.olat.finance.user.util.AccountUtil;

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
	public String getPaidStatus() {
		String status = "";
		if (totalAmount == null)
			status = AccountUtil.NOT_ASSIGNED;
		else {
			if (getRemainingAmount() > 0) {

				if (getRemainingAmount().longValue() == getTotalAmount()
						.longValue()) {
					status = AccountUtil.NOT_PAID;
				} else {
					status = AccountUtil.PARTIAL_PAID;
				}
			}else if(getRemainingAmount().longValue() == 0) {
				status = AccountUtil.PAID;
			}else{
				status = AccountUtil.OVER_PAID;
			}
		}
		return status;
	}
	@Override
	public String getInstituteId() {
		return instituteId;
	}
	@Override
	public void setInstituteId(String id) {
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
