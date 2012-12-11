package org.olat.finance.user.manager;

import java.util.List;

import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.ui.UserAccountSearchParams;

public interface UserAccountService {

	List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params);
	
	void markAccountAsPaidInFull(Long identityId, Long CategoryId);

	void markAccountAsPaidInFull(List<UserAccountView> selectedItems);

	void assingFeeCategory(FeeCategory category, UserAccountView view);

}
