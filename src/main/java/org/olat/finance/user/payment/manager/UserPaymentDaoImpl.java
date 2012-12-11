package org.olat.finance.user.payment.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.manager.GenericDaoHibernateImpl;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.payment.model.UserPaymentInfoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paymentDao")
public class UserPaymentDaoImpl extends GenericDaoHibernateImpl<UserPaymentInfo, Long>
		implements UserPaymentDao {

	public UserPaymentDaoImpl(Class<UserPaymentInfo> type) {
		super(type);
	}
	public UserPaymentDaoImpl(){
		super(UserPaymentInfo.class);
	}

	@Autowired
	private DB dbInstance;
	
	@Override
	public Class getType() {
		return UserPaymentInfoImpl.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return dbInstance.getCurrentEntityManager();
	}
	@Override
	public List<UserPaymentInfo> findUserPayments(Long identityId) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(UserPaymentInfoImpl.class.getName())
				.append(" f ");
		sb.append("where f.feeIdentityMapping.identity.key=:identityId ");

		TypedQuery<UserPaymentInfo> dbq = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), UserPaymentInfo.class);
		dbq.setParameter("identityId", identityId);
		dbq.setHint("org.hibernate.cacheable", Boolean.TRUE);

		List<UserPaymentInfo> list = dbq.getResultList();
		return list;
	}
}
