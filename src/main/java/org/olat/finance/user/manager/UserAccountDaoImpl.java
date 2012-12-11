package org.olat.finance.user.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.model.UserAccountViewImpl;
import org.olat.finance.user.ui.UserAccountSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userAccountDao")
public class UserAccountDaoImpl implements UserAccountDao{

	@Autowired
	private DB dbInstance;
	
	public UserAccountDaoImpl() {
	}

	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}

	@Override
	public List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params) {
		
		StringBuilder query = new StringBuilder();

		query.append("select uav from ")
				.append(UserAccountViewImpl.class.getName()).append(" uav ");
		
		query.append(" where uav.instituteId=:instituteId ");
		
		if(params.getGroupName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "uav", "groupName", "groupName");
		}
		if(params.getTemplateName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "uav", "templateName", "templateName");
		}
		if(params.getUserName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "uav", "name", "userName");
		}
		
		TypedQuery<UserAccountView> dbq = dbInstance.getCurrentEntityManager().createQuery(
				query.toString(), UserAccountView.class);
		
		dbq.setParameter("instituteId", params.getInstituteId());
		if(params.getGroupName() != null){
			dbq.setParameter("groupName", "%"+params.getGroupName()+"%");
		}
		if(params.getTemplateName() != null){
			dbq.setParameter("templateName", "%"+params.getTemplateName()+"%");
		}
		if(params.getUserName() != null){
			dbq.setParameter("userName", "%"+params.getUserName()+"%");
		}
		
		List<UserAccountView> list = dbq.getResultList();
		return list;
		
	}
	private StringBuilder searchLikeAttribute(StringBuilder sb, String objName, String attribute, String var) {
		if(dbInstance.getDbVendor().equals("mysql")) {
			sb.append(" ").append(objName).append(".").append(attribute).append(" like :").append(var);
		} else {
			sb.append(" lower(").append(objName).append(".").append(attribute).append(") like :").append(var);
			if(dbInstance.getDbVendor().equals("oracle")) {
	 	 		sb.append(" escape '\\'");
	 	 	}
		}
		return sb;
	}


}
