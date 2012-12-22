package org.olat.institute.feature.manager;

import org.olat.core.configuration.AbstractOLATModule;
import org.olat.core.configuration.PersistedProperties;
import org.olat.core.util.coordinate.CoordinatorManager;

public class FC extends AbstractOLATModule {
	
	private static final String INSTITITE_FEATURE_CHECK_ENABLED = "InstituteFeatureCheckEnabled";
	private static boolean instituteFeatureCheckEnabled;
	private CoordinatorManager coordinatorManager;

	private FC(){
	}
	
	public static boolean isEnabled(String instituteId, Integer featureId){
		if(!instituteFeatureCheckEnabled){
			return true;
		}
		return InstituteFeatureLoader.isEnabledForInstitute(Long.valueOf(featureId), instituteId);
	}
	public static boolean isEnabled(boolean configEnabled, String instituteId, Integer featureId){
		
		boolean enabled = true;
		
		if(!configEnabled){
			enabled = false;
		}if(featureId!= null && !FC.isEnabled(instituteId, featureId)){
			enabled = false;
		}
		return enabled;
	}

	@Override
	public void init() {
		
	}
	public void setCoordinator(CoordinatorManager coordinatorManager) {
		this.coordinatorManager = coordinatorManager;
	}
	
	@Override
	public void setPersistedProperties(PersistedProperties persistedProperties) {
		this.moduleConfigProperties = persistedProperties;
	}


	@Override
	protected void initDefaultProperties() {
		System.out.println("Default Called:");
		instituteFeatureCheckEnabled = getBooleanConfigParameter(INSTITITE_FEATURE_CHECK_ENABLED, false);
	}

	@Override
	protected void initFromChangedProperties() {
		// TODO Auto-generated method stub
		
	}
}
