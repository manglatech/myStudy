package org.olat.finance.user.payment.manager;

import java.util.List;

import org.olat.finance.fee.manager.FeeIdentityMappingDao;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.user.manager.UserAccountDao;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.payment.model.UserPaymentInfoImpl;
import org.olat.finance.user.util.PaidStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.svcs.types.ap.PaymentInfo;

@Service("userPaymentService")
public class UserPaymentServiceImpl implements UserPaymentService {

	@Autowired
	UserPaymentDao paymentDao;
	
	@Autowired
	UserAccountDao accountDao;
	
	@Autowired
	FeeIdentityMappingDao feeIdentityMappingDao;
	
	@Override
	public void addPayment(Long categoryId, Long identityId, Integer paid) {
		PaidStatus status = null; 
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(identityId, categoryId);

		UserPaymentInfo payment = new UserPaymentInfoImpl();
		if(mapping != null){
			payment.setFeeIdentityMapping(mapping);
			payment.setPaidAmount(paid);
		
			UserAccountView view = accountDao.searchUserAccountSummary(identityId, categoryId);
			if(view != null){
				Long paidAmount = view.getPaidAmount();
				Long totalAmount = view.getTotalAmount();
				if(paidAmount != null && totalAmount == paidAmount + paid){
					status = PaidStatus.PAID;
				}else if(paidAmount != null && totalAmount < paidAmount + paid){
					status = PaidStatus.OVER_PAID;
				}else{
					status = PaidStatus.PARTIAL_PAID;
				}
			}
		}
		paymentDao.create(payment);
		if(status != null){
			mapping.setPaid(status.getId());
			feeIdentityMappingDao.merge(mapping);
		}
	}
	@Override
	public List<UserPaymentInfo> findUserPayments(Long identityId) {
		return paymentDao.findUserPayments(identityId);
	}
	@Override
	public void deletePayments(List<Long> paymentsIds) {
		List<UserPaymentInfo> entities = paymentDao.load(paymentsIds);
		for(UserPaymentInfo info : entities){
			paymentDao.remove(info);
		}
	}
}
