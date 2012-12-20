package org.olat.finance.user.util;

public enum PaidStatus {
	
	PAID_IN_FULL(0,"Paid In Full"), 
	PAID(1,"Paid"),
	NOT_PAID(2,"Not Paid"),
	PARTIAL_PAID(3,"Partial Paid"),
	OVER_PAID(4,"Over Paid");
	
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
		
		if(PAID_IN_FULL.getId() == paidStatus)
			return PAID_IN_FULL;
		else if(PAID.getId() == paidStatus)
			return PAID;
		else if(NOT_PAID.getId() == paidStatus)
			return NOT_PAID;
		else if(PARTIAL_PAID.getId() == paidStatus)
			return PARTIAL_PAID;
		else if(OVER_PAID.getId() == paidStatus)
			return OVER_PAID;
		
		return null;
	}
	
	
}
