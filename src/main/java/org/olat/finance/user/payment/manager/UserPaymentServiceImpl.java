package org.olat.finance.user.payment.manager;

import java.util.List;

import org.olat.finance.fee.manager.FeeIdentityMappingDao;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.payment.model.UserPaymentInfoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userPaymentService")
public class UserPaymentServiceImpl implements UserPaymentService {

	@Autowired
	UserPaymentDao paymentDao;
	
	@Autowired
	FeeIdentityMappingDao feeIdentityMappingDao;
	
	@Override
	public void addPayment(Long categoryId, Long identityId, Integer paid) {
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(identityId, categoryId);
		if(mapping != null){
			UserPaymentInfo payment = new UserPaymentInfoImpl();
			payment.setFeeIdentityMapping(mapping);
			payment.setPaidAmount(paid);
			paymentDao.create(payment);
		}
	}

	@Override
	public List<UserPaymentInfo> findUserPayments(Long identityId) {
		return paymentDao.findUserPayments(identityId);
	}

	@Override
	public void deletePayments(List<Long> paymentsIds) {
		// TODO Auto-generated method stub
	}
}
