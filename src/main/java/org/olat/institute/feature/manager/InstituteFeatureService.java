package org.olat.institute.feature.manager;

import java.util.List;

import org.olat.institute.feature.model.InstituteFeature;

public interface InstituteFeatureService {

	void changeStatus(List<InstituteFeature> selectedItems, Integer status);
	List<InstituteFeature> findInstituteFeaturesByInstituteId(String instituteId);

}
