package org.olat.finance.user.payment.manager;

import java.util.List;

import org.olat.finance.user.payment.model.UserPaymentInfo;

public interface UserPaymentService {

	void addPayment(Long templateId, Long identityId, Integer payment);
	List<UserPaymentInfo> findUserPayments(Long identityId);
	void deletePayments(List<Long> paymentsIds);

}
