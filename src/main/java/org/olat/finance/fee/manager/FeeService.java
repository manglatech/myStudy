package org.olat.finance.fee.manager;

import java.util.List;

import org.olat.core.id.Identity;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.fee.model.FeeMapping;

public interface FeeService {
	
	Fee createFee(Identity creator, Fee fee);
	void deleteFee(Fee fee);
	void deleteFee(List<Long> ids);
	List<Fee> findAllFee(Identity identity);
	public List<Fee> findFeeByCategory(Long categoryId, boolean includeAdded);
	
	FeeCategory createFeeCategory(Identity creator, FeeCategory feeCategory);
	void deleteFeeCategory(FeeCategory feeCategory);
	void deleteFeeCategory(List<Long> ids);
	List<FeeCategory> findAllFeeCategory(Identity identity);
	
	List<FeeMapping> addFeeMapping(Long categoryId, List<Long> feeIds);
	List<FeeMapping> findFeeMapping(FeeCategory feeCategory);
	void removeFeeMappings(List<Long> ids);
	void updateFeeMapping(List<FeeMapping> mappings);
	List<FeeIdentityMapping> findFeeIdentityMapping(FeeCategory feeCategory);
	void removeFeeIdentityMappings(List<Long> ids);
	boolean isIdentityInFeeCategory(Identity identity, FeeCategory feeCategory);
	void addIdentitiesToFeeCategory(List<Identity> toAdd,FeeCategory feeCategory);
	void copyFeeCategory(Identity identity, FeeCategory original, String name, String description,
			boolean copyFee);
	void addIdentitiesToFeeCategory(Identity identity, FeeCategory category);
	FeeCategory findFeeCategoryById(Long templateId);
	
	
}
