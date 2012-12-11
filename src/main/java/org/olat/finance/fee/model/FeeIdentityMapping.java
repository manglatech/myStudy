package org.olat.finance.fee.model;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.Identity;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;

public interface FeeIdentityMapping extends Persistable, ModifiedInfo, CreateInfo{

	public abstract FeeCategory getFeeCategory();
	public abstract void setFeeCategory(FeeCategory category);

	public Integer getPaid();
	public void setPaid(Integer paid);
	
	public Identity getIdentity();
	public void setIdentity(Identity identity);
	
}