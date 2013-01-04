package org.olat.finance.fee.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeImpl;
import org.olat.finance.fee.model.FeeMappingImpl;
import org.olat.finance.fee.model.SearchFeeParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeDao")
public class FeeDaoImpl extends GenericDaoHibernateImpl<Fee, Long> implements
		FeeDao {

	@Autowired
	private DB dbInstance;
	
	public FeeDaoImpl(){
		super(Fee.class);
	}
	
	public FeeDaoImpl(Class<Fee> type) {
		super(type);
	}

	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}

	@Override
	public List<Fee> findAllFee(Identity identity) {
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeImpl.class.getName()).append(" f ");
		sb.append("where f.instituteId=:instituteId ");

		TypedQuery<Fee> dbq = dbInstance.getCurrentEntityManager().createQuery(
				sb.toString(), Fee.class);
		dbq.setParameter("instituteId", identity.getInstituteId());
		dbq.setHint("org.hibernate.cacheable", Boolean.TRUE);

		List<Fee> list = dbq.getResultList();
		return list;
	}
	
	public List<Fee> findFeeByCategory(SearchFeeParams params) {
		StringBuilder query = new StringBuilder();

		query.append("select distinct(f) from ")
				.append(FeeImpl.class.getName()).append(" f ");
		
			if (params.isIncludeAdded()) {
				query.append(" where f.key in ( ");
			} else {
				query.append(" where f.key not in ( ");
			}
			query.append(" select distinct(fm.fee.key) from ")
					.append(FeeMappingImpl.class.getName()).append(" fm ")
					.append(" where fm.feeCategory.key=:categoryId ");
			query.append(" ) ");
	
		TypedQuery<Fee> dbq = dbInstance.getCurrentEntityManager().createQuery(
				query.toString(), Fee.class);
			dbq.setParameter("categoryId", params.getFeeCategoryId());
		
		List<Fee> list = dbq.getResultList();
		return list;
	}
	@Override
	public Class getType(){
    	return FeeImpl.class;
    }

	
}
