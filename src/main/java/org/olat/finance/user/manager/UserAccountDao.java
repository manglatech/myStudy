package org.olat.finance.user.manager;

import java.util.List;

import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.ui.UserAccountSearchParams;


public interface UserAccountDao {

	List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params);

}
