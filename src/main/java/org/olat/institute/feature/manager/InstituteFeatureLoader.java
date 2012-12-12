package org.olat.institute.feature.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.institute.feature.model.InstituteFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("featureLoader")
public class InstituteFeatureLoader {
	
	private static Map<String, Map<Long, InstituteFeature>> iFeatures = new HashMap<String, Map<Long,InstituteFeature>>();
	
	@Autowired
	InstituteFeatureDao instituteFeatureDao;
	
	private InstituteFeatureLoader(){
	}
	
	public void init(){
		List<InstituteFeature> features = instituteFeatureDao.loadAll();
		for(InstituteFeature feature : features){
			
			Map<Long, InstituteFeature> map = iFeatures.get(feature.getInstituteId());
			if(map == null){
				map  = new HashMap<Long, InstituteFeature>();
				iFeatures.put(feature.getInstituteId(), map);
			}
			map.put(feature.getFeatureId(), feature);
		}
	}
	public static boolean isEnabledForInstitute(Long featureId, String instituteId){
		
		boolean enabled = false;
		
		Map<Long, InstituteFeature> instituteFeature = iFeatures.get(instituteId);
		if(instituteFeature != null){
			InstituteFeature f = instituteFeature.get(featureId);
			if(f != null){
				enabled = true;
			}
		}
		
		return enabled;
	}
}
