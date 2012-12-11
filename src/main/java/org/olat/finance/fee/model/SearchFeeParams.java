package org.olat.finance.fee.model;

public class SearchFeeParams {
	
	private String instituteId;
	private Long feeCategoryId;
	private boolean includeAdded;
	
	public Long getFeeCategoryId() {
		return feeCategoryId;
	}

	public void setFeeCategoryId(Long feeCategoryId) {
		this.feeCategoryId = feeCategoryId;
	}
	
	public boolean isIncludeAdded() {
		return includeAdded;
	}

	public void setIncludeAdded(boolean includeAdded) {
		this.includeAdded = includeAdded;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
}
