package org.olat.finance.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.finance.user.util.AccountUtil;

@Entity
@Table(name = "o_user_account_summary_v")
@IdClass(UserAccountViewPK.class)
public class UserAccountViewImpl implements UserAccountView {

	private static final long serialVersionUID = -2792071329533697940L;

	@Id
	@Column(name = "group_id")
	public Long groupId;

	@Id
	@Column(name = "identity_id")
	public Long identityId;

	@Column(name = "fee_category_id")
	public Long templateId;

	@Column(name = "user_name")
	public String name;

	@Transient
	public String email;

	@Column(name = "template_name")
	public String templateName;

	@Column(name = "group_name")
	public String groupName;

	@Column(name = "user_paid_amount")
	public Long paidAmount;

	@Column(name = "total_amount")
	public Long totalAmount;

	@Column(name = "institute_id")
	public String instituteId;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public String getGroupName() {
		return groupName;
	}

	@Override
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public Long getPaidAmount() {
		if (paidAmount == null)
			return Long.valueOf(0);
		return paidAmount;
	}

	@Override
	public void setPaidAmount(Long paidAmount) {
		this.paidAmount = paidAmount;
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
	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
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
	public Long getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Override
	public Long getTemplateId() {
		return templateId;
	}

	@Override
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Override
	public Long getIdentityId() {
		return identityId;
	}

	@Override
	public void setIdentityId(Long identityId) {
		this.identityId = identityId;
	}

	@Override
	public String getInstituteId() {
		return instituteId;
	}

	@Override
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
}

class UserAccountViewPK implements Serializable {

	private static final long serialVersionUID = -7097468964119139873L;
	public Long groupId;
	public Long identityId;

	public UserAccountViewPK() {
	}

	public UserAccountViewPK(Long groupId, Long identityId, Long templateId) {

		this.groupId = groupId;
		this.identityId = identityId;

	}

	public Long getGroupId() {
		return groupId;
	}

	public Long getIdentityId() {
		return identityId;
	}

	public boolean equals(Object o) {
		return ((o instanceof UserAccountViewPK)
				&& groupId == ((UserAccountViewPK) o).getGroupId() && identityId == ((UserAccountViewPK) o)
					.getIdentityId());
	}

	@Override
	public int hashCode() {
		return Long.valueOf(groupId + identityId).hashCode();
	}

}
