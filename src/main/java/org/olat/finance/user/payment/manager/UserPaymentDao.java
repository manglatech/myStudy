package org.olat.finance.user.payment.manager;

import java.util.List;

import org.olat.core.commons.persistence.manager.GenericDao;
import org.olat.finance.user.payment.model.UserPaymentInfo;

public interface UserPaymentDao extends GenericDao<UserPaymentInfo, Long> {

	List<UserPaymentInfo> findUserPayments(Long identityId);

}
