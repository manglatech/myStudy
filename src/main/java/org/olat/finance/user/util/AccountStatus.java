package org.olat.finance.user.util;

public enum AccountStatus {
	
	NOT_DEFINE(0,"Not Define"), 
	ASSIGNED(1,"Assigned"),
	UNASSIGNED(2,"UnAssigned");
	
	private Integer id; 
	private String value;
	
	AccountStatus(Integer id, String value){
		this.id = id;
		this.value= value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static AccountStatus find(Integer accountStatus) {
		
		if(NOT_DEFINE.getId() == accountStatus)
			return NOT_DEFINE;
		else if(ASSIGNED.getId() == accountStatus)
			return ASSIGNED;
		else if(UNASSIGNED.getId() == accountStatus)
			return UNASSIGNED;
		
		return null;
	}
	
	
}
