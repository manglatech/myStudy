package org.olat.finance.fee.model;

import java.util.Set;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;

public interface Fee extends Persistable, ModifiedInfo, CreateInfo{

	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getDescription();
	public abstract void setDescription(String description);

	public abstract String getInstituteId();
	public abstract void setInstituteId(String instituteId);
	
	public Set<FeeMapping> getFeeCategoryMapping();
	public void setFeeCategoryMapping(Set<FeeMapping> feeCategoryMapping);

}