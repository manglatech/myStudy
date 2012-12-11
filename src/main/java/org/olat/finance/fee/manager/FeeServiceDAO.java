package org.olat.finance.fee.manager;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.finance.fee.model.FeeImpl;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.finance.fee.model.FeeMappingImpl;
import org.olat.finance.fee.model.SearchFeeParams;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupOrder;
import org.olat.group.model.SearchBusinessGroupParams;
import org.olat.resource.OLATResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feeServiceDao")
public class FeeServiceDAO {

	@Autowired
	private DB dbInstance;

	public Fee merge(Fee feeStructure) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		Fee mergedFee = em.merge(feeStructure);
		return mergedFee;
	}

	public FeeCategory merge(FeeCategory feeStructure) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		FeeCategory mergedFeeCategory = em.merge(feeStructure);
		return mergedFeeCategory;
	}

	public Fee createFee(Identity creator, Fee fee) {
		dbInstance.getCurrentEntityManager().persist(fee);
		return fee;
	}

	public FeeCategory createFeeCategory(Identity creator,
			FeeCategory feeCategory) {
		dbInstance.getCurrentEntityManager().persist(feeCategory);
		return feeCategory;
	}

	public FeeCategory loadFeeCategory(Long categoryId) {
		return dbInstance.getCurrentEntityManager().find(FeeCategoryImpl.class,
				categoryId);
	}

	public List<FeeMapping> addFeeMapping(List<FeeMapping> mappings) {
		for (FeeMapping mapping : mappings)
			dbInstance.getCurrentEntityManager().persist(mapping);
		return mappings;
	}

	public void deleteFee(Fee fee) {
		dbInstance.getCurrentEntityManager().remove(fee);
	}

	public void deleteFeeCategory(FeeCategory feeCategory) {
		dbInstance.getCurrentEntityManager().remove(feeCategory);
	}

	public List<FeeCategory> findFeeCategory(Identity identity,
			FeeCategory feeCategory) {

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

	public List<Fee> findFee(Identity identity, Fee fee) {

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

	public List<FeeCategory> loadFeeCategory(List<Long> ids) {

		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select f from ").append(FeeCategoryImpl.class.getName())
				.append(" f ").append(" where f.key in (:ids)");

		List<FeeCategory> feeCategories = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), FeeCategory.class)
				.setParameter("ids", ids).getResultList();

		return feeCategories;
	}

	public List<Fee> loadFees(List<Long> feeIds) {

		if (feeIds == null || feeIds.isEmpty()) {
			return Collections.emptyList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(FeeImpl.class.getName()).append(" f ")
				.append(" where f.key in (:ids)");

		List<Fee> fees = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Fee.class)
				.setParameter("ids", feeIds).getResultList();

		return fees;
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

	public List<Fee> findFee(Identity identity, SearchFeeParams params) {
		
		params.setInstituteId(identity.getInstituteId());
		TypedQuery<Fee> query = createFindDBQuery(params, Fee.class);
		List<Fee> fees = query.getResultList();
		return fees;
		
	}
	
	private <T> TypedQuery<T> createFindDBQuery(SearchFeeParams params, Class<T> resultClass) {
		StringBuilder query = new StringBuilder();
		boolean where = false;
		
		query.append("select distinct(f) from ").append(FeeImpl.class.getName()).append(" f ");
		if(params.getFeeCategoryId() != null){
			query.append(" inner join fetch f.feeCategoryMapping fm ");
		}
		if(params.getInstituteId() != null){
			where = where(query, where);
			query.append(" f.instituteId=:instituteId ");
		}
		if(params.getFeeCategoryId() != null){
			where = where(query, where);
			if(params.isIncludeAdded()){
				query.append(" fm.feeCategory in ( select distinct(fc) from ").append(FeeCategoryImpl.class.getName()).append(" fc where fc.key=:feeCategoryId )");
			}else{
				query.append(" fm.feeCategory not in ( select distinct(fc) from ").append(FeeCategoryImpl.class.getName()).append(" fc where fc.key=:feeCategoryId )");
			}
		}
		TypedQuery<T> dbq = dbInstance.getCurrentEntityManager().createQuery(query.toString(), resultClass);
		
		if(params.getInstituteId() != null){
			dbq.setParameter("instituteId", params.getInstituteId());
		}
		if(params.getFeeCategoryId() != null){
			dbq.setParameter("feeCategoryId", params.getFeeCategoryId());
		}
		return dbq;
	}
	
	private final boolean where(StringBuilder sb, boolean where) {
		if(where) {
			sb.append(" and ");
		} else {
			sb.append(" where ");
		}
		return true;
	}
	
	private final boolean and(StringBuilder sb, boolean and) {
		if(and) sb.append(" and ");
		else sb.append(" where ");
		return true;
	}
	
	private final boolean or(StringBuilder sb, boolean or) {
		if(or) sb.append(" or ");
		else sb.append(" ");
		return true;
	}

}
