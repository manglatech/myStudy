package org.olat.institute.feature.manager;

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
	

}
