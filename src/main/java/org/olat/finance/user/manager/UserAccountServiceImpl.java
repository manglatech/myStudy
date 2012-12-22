package org.olat.finance.user.manager;

import java.util.List;

import org.olat.basesecurity.BaseSecurityManager;
import org.olat.core.id.Identity;
import org.olat.finance.fee.manager.FeeIdentityMappingDao;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.ui.CreateUserAccountSearchParams;
import org.olat.finance.user.ui.UserAccountSearchParams;
import org.olat.finance.user.util.PaidStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userAccountService")
public class UserAccountServiceImpl implements UserAccountService{

	@Autowired
	UserAccountDao userAccountDao;
	
	@Autowired
	FeeIdentityMappingDao feeIdentityMappingDao;
	
	@Autowired
	FeeService feeService;
	
	@Override
	public List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params) {
		return userAccountDao.searchUserAccountSummary(params);
	}

	@Override
	public void markAccountAsPaidInFull(Long identityId, Long categoryId) {
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(identityId, categoryId);
		if(mapping != null){
			mapping.setPaid(PaidStatus.MARK_AS_PAID.getId());
			feeIdentityMappingDao.merge(mapping);
		}
	}

	@Override
	public void markAccountAsPaidInFull(List<UserAccountView> selectedItems) {
		for(UserAccountView view : selectedItems){
			markAccountAsPaidInFull(view.getIdentity().getKey(), view.getFeeCategory().getKey());
		}
	}
	@Override
	public void assingFeeCategory(FeeCategory category, UserAccountView view) {
		Identity identity = BaseSecurityManager.getInstance().loadIdentityByKey(view.getIdentity().getKey());
		feeService.addIdentitiesToFeeCategory(identity, category);
	}
	@Override
	public List<Identity> searchUserAccountSummary(
			CreateUserAccountSearchParams params) {
		List<Identity> identities = userAccountDao.searchUserAccountSummary(params);
		return identities;
	}
}
