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
	public void markAccountAsClosed(List<UserAccountView> selectedItems) {
		for(UserAccountView view : selectedItems){
			markAccountByStatus(view.getIdentity().getKey(), view.getFeeCategory().getKey(), PaidStatus.MARK_AS_PAID);
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

	@Override
	public void markAccountAsOpen(List<UserAccountView> selectedItems) {
		for(UserAccountView view : selectedItems){
			markAccountByStatus(view.getIdentity().getKey(), view.getFeeCategory().getKey(),PaidStatus.NOT_DEFINE);
		}
	}

	public void markAccountByStatus(Long identityId, Long categoryId, PaidStatus notDefine) {
		FeeIdentityMapping mapping = feeIdentityMappingDao.searchFeeMapping(identityId, categoryId);
		if(mapping != null){
			mapping.setPaid(notDefine.getId());
			feeIdentityMappingDao.merge(mapping);
		}
	}
}
