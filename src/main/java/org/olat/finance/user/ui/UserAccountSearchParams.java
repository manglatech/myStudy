package org.olat.finance.user.ui;

import org.olat.finance.user.util.PaidStatus;


public class UserAccountSearchParams {
	
	public UserAccountSearchParams() {
	}
	
	private String groupName;
	private String userName;
	private String templateName;
	private String instituteId;
	private PaidStatus paidStatus;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public PaidStatus getPaidStatus() {
		return paidStatus;
	}
	public void setPaidStatus(PaidStatus paidStatus) {
		this.paidStatus = paidStatus;
	}
	
}
