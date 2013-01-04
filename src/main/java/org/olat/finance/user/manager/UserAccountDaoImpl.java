package org.olat.finance.user.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.olat.basesecurity.IdentityImpl;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeIdentityMappingImpl;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.model.UserAccountViewImpl;
import org.olat.finance.user.ui.CreateUserAccountSearchParams;
import org.olat.finance.user.ui.UserAccountSearchParams;
import org.olat.finance.user.util.AccountStatus;
import org.olat.finance.user.util.PaidStatus;
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
		
		if(params.getTemplateName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "uav", "feeCategory.name", "templateName");
		}
		if(params.getUserName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "uav", "identity.name", "userName");
		}
		if(params.getDueDate() != null){
			query.append(" and ");
			query.append(" uav.dueDate<:dueDate ");
		}
		
		if(params.getPaidStatus() != null){
			switch (params.getPaidStatus()) {
				case NOT_PAID:
					query.append(" and ");
					query.append(" (user_paid_amount is null or user_paid_amount = 0) ");
					query.append("and paid_status = 0");
					break;
				case PARTIAL_PAID:
					query.append(" and ");
					query.append(" (total_amount > user_paid_amount) ");
					query.append(" and paid_status = 0 ");
					break;
				case PAID:
					query.append(" and ");
					query.append(" (total_amount is not null  and user_paid_amount  = total_amount) ");
					query.append(" and paid_status = 0 ");
					break;
				case MARK_AS_PAID:
					query.append(" and ");
					query.append(" paid_status = 5 ");
					break;
				default:
					break;
			}
		}
		
		TypedQuery<UserAccountView> dbq = dbInstance.getCurrentEntityManager().createQuery(
				query.toString(), UserAccountView.class);
		
		dbq.setParameter("instituteId", params.getInstituteId());
		if(params.getTemplateName() != null){
			dbq.setParameter("templateName", "%"+params.getTemplateName()+"%");
		}
		if(params.getUserName() != null){
			dbq.setParameter("userName", "%"+params.getUserName()+"%");
		}
		if(params.getDueDate() != null){
			dbq.setParameter("dueDate", params.getDueDate());
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

	@Override
	public UserAccountView searchUserAccountSummary(Long identityId,
			Long categoryId) {
		
		StringBuilder query = new StringBuilder();
		query.append("select uav from ")
		.append(UserAccountViewImpl.class.getName()).append(" uav ");
		query.append(" where uav.identity.key=:identityId ");
		query.append(" and uav.feeCategory.key=:categoryId ");

		TypedQuery<UserAccountView> dbq = dbInstance.getCurrentEntityManager().createQuery(
				query.toString(), UserAccountView.class);
		
		dbq.setParameter("identityId", identityId);
		dbq.setParameter("categoryId", categoryId);
		
		List<UserAccountView> list = dbq.getResultList();
		return list.iterator().next();
	}

	@Override
	public List<Identity> searchUserAccountSummary(
			CreateUserAccountSearchParams params) {
		
		StringBuilder query = new StringBuilder();
		query.append(" select distinct obi.* FROM o_bs_identity obi ");
		query.append(" left join o_bs_membership obm on obi.id = obm.identity_id ");
		query.append(" left join o_gp_business og on obm.secgroup_id = og.fk_partipiciantgroup ");
		query.append(" where obi.fk_institute_id=:instituteId and obi.status < "+Identity.STATUS_VISIBLE_LIMIT);
		
		if(params.getUserName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "obi", "name", "username");
		}
		if(params.getGroupName() != null){
			query.append(" and ");
			searchLikeAttribute(query, "og", "groupname", "groupname");
		}
		if(params.getAccountStatus() != null){
			if(AccountStatus.UNASSIGNED == params.getAccountStatus()){
				query.append(" and ");
				query.append(" obi.id not in ( select distinct fim.fk_identity_id from ");
				query.append(" o_fee_identity_mapping fim ) ");
			}else if(AccountStatus.ASSIGNED == params.getAccountStatus()){
				query.append(" and ");
				query.append(" obi.id in ( select distinct fim.fk_identity_id from ");
				query.append(" o_fee_identity_mapping fim ) ");
			}
		}
		
		
		Query q = dbInstance.getCurrentEntityManager().createNativeQuery(query.toString(), IdentityImpl.class);
		q.setParameter("instituteId", params.getInstituteId());
		if(params.getUserName() != null){
			q.setParameter("username", "%"+params.getUserName()+"%");
		}
		if(params.getGroupName() != null){
			q.setParameter("groupname", "%"+params.getGroupName()+"%");
		}
		List<Identity> identites = q.getResultList();
		return identites;
		
	}
}
