package org.olat.finance.user.ui;

import java.util.Date;

import org.olat.core.gui.control.Event;
import org.olat.core.id.Identity;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.StringHelper;
import org.olat.finance.user.util.PaidStatus;

public class UserAccountSearchEvent extends Event implements StateEntry {
	
	private static final long serialVersionUID = 3978824313870707336L;
	private String groupName;
	private String templateName;
	private Date dueDate;
	
	private String userName;
	
	private boolean isAllStatus;
	private boolean isPaidStatus;
	private boolean isUnPaidStatus;
	private boolean isPartialPaidStatus;
	private boolean isMarkAsPaid;
	
	private static final String command = "userAccountSearch";
	
	public UserAccountSearchEvent() {
		super(command);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isAllStatus() {
		return isAllStatus;
	}

	public void setAllStatus(boolean isAllStatus) {
		this.isAllStatus = isAllStatus;
	}

	public boolean isPaidStatus() {
		return isPaidStatus;
	}

	public void setPaidStatus(boolean isPaidStatus) {
		this.isPaidStatus = isPaidStatus;
	}

	public boolean isUnPaidStatus() {
		return isUnPaidStatus;
	}

	public void setUnPaidStatus(boolean isUnPaidStatus) {
		this.isUnPaidStatus = isUnPaidStatus;
	}

	public boolean isPartialPaidStatus() {
		return isPartialPaidStatus;
	}

	public void setPartialPaidStatus(boolean isPartialPaidStatus) {
		this.isPartialPaidStatus = isPartialPaidStatus;
	}
	
	public boolean isMarkAsPaid() {
		return isMarkAsPaid;
	}

	public void setMarkAsPaid(boolean isMarkAsPaid) {
		this.isMarkAsPaid = isMarkAsPaid;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public UserAccountSearchParams convertToSearchBusinessGroupParams(Identity identity) {
		UserAccountSearchParams params = new UserAccountSearchParams();
		
		params.setGroupName(StringHelper.containsNonWhitespace(groupName) ? groupName : null);
		params.setUserName((StringHelper.containsNonWhitespace(userName)) ? userName : null);
		params.setTemplateName((StringHelper.containsNonWhitespace(templateName)) ? templateName : null);
		params.setInstituteId(identity.getInstituteId());
		params.setDueDate(dueDate);
		
		if(isPaidStatus){
			params.setPaidStatus(PaidStatus.PAID);
		}else if(isPartialPaidStatus){
			params.setPaidStatus(PaidStatus.PARTIAL_PAID);
		}else if(isUnPaidStatus){
			params.setPaidStatus(PaidStatus.NOT_PAID);
		}else if(isMarkAsPaid){
			params.setPaidStatus(PaidStatus.MARK_AS_PAID);
		}
		return params;
	}

	@Override
	public UserAccountSearchEvent clone() {
		UserAccountSearchEvent clone = new UserAccountSearchEvent();
		
		clone.groupName = groupName;
		clone.templateName = templateName;
		clone.userName = userName;
		clone.isAllStatus = isAllStatus;
		clone.isPaidStatus = isPaidStatus;
		clone.isPartialPaidStatus = isPartialPaidStatus;
		clone.isUnPaidStatus = isUnPaidStatus;
		clone.isMarkAsPaid = isMarkAsPaid;
		clone.dueDate = dueDate;
		
		return clone;
	}

	
	
	
}
