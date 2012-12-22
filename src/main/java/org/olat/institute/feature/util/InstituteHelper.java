package org.olat.institute.feature.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.institute.feature.model.InstituteFeature;

public class InstituteHelper {

	public static Map<String, Map<Long, InstituteFeature>> convertInMap(
			List<InstituteFeature> features) {
		
		Map<String, Map<Long, InstituteFeature>> iFeatures = new HashMap<String, Map<Long,InstituteFeature>>();
		for(InstituteFeature feature : features){
			
			Map<Long, InstituteFeature> map = iFeatures.get(feature.getInstituteId());
			if(map == null){
				map  = new HashMap<Long, InstituteFeature>();
				iFeatures.put(feature.getInstituteId(), map);
			}
			map.put(feature.getFeatureId(), feature);
		}
		return iFeatures;
	}

}
