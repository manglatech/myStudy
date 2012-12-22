package org.olat.institute.feature.manager;

import java.util.List;
import java.util.Map;

import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.feature.util.InstituteHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("featureLoader")
public class InstituteFeatureLoader {
	
	private static Map<String, Map<Long, InstituteFeature>> iFeatures;
	
	@Autowired
	InstituteFeatureDao instituteFeatureDao;
	
	private InstituteFeatureLoader(){
	}
	
	public void init(){
		List<InstituteFeature> features = instituteFeatureDao.loadAll();
		iFeatures = InstituteHelper.convertInMap(features);
	}
	
	public static boolean isEnabledForInstitute(Long featureId, String instituteId){
		
		boolean enabled = true;
		
		Map<Long, InstituteFeature> instituteFeature = iFeatures.get(instituteId);
		if(instituteFeature != null){
			InstituteFeature f = instituteFeature.get(featureId);
			if(f != null && f.getStatus() == 0){
				enabled = false;
			}
		}
		
		return enabled;
	}
	public static void reload(){
		(new InstituteFeatureLoader()).init();
	}
}
