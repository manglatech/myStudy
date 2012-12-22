package org.olat.institute.feature.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.institute.feature.model.InstituteFeature;

public interface InstituteFeatureDao extends GenericDao<InstituteFeature, Long>{

	List<InstituteFeature> findInstituteFeaturesByInstituteId(String featureId);

	
}
