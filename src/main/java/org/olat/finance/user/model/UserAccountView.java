package org.olat.finance.user.model;

import java.io.Serializable;

import org.olat.core.id.InstituteEnabled;

public interface UserAccountView extends InstituteEnabled, Serializable {

	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getEmail();
	public abstract void setEmail(String email);

	public abstract String getTemplateName();
	public abstract void setTemplateName(String templateName);

	public abstract String getGroupName();
	public abstract void setGroupName(String groupName);

	public abstract Long getPaidAmount();
	public abstract void setPaidAmount(Long paidAmount);

	public abstract Long getRemainingAmount();
	public abstract String getPaidStatus();

	public abstract Long getGroupId();
	public abstract void setGroupId(Long groupId);

	public abstract Long getTemplateId();
	public abstract void setTemplateId(Long templateId);

	public abstract Long getIdentityId();
	public abstract void setIdentityId(Long identityId);

	public abstract String getInstituteId();
	public abstract void setInstituteId(String instituteId);
	
	Long getTotalAmount();
	void setTotalAmount(Long totalAmount);

}