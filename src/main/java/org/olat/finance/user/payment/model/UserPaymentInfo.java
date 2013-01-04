package org.olat.finance.user.payment.model;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;
import org.olat.finance.fee.model.FeeIdentityMapping;

public interface UserPaymentInfo extends Persistable, ModifiedInfo, CreateInfo{

	public abstract Integer getPaidAmount();

	public abstract void setPaidAmount(Integer paidAmount);

	public abstract FeeIdentityMapping getFeeIdentityMapping();

	public abstract void setFeeIdentityMapping(
			FeeIdentityMapping feeIdentityMapping);

}