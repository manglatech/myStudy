package org.olat.finance.user.ui;

import org.olat.core.gui.control.Event;
import org.olat.core.id.Identity;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.StringHelper;

public class UserAccountSearchEvent extends Event implements StateEntry {
	
	private static final long serialVersionUID = 3978824313870707336L;
	private String groupName;
	private String templateName;
	private String userName;
	
	private boolean isAllStatus;
	private boolean isPaidStatus;
	private boolean isUnPaidStatus;
	private boolean isPartialPaidStatus;
	
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
	
	public UserAccountSearchParams convertToSearchBusinessGroupParams(Identity identity) {
		UserAccountSearchParams params = new UserAccountSearchParams();
		
		params.setGroupName(StringHelper.containsNonWhitespace(groupName) ? groupName : null);
		params.setUserName((StringHelper.containsNonWhitespace(userName)) ? userName : null);
		params.setTemplateName((StringHelper.containsNonWhitespace(templateName)) ? templateName : null);
		params.setInstituteId(identity.getInstituteId());
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
		
		return clone;
	}

	
	
	
}
