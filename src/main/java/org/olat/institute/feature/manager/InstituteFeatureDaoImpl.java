package org.olat.institute.feature.manager;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.institute.feature.model.InstituteFeature;
import org.olat.institute.feature.model.InstituteFeatureImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("instituteFeatureDao")
public class InstituteFeatureDaoImpl extends GenericDaoHibernateImpl<InstituteFeature, Long> implements InstituteFeatureDao {

	@Autowired
	private DB dbInstance;
	public InstituteFeatureDaoImpl(){
		super(InstituteFeature.class);
	}
	@Override
	public Class getType() {
		return InstituteFeatureImpl.class;
	}
	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
	
	@Override
	public List<InstituteFeature> findInstituteFeaturesByInstituteId(
			String instituteId) {
		if (instituteId == null) {
			return Collections.emptyList();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select f from ").append(getType().getName())
				.append(" f ").append(" where f.instituteId=:instituteId)");

		List<InstituteFeature> list = getEntityManager()
				.createQuery(sb.toString(), InstituteFeature.class)
				.setParameter("instituteId", instituteId).getResultList();
		
		return list;
	}
	

}
