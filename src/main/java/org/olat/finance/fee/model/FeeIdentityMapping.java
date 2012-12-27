package org.olat.finance.fee.model;

import java.util.Set;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.Identity;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;
import org.olat.finance.user.payment.model.UserPaymentInfo;

public interface FeeIdentityMapping extends Persistable, ModifiedInfo, CreateInfo{

	public abstract FeeCategory getFeeCategory();
	public abstract void setFeeCategory(FeeCategory category);

	public Integer getPaid();
	public void setPaid(Integer paid);
	
	public Identity getIdentity();
	public void setIdentity(Identity identity);
	
	public Set<UserPaymentInfo> getUserPayments();
	public void setUserPayments(Set<UserPaymentInfo> userPayments);
	
}