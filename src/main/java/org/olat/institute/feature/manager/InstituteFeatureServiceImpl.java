package org.olat.institute.feature.manager;

import java.util.ArrayList;
import java.util.List;

import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.feature.model.InstituteFeatureImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("instituteFeatuerService")
public class InstituteFeatureServiceImpl implements InstituteFeatureService {

	@Autowired
	private InstituteFeatureDao instituteFeatureDao;
	
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
	public List<InstituteFeature> loadUniqueInstitutes() {
		//return instituteFeatureDao.uniqueFeatures();
		List<InstituteFeature> features = new ArrayList<InstituteFeature>();
		InstituteFeature f = new InstituteFeatureImpl();
		f.setInstituteId("2");
		f.setFeatureName("UVPCE");
		features.add(f);
		
		InstituteFeature f1 = new InstituteFeatureImpl();
		f1.setInstituteId("1");
		f1.setFeatureName("NE - UVPCE");
		features.add(f1);
		
		return features;
	}

	@Override
	public List<InstituteFeature> findInstituteFeaturesByInstituteId(
			String instituteId) {
		return instituteFeatureDao.findInstituteFeaturesByInstituteId(instituteId);
	}

}
