package org.olat.core.commons.persistence.manager;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.olat.institute.feature.model.InstituteFeature;

public abstract class GenericDaoHibernateImpl<T, PK extends Serializable> implements
		GenericDao<T, PK> {
	
	private Class<T> type;

	public GenericDaoHibernateImpl(Class<T> type) {
		this.type = type;
	}
	public T create(T o) {
		getEntityManager().persist(o);
		getEntityManager().flush();
		return o;
	}
    public List<T> create(List<T> entities){
    	for(T t: entities){
    		getEntityManager().persist(t);
    	}
    	getEntityManager().flush();
    	return entities;
    }
    public abstract Class<T> getType();
    
	public T load(PK id) {
		return getEntityManager().find(getType(),id);
	}
	public List<T> load(List<PK> ids){
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select f from ").append(type.getName())
				.append(" f ").append(" where f.key in (:ids)");

		List<T> list = getEntityManager()
				.createQuery(sb.toString(), type)
				.setParameter("ids", ids).getResultList();
		return list;
	}
	public List<T> loadAll(){
		
		StringBuilder sb = new StringBuilder();
		sb.append("select f from ").append(type.getName())
				.append(" f ");
		
		List<T> list = getEntityManager()
				.createQuery(sb.toString(), getType())
				.getResultList();
		
		return list;
	}
	
	public void merge(T o) {
		getEntityManager().merge(o);
		getEntityManager().flush();
	}
	public void remove(T o) {
		getEntityManager().remove(o);
		getEntityManager().flush();
	}
	public abstract EntityManager getEntityManager();
	
	protected final boolean where(StringBuilder sb, boolean where) {
		if(where) {
			sb.append(" and ");
		} else {
			sb.append(" where ");
		}
		return true;
	}
	
	protected final boolean and(StringBuilder sb, boolean and) {
		if(and) sb.append(" and ");
		else sb.append(" where ");
		return true;
	}
	
	protected final boolean or(StringBuilder sb, boolean or) {
		if(or) sb.append(" or ");
		else sb.append(" ");
		return true;
	}
	
	

}