package org.olat.finance.fee.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;

public interface FeeCategoryDao extends GenericDao<FeeCategory, Long> {
	
	public List<FeeCategory> findAllFeeCategory(Identity identity);
}
