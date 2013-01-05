package org.olat.institute.manager;

import java.util.List;

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityManager;
import org.olat.institute.feature.manager.InstituteFeatureDao;
import org.olat.institute.feature.manager.InstituteFeatureLoader;
import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.feature.model.InstituteFeatureImpl;
import org.olat.institute.model.Institute;
import org.olat.institute.model.InstituteImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("instituteService")
public class InstituteServiceImpl implements InstituteService {

	@Autowired
	InstituteDao instituteDao;
	
	@Autowired
	private InstituteFeatureDao instituteFeatureDao;
	
	@Autowired
	InstituteFeatureLoader loader;
	
	private String superInstitute = "0";
	
	@Override
	public List<Institute> findAllInstitute() {
		List<Institute> institutes = instituteDao.loadAll();
		return institutes;
	}

	@Override
	public Institute createInstitute(String id, String name,
			String numberOfUsers) {
		Institute institute = new InstituteImpl();
		institute.setInstituteId(id);
		institute.setName(name);
		institute.setNumberOfUser(Integer.valueOf(numberOfUsers));
		institute = instituteDao.create(institute);
		
		List<InstituteFeature> features = instituteFeatureDao.findInstituteFeaturesByInstituteId(superInstitute);
		
		for(InstituteFeature feature : features){
			InstituteFeature f = new InstituteFeatureImpl();
			f.setInstituteId(institute.getInstituteId());
			f.setFeatureId(feature.getFeatureId());
			f.setStatus(feature.getStatus());
			f.setFeatureName(feature.getFeatureName());
			instituteFeatureDao.create(f);
		}
		
		/*BaseSecurity mgr = BaseSecurityManager.getInstance();
		mgr.createAndPersistIdentity(username, user, provider, authusername, "w2study");*/
		
		loader.init();
		
		return institute;
	}

	@Override
	public Institute updateInstitute(Long key, String name, String numberOfUsers) {
		Institute institute = instituteDao.load(key);
		if(institute != null){
			institute.setName(name);			
		}
		instituteDao.merge(institute);
		return institute;
	}
	@Override
	public void deleteInstitute(List<Institute> selectedItems) {
		List<InstituteFeature> features = instituteFeatureDao.findInstituteFeaturesByInstituteId(superInstitute);
		for(InstituteFeature feature : features){
			instituteFeatureDao.remove(feature);
		}
		for(Institute i : selectedItems){
			Institute i1 = instituteDao.load(i.getKey());
			instituteDao.remove(i1);
		}
	}
	
}
