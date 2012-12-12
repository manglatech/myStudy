package org.olat.institute.feature.manager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/java/org/olat/institute/feature/_spring/instituteFeatures.xml",
		"file:src/main/java/org/olat/core/commons/persistence/_spring/core_persistence.xml"})
public class InstituteFeatureLoaderTest{

	@Autowired
	InstituteFeatureLoader loader;

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}
	
	@Test
	public void testHello(){
		Assert.assertTrue(true);
	} 

}
