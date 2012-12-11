package org.olat.finance.fee;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.finance.fee.model.FeeImpl;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;

public class FeeServiceTest extends OlatTestCase{
	
	@Autowired
	FeeService feeService;
	
	FeeCategory feeStructure = new FeeCategoryImpl();
	FeeCategory parent = new FeeCategoryImpl();
	Fee child1 = new FeeImpl();
	Fee child2 = new FeeImpl();
	
	@Before
	public void setUp(){
		
		feeStructure.setName("Sample Fee");
		feeStructure.setDescription("Description of Sample Fee");
		feeStructure.setInstituteId("0");
		
		parent.setName("Sample Template Fee");
		parent.setDescription("Description of Sample Fee");
		parent.setInstituteId("0");
		
		child1.setName("Sample Fee 1");
		child1.setDescription("Description of Sample Fee");
		child1.setInstituteId("0");
		
		child2.setName("Sample Fee 2");
		child2.setDescription("Description of Sample Fee");
		child2.setInstituteId("0");
		
	}
	@After
	public void tearDown(){
	}
	
	@Test
	public void testCreateFeeStrucutre(){
		feeStructure = feeService.createFeeCategory(null,feeStructure);
		Assert.assertNotNull(feeStructure);
	}
	/*@Test
	public void testFindFeeStrucutre(){
		List<FeeStructure> list = new ArrayList<FeeStructure>();
		list = feeStructureService.findFeeStructure(null,feeStructure);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		for(FeeStructure feeSt : list)
			feeStructureService.deleteFeeStructure(feeSt);
				
		Assert.assertTrue(true);
	}*/
	
	@Test
	public void testAddFeeToFeeStructure(){
		
		
		parent = feeService.createFeeCategory(null, parent);
		child1 = feeService.createFee(null, child1);
		child2 = feeService.createFee(null, child2);
		
		List<Long> childIds = new ArrayList<Long>();
		childIds.add(child1.getKey());
		childIds.add(child2.getKey());
		
		List<FeeMapping> mappings  = feeService.addFeeMapping(parent.getKey(),childIds);
		Assert.assertNotNull(mappings);
		Assert.assertEquals(2, mappings.size());
	}
	@Test
	public void testFindFeeMapping(){
		feeService.findFeeByCategory(new Long(14155776), false);
		Assert.assertTrue(false);
	}
	@Test
	public void testDeleteFeeFromStrucutre(){
		Assert.assertTrue(false);
	}
	
		
}
