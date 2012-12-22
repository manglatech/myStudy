package org.olat.finance.user.ui;

import org.olat.core.gui.control.Event;
import org.olat.core.id.Identity;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.StringHelper;
import org.olat.finance.user.util.AccountStatus;

public class CreateUserAccountSearchEvent extends Event implements StateEntry {
	
	private static final long serialVersionUID = 3978824313870707336L;
	private String groupName;
	private String courseName;
	private String userName;
	
	private boolean isAssignedStatus;
	private boolean isUnAssignedStatus;
	private static final String command = "createuserAccountSearch";
	
	public CreateUserAccountSearchEvent() {
		super(command);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isAssignedStatus() {
		return isAssignedStatus;
	}

	public void setAssignedStatus(boolean isAssignedStatus) {
		this.isAssignedStatus = isAssignedStatus;
	}

	public boolean isUnAssignedStatus() {
		return isUnAssignedStatus;
	}

	public void setUnAssignedStatus(boolean isUnAssignedStatus) {
		this.isUnAssignedStatus = isUnAssignedStatus;
	}
	
	public CreateUserAccountSearchParams convertToSearchBusinessGroupParams(Identity identity) {
		CreateUserAccountSearchParams params = new CreateUserAccountSearchParams();
		
		params.setGroupName(StringHelper.containsNonWhitespace(groupName) ? groupName : null);
		params.setUserName((StringHelper.containsNonWhitespace(userName)) ? userName : null);
		params.setCourseName((StringHelper.containsNonWhitespace(courseName)) ? courseName : null);
		params.setInstituteId(identity.getInstituteId());
		
		if(isUnAssignedStatus){
			params.setAccountStatus(AccountStatus.UNASSIGNED);
		}else if(isAssignedStatus){
			params.setAccountStatus(AccountStatus.ASSIGNED);
		}
		
		return params;
	}

	@Override
	public CreateUserAccountSearchEvent clone() {
		CreateUserAccountSearchEvent clone = new CreateUserAccountSearchEvent();
		
		clone.groupName = groupName;
		clone.courseName = courseName;
		clone.userName = userName;
		clone.isAssignedStatus = isAssignedStatus;
		clone.isUnAssignedStatus = isUnAssignedStatus;
		return clone;
	}

	
	
	
}
