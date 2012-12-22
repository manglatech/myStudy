package org.olat.finance.user.model;

import java.io.Serializable;

import org.olat.core.id.Identity;
import org.olat.core.id.InstituteEnabled;
import org.olat.finance.fee.model.FeeCategory;

public interface UserAccountView extends InstituteEnabled, Serializable {

	public Identity getIdentity();
	public FeeCategory getFeeCategory();
	public abstract Long getPaidAmount();
	public abstract Long getRemainingAmount();
	public abstract String getPaidStatusAsStr();
	public abstract String getInstituteId();
	public abstract Long getTotalAmount();
	public Integer getPaidStatusId();
	
}