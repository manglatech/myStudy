package org.olat.institute.feature.manager;

import java.util.List;

import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.manager.InstituteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("instituteFeatuerService")
public class InstituteFeatureServiceImpl implements InstituteFeatureService {

	@Autowired
	private InstituteFeatureDao instituteFeatureDao;
	
	@Autowired
	private InstituteDao instituteDao;
	
	@Autowired
	InstituteFeatureLoader loader;
	
	@Override
	public void changeStatus(List<InstituteFeature> selectedItems,
			Integer status) {
		for(InstituteFeature feature: selectedItems){
			InstituteFeature f = instituteFeatureDao.load(feature.getKey());
			f.setStatus(status);
			instituteFeatureDao.merge(f);
		}
		loader.init();
	}

	@Override
	public List<InstituteFeature> findInstituteFeaturesByInstituteId(
			String instituteId) {
		return instituteFeatureDao.findInstituteFeaturesByInstituteId(instituteId);
	}

}
