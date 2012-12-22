package org.olat.finance.user.manager;

import java.util.List;

import org.olat.core.id.Identity;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.ui.CreateUserAccountSearchParams;
import org.olat.finance.user.ui.UserAccountSearchParams;


public interface UserAccountDao {

	List<UserAccountView> searchUserAccountSummary(
			UserAccountSearchParams params);

	UserAccountView searchUserAccountSummary(Long identityId, Long categoryId);

	List<Identity> searchUserAccountSummary(
			CreateUserAccountSearchParams params);

}
