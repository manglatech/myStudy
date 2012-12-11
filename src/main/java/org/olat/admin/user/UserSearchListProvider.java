/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.admin.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.olat.basesecurity.BaseSecurity;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.control.generic.ajax.autocompletion.ListProvider;
import org.olat.core.gui.control.generic.ajax.autocompletion.ListReceiver;
import org.olat.core.gui.util.CSSHelper;
import org.olat.core.id.Identity;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.user.UserManager;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class UserSearchListProvider implements ListProvider {
	
	private final BaseSecurity securityManager;
	private final UserManager userManager;
	
	public UserSearchListProvider() {
		securityManager = CoreSpringFactory.getImpl(BaseSecurity.class);
		userManager = CoreSpringFactory.getImpl(UserManager.class);
	}
	
	
	@Override
	public void getResult(String searchValue, String instituteId, ListReceiver receiver) {
		Map<String, String> userProperties = new HashMap<String, String>();
		// We can only search in mandatory User-Properties due to problems
		// with hibernate query with join and not existing rows
		userProperties.put(UserConstants.FIRSTNAME, searchValue);
		userProperties.put(UserConstants.LASTNAME, searchValue);
		userProperties.put(UserConstants.EMAIL, searchValue);
		// Search in all fileds -> non intersection search
		List<Identity> res = searchUsers(searchValue, userProperties, instituteId, false);
		int maxEntries = 15;
		boolean hasMore = false;
		for (Iterator<Identity> it_res = res.iterator(); (hasMore=it_res.hasNext()) && maxEntries > 0;) {
			maxEntries--;
			Identity ident = it_res.next();
			User u = ident.getUser();
			String key = ident.getKey().toString();
			String displayKey = ident.getName();
			String displayText = userManager.getUserDisplayName(u);
			receiver.addEntry(key, displayKey, displayText, CSSHelper.CSS_CLASS_USER);
		}					
		if(hasMore){
			receiver.addEntry(".....",".....");
		}
	}
	
	protected List<Identity> searchUsers(String login, Map<String, String> userPropertiesSearch, String instituteId,  boolean userPropertiesAsIntersectionSearch) {
	  return securityManager.getVisibleIdentitiesByPowerSearch(
			(login.equals("") ? null : login),
			userPropertiesSearch, userPropertiesAsIntersectionSearch,	// in normal search fields are intersected
			null, null, null, null, null,instituteId);
	}
}
