package org.olat.finance.fee.manager;

import java.util.List;

import javax.persistence.EntityManager;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.finance.fee.model.FeeMappingImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeMappingDao")
public class FeeMappingDaoImpl extends GenericDaoHibernateImpl<FeeMapping, Long> implements FeeMappingDao{

	@Autowired
	private DB dbInstance;
	
	public FeeMappingDaoImpl() {
		super(FeeMapping.class);
	}
	public FeeMappingDaoImpl(Class<FeeMapping> type) {
		super(type);
	}
	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
	public List<FeeMapping> findFeeMappingByCategory(FeeCategory feeCategory) {
		
		Long categoryId = feeCategory.getKey();
		
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeMappingImpl.class.getName()).append(" f ")
				.append(" where f.feeCategory.key=:categoryId)");

		List<FeeMapping> fees = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeMapping.class)
				.setParameter("categoryId", categoryId).getResultList();

		return fees;
		
	}
	
	@Override
	public Class getType(){
    	return FeeMappingImpl.class;
    }
}
