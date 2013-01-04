package org.olat.finance.fee.manager;

import java.util.List;

import javax.persistence.EntityManager;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.fee.model.FeeIdentityMappingImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeIdentityMappingDao")
public class FeeIdentityMappingDaoImpl extends GenericDaoHibernateImpl<FeeIdentityMapping, Long> implements FeeIdentityMappingDao{

	@Autowired
	private DB dbInstance;
	
	public FeeIdentityMappingDaoImpl() {
		super(FeeIdentityMapping.class);
	}
	public FeeIdentityMappingDaoImpl(Class<FeeIdentityMapping> type) {
		super(type);
	}
	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
	public List<FeeIdentityMapping> findFeeMappingByCategory(FeeCategory feeCategory) {
		
		Long categoryId = feeCategory.getKey();
		
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeIdentityMappingImpl.class.getName()).append(" f ")
				.append(" where f.feeCategory.key=:categoryId)");

		List<FeeIdentityMapping> fees = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeIdentityMapping.class)
				.setParameter("categoryId", categoryId).getResultList();

		return fees;
		
	}
	
	@Override
	public Class getType(){
    	return FeeIdentityMappingImpl.class;
    }
	
	@Override
	public boolean isIdentityInFeeCategory(Long categoryId, Long identityId) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeIdentityMappingImpl.class.getName()).append(" f ")
				.append(" where f.feeCategory.key=:categoryId and f.identity.key=:identityId)");

		List<FeeIdentityMapping> fees = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeIdentityMapping.class)
				.setParameter("categoryId", categoryId)
				.setParameter("identityId", identityId)
				.getResultList();
		
		if(fees != null && fees.size() > 0)
			return true;
		
		return false;
	}
	@Override
	public FeeIdentityMapping searchFeeMapping(Long identityId, Long categoryId) {

		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeIdentityMappingImpl.class.getName()).append(" f ")
				.append(" where f.feeCategory.key=:categoryId and f.identity.key=:identityId)");

		List<FeeIdentityMapping> mappings = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeIdentityMapping.class)
				.setParameter("categoryId", categoryId)
				.setParameter("identityId", identityId)
				.getResultList();

		if(mappings != null && mappings.size() > 0)
			return mappings.get(0);
		return null;
	}
}
