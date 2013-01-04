package org.olat.finance.user.payment.manager;

import java.util.List;

import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.user.payment.model.UserPaymentInfo;

public interface UserPaymentService {

	void addPayment(Long templateId, Long identityId, Integer payment);
	List<UserPaymentInfo> findUserPayments(Long identityId);
	void deletePayments(List<Long> paymentsIds);
	void transferPayments(List<UserPaymentInfo> info,Identity identity,
			FeeCategory feeCategory);

}
