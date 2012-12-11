package org.olat.finance.fee.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;

public interface FeeIdentityMappingDao extends GenericDao<FeeIdentityMapping, Long> {

	public List<FeeIdentityMapping> findFeeMappingByCategory(FeeCategory feeCategory);

	public boolean isIdentityInFeeCategory(Long feeCategoryId, Long identityId);

	public FeeIdentityMapping searchFeeMapping(Long identityId, Long categoryId);
	
}
