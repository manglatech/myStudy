package org.olat.institute.feature.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.feature.model.InstituteFeatureImpl;
import org.olat.institute.feature.util.InstituteHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/dao_dummy_context.xml",
		"file:src/main/java/org/olat/institute/feature/_spring/instituteFeatures.xml" })
public class InstituteFeatureLoaderTest {

	@Autowired
	InstituteFeatureLoader loader;

	@Autowired
	FC fc;
	
	List<InstituteFeature> features = new ArrayList<InstituteFeature>();

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testIfMethodInvoked() {
		Assert.assertNotNull(loader);
		Assert.assertFalse(InstituteFeatureLoader.isEnabledForInstitute(
				Long.valueOf(1), "1"));
		Assert.assertTrue(true);
	}

	@Test
	public void testConvertToMap() {
		
		InstituteFeature feature = getInstituteFeature(Long.valueOf(1), "1");
		features.add(feature);
		Map<String, Map<Long, InstituteFeature>> map = InstituteHelper.convertInMap(features);
		
		Assert.assertEquals(map.keySet().size(), 1);
		Assert.assertEquals(map.keySet().iterator().next(), "1");
		
		Map<Long, InstituteFeature> iMap = map.get("1");
		Assert.assertNotNull(iMap);
		Assert.assertEquals(iMap.size(), 1);
		Assert.assertEquals(iMap.keySet().iterator().next(), Long.valueOf(1));
		
	}

	private InstituteFeature getInstituteFeature(Long featureId, String instituteId) {
		InstituteFeature feature = new InstituteFeatureImpl();
		feature.setFeatureId(featureId);
		feature.setInstituteId(instituteId);
		return feature;
	}

}
