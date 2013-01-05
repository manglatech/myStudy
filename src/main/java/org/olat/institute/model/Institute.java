package org.olat.institute.model;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;

public interface Institute extends Persistable, ModifiedInfo, CreateInfo{

	public abstract String getName();
	public abstract void setName(String name);

	public abstract String getInstituteId();
	public abstract void setInstituteId(String instituteId);
	
	public Integer getNumberOfUser();
	public void setNumberOfUser(Integer numberOfUser);
	
}