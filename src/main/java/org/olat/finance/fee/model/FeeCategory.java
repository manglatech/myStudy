package org.olat.finance.fee.model;

import java.util.Date;
import java.util.Set;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;

public interface FeeCategory extends Persistable, ModifiedInfo, CreateInfo{

	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getDescription();
	public abstract void setDescription(String description);

	public abstract String getInstituteId();
	public abstract void setInstituteId(String instituteId);
	
	public Set<FeeMapping> getFeeMapping();
	public void setFeeMapping(Set<FeeMapping> feeMapping);
	
	public Date getDueDate();
	public void setDueDate(Date dueDate);
	
}