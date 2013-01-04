package org.olat.finance.fee.model;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;

public interface FeeMapping extends Persistable, ModifiedInfo, CreateInfo{

	public abstract Fee getFee();
	public abstract void setFee(Fee fee);

	public abstract FeeCategory getFeeCategory();
	public abstract void setFeeCategory(FeeCategory category);

	public abstract Integer getPrice();
	public abstract void setPrice(Integer price);

}