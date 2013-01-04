package org.olat.finance.user.payment.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.olat.core.id.Identity;
import org.olat.finance.fee.manager.FeeIdentityMappingDao;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.user.manager.UserAccountDao;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.payment.model.UserPaymentInfoImpl;
import org.olat.finance.user.util.PaidStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userPaymentService")
public class UserPaymentServiceImpl implements UserPaymentService {

	@Autowired
	UserPaymentDao paymentDao;

	@Autowired
	UserAccountDao accountDao;

	@Autowired
	FeeIdentityMappingDao feeIdentityMappingDao;

/*	@Override
	public void addPayment(Long categoryId, Long identityId, Integer paid) {
		PaidStatus status = null;
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(
				identityId, categoryId);

		UserPaymentInfo payment = new UserPaymentInfoImpl();
		if (mapping != null) {
			payment.setFeeIdentityMapping(mapping);
			payment.setPaidAmount(paid);
			status = identifyPaidStatus(categoryId, identityId, paid);
		}
		paymentDao.create(payment);
		if (status != null) {
			mapping.setPaid(status.getId());
			feeIdentityMappingDao.merge(mapping);
		}
	}
	private PaidStatus identifyPaidStatus(Long categoryId, Long identityId,
			Integer paid) {
		
		PaidStatus status = null;
		UserAccountView view = accountDao.searchUserAccountSummary(
				identityId, categoryId);
		if (view != null) {
			Long paidAmount = view.getPaidAmount();
			Long totalAmount = view.getTotalAmount();
			if (paidAmount != null && totalAmount == paidAmount + paid) {
				status = PaidStatus.PAID;
			} else if (paidAmount != null
					&& totalAmount < paidAmount + paid) {
				status = PaidStatus.OVER_PAID;
			} else if((paidAmount + paid) == 0 ) {
				status = PaidStatus.NOT_PAID;
			}else{
				status = PaidStatus.PARTIAL_PAID;
			}
		}
		return status;
	}*/
	
	@Override
	public void addPayment(Long categoryId, Long identityId, Integer paid) {
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(
				identityId, categoryId);
		
		UserPaymentInfo payment = new UserPaymentInfoImpl();
		if (mapping != null) {
			payment.setFeeIdentityMapping(mapping);
			payment.setPaidAmount(paid);
		}
		
		paymentDao.create(payment);
		
	}
	
	@Override
	public List<UserPaymentInfo> findUserPayments(Long identityId) {
		return paymentDao.findUserPayments(identityId);
	}

	@Override
	public void deletePayments(List<Long> paymentsIds) {
		List<UserPaymentInfo> entities = paymentDao.load(paymentsIds);
		List<FeeIdentityMapping> mappings = new ArrayList<FeeIdentityMapping>();
		
		for (UserPaymentInfo info : entities) {
			mappings.add(info.getFeeIdentityMapping());
			paymentDao.remove(info);
		}
		/*for (FeeIdentityMapping mapping : mappings) {
			PaidStatus status = identifyPaidStatus(mapping.getFeeCategory().getKey(), mapping.getIdentity().getKey(), 0);
			if(status == null){
				status = PaidStatus.NOT_PAID;
			}
			FeeIdentityMapping m = feeIdentityMappingDao.load(mapping.getKey());
			m.setPaid(status.getId());
			feeIdentityMappingDao.merge(m);
		}*/
	}

	@Override
	public void transferPayments(List<UserPaymentInfo> userPayments,Identity identity,
			FeeCategory feeCategory) {
		
		List<Long> paymentIds = new ArrayList<Long>();
		List<Integer> priceList = new ArrayList<Integer>();
		
		for (UserPaymentInfo payment : userPayments) {
			priceList.add(payment.getPaidAmount());
			paymentIds.add(payment.getKey());
		}
		deletePayments(paymentIds);
		for(Integer price: priceList){
			addPayment(feeCategory.getKey(), identity.getKey(), price);
		}
	}
}
