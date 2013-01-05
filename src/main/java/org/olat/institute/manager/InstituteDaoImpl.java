package org.olat.institute.manager;

import javax.persistence.EntityManager;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.institute.model.Institute;
import org.olat.institute.model.InstituteImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("instituteDao")
public class InstituteDaoImpl extends GenericDaoHibernateImpl<Institute, Long>
		implements InstituteDao {

	public InstituteDaoImpl(Class<Institute> type) {
		super(type);
	}
	public InstituteDaoImpl(){
		super(Institute.class);
	}

	@Autowired
	private DB dbInstance;
	
	@Override
	public Class getType() {
		return InstituteImpl.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
}
