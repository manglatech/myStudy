package org.olat.finance.user.util;

public enum PaidStatus {
	
	NOT_DEFINE(0,"Not Define"), 
	PAID(1,"Paid"),
	NOT_PAID(2,"Not Paid"),
	PARTIAL_PAID(3,"Partial Paid"),
	OVER_PAID(4,"Over Paid"),
	MARK_AS_PAID(5,"Mark As Paid");
	
	private Integer id; 
	private String value;
	
	PaidStatus(Integer id, String value){
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

	public static PaidStatus find(Integer paidStatus) {
		
		if(NOT_DEFINE.getId() == paidStatus)
			return NOT_DEFINE;
		else if(PAID.getId() == paidStatus)
			return PAID;
		else if(NOT_PAID.getId() == paidStatus)
			return NOT_PAID;
		else if(PARTIAL_PAID.getId() == paidStatus)
			return PARTIAL_PAID;
		else if(OVER_PAID.getId() == paidStatus)
			return OVER_PAID;
		else if(MARK_AS_PAID.getId() == paidStatus)
			return MARK_AS_PAID;
		
		return null;
	}
	
	
}
