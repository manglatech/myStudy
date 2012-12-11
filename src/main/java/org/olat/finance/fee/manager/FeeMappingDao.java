package org.olat.finance.fee.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeMapping;

public interface FeeMappingDao extends GenericDao<FeeMapping, Long> {

	public List<FeeMapping> findFeeMappingByCategory(FeeCategory feeCategory);
	
}
