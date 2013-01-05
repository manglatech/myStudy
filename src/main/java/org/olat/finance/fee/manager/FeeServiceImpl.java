package org.olat.finance.fee.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.olat.core.id.Identity;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.fee.model.FeeIdentityMappingImpl;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.finance.fee.model.FeeMappingImpl;
import org.olat.finance.fee.model.SearchFeeParams;
import org.olat.finance.user.util.PaidStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeService")
public class FeeServiceImpl implements FeeService {
	
	@Autowired	
	FeeDao feeDao;
	@Autowired	
	FeeMappingDao feeMappingDao;
	@Autowired	
	FeeCategoryDao feeCategoryDao;
	@Autowired	
	FeeIdentityMappingDao feeIdentityMappingDao;
	
	@Override
	public Fee createFee(Identity creator, Fee fee) {
		fee.setInstituteId(creator.getInstituteId());
		return feeDao.create(fee);
	}
	@Override
	public FeeCategory createFeeCategory(Identity creator,
			FeeCategory feeCategory) {
		feeCategory.setInstituteId(creator.getInstituteId());
		return feeCategoryDao.create(feeCategory);
	}
	
	@Override
	public List<FeeMapping> addFeeMapping(Long categoryId, List<Long> feeIds) {
		
		FeeCategory feeCategory = feeCategoryDao.load(categoryId);
		List<Fee> fees = feeDao.load(feeIds);
		
		List<FeeMapping> mappings = new ArrayList<FeeMapping>();
		for(Fee fee : fees){
			FeeMapping mapping = new FeeMappingImpl();
			mapping.setFeeCategory(feeCategory);
			mapping.setFee(fee);
			mappings.add(mapping);
		}
		return feeMappingDao.create(mappings);
	}
	@Override
	public List<FeeMapping> findFeeMapping(FeeCategory feeCategory) {
		return feeMappingDao.findFeeMappingByCategory(feeCategory);
	}
	@Override
	public void deleteFee(Fee fee) {
		feeDao.remove(fee);
	}
	@Override
	public void deleteFeeCategory(FeeCategory feeCategory) {
		feeCategoryDao.remove(feeCategory);
	}
	@Override
	public void deleteFee(List<Long> ids) {
		List<Fee> fees = feeDao.load(ids);
		for(Fee fee : fees){
			feeDao.remove(fee);
		}
	}
	@Override
	public void deleteFeeCategory(List<Long> ids) {
		List<FeeCategory> feeCategories = feeCategoryDao.load(ids);
		for(FeeCategory feeCategory : feeCategories){
			feeCategoryDao.remove(feeCategory);
		}
	}
	@Override
	public List<FeeCategory> findAllFeeCategory(Identity identity) {
		return feeCategoryDao.findAllFeeCategory(identity);
	}
	@Override
	public List<Fee> findAllFee(Identity identity) {
		return feeDao.findAllFee(identity);
	}
	
	@Override
	public List<Fee> findFeeByCategory(Long categoryId, boolean includeAdded){
		SearchFeeParams param = new SearchFeeParams();
		param.setFeeCategoryId(categoryId);
		param.setIncludeAdded(includeAdded);
		return feeDao.findFeeByCategory(param);
	}
	@Override
	public void removeFeeMappings(List<Long> ids) {
		List<FeeMapping> feeMappings = feeMappingDao.load(ids);
		for(FeeMapping feeMapping : feeMappings){
			feeMappingDao.remove(feeMapping);
		}
	}
	@Override
	public void updateFeeMapping(List<FeeMapping> mappings) {
		for(FeeMapping feeMapping : mappings){
			feeMappingDao.merge(feeMapping);
		}
	}
	@Override
	public List<FeeIdentityMapping> findFeeIdentityMapping(
			FeeCategory feeCategory) {
		return feeIdentityMappingDao.findFeeMappingByCategory(feeCategory);
	}
	@Override
	public void removeFeeIdentityMappings(List<Long> ids) {
		List<FeeIdentityMapping> list = feeIdentityMappingDao.load(ids);
		for(FeeIdentityMapping entity : list){
			feeIdentityMappingDao.remove(entity);
		}
	}
	@Override
	public boolean isIdentityInFeeCategory(Identity identity,
			FeeCategory feeCategory) {
		return feeIdentityMappingDao.isIdentityInFeeCategory(feeCategory.getKey(), identity.getKey());
	}
	@Override
	public void addIdentitiesToFeeCategory(List<Identity> toAdd,FeeCategory feeCategory) {
		for(Identity identity : toAdd){
			addIdentitiesToFeeCategory(identity, feeCategory);
		}
	}
	
	@Override
	public void addIdentitiesToFeeCategory(Identity identity,
			FeeCategory feeCategory) {
		FeeIdentityMapping mapping = new FeeIdentityMappingImpl();
		mapping.setFeeCategory(feeCategory);
		mapping.setIdentity(identity);
		mapping.setPaid(PaidStatus.NOT_DEFINE.getId());
		feeIdentityMappingDao.create(mapping);
	}
	
	@Override
	public void copyFeeCategory(Identity identity, FeeCategory original, String name,
			String description, boolean copyFee) {

		System.out.println("COPYING FEE CATEGORY");
		System.out.println("NAME"+name);
		System.out.println("DESC"+description);
		System.out.println("copyFee"+copyFee);
		
		FeeCategory feeCategory = new FeeCategoryImpl();
		feeCategory.setName(name);
		feeCategory.setDescription(description);
		
		feeCategory = createFeeCategory(identity, feeCategory);
		
		if(copyFee){
			
			FeeCategory oCategory = feeCategoryDao.load(original.getKey());
			Set<FeeMapping> mappings = oCategory.getFeeMapping();
			
			List<Long> feeIds = new ArrayList<Long>();
			for(FeeMapping mapping: mappings){
				feeIds.add(mapping.getFee().getKey());
			}
			addFeeMapping(feeCategory.getKey(), feeIds);
		}
		
	}
	@Override
	public FeeCategory findFeeCategoryById(Long templateId) {
		return feeCategoryDao.load(templateId);
	}
}
