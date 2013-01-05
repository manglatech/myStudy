package org.olat.finance.user.manager;

import java.util.List;

import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.ui.CreateUserAccountSearchParams;
import org.olat.finance.user.ui.UserAccountSearchParams;
import org.olat.finance.user.util.PaidStatus;

public interface UserAccountService {

	List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params);
	
	void assingFeeCategory(FeeCategory category, UserAccountView view);

	List<Identity> searchUserAccountSummary(
			CreateUserAccountSearchParams params);

	void markAccountAsClosed(List<UserAccountView> selectedItems);
	void markAccountAsOpen(List<UserAccountView> selectedItems);
	public void markAccountByStatus(Long identityId, Long categoryId, PaidStatus notDefine);

}
