package org.olat.institute.feature.model;

import org.olat.core.id.Persistable;

public interface InstituteFeature extends Persistable{
	
	public Long getFeatureId();
	public String getInstituteId();
	
	public void setFeatureId(Long featureId);
	public void setInstituteId(String instituteId);
	
	public String getFeatureName();
	public void setFeatureName(String featureName);
	
	public Integer getStatus();
	public void setStatus(Integer status);
	
}
