package org.olat.finance.fee.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.SearchFeeParams;

public interface FeeDao extends GenericDao<Fee, Long>{
	public List<Fee> findFeeByCategory(SearchFeeParams params);
	public List<Fee> findAllFee(Identity identity);
}
