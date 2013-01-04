package org.olat.finance.fee.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeCategoryDao")
public class FeeCategoryDaoImpl extends GenericDaoHibernateImpl<FeeCategory, Long> implements FeeCategoryDao{

	@Autowired
	private DB dbInstance;
	
	public FeeCategoryDaoImpl(){
		super(FeeCategory.class);
	}
	
	public FeeCategoryDaoImpl(Class<FeeCategory> type) {
		super(type);
	}
	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
	
	public List<FeeCategory> findAllFeeCategory(Identity identity) {

		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeCategoryImpl.class.getName())
				.append(" f ");
		sb.append("where f.instituteId=:instituteId ");

		TypedQuery<FeeCategory> dbq = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeCategory.class);
		dbq.setParameter("instituteId", identity.getInstituteId());
		dbq.setHint("org.hibernate.cacheable", Boolean.TRUE);

		List<FeeCategory> list = dbq.getResultList();
		return list;
	}
	@Override
	public Class getType(){
    	return FeeCategoryImpl.class;
    }

}
