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
package org.olat.user;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.olat.basesecurity.IdentityImpl;
import org.olat.basesecurity.IdentityShort;
import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.commons.persistence.DBQuery;
import org.olat.core.helpers.Settings;
import org.olat.core.id.Identity;
import org.olat.core.id.Preferences;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.core.logging.AssertException;
import org.olat.core.util.WebappHelper;
import org.olat.core.util.i18n.I18nModule;
import org.olat.core.util.mail.MailHelper;
import org.olat.institute.model.InstituteImpl;
import org.olat.properties.Property;
import org.olat.properties.PropertyManager;
import org.olat.user.propertyhandlers.UserPropertyHandler;

/**
 * <h3>Description:</h3>
 * This implementation of the user manager manipulates user objects based on a
 * hibernate implementation
 * <p>
 * Initial Date: 31.07.2007 <br>
 * 
 * @author Florian Gnaegi, frentix GmbH, http://www.frentix.com
 */
public class UserManagerImpl extends UserManager {
  // used to save user data in the properties table 
  private static final String CHARSET = "charset";
  private UserDisplayNameCreator userDisplayNameCreator;
  
	/**
	 * Use UserManager.getInstance(), this is a spring factory method to load the
	 * correct user manager
	 */
	private UserManagerImpl() {
		INSTANCE = this;
	}

	/**
	 * @see org.olat.user.UserManager#createUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public User createUser(String firstName, String lastName, String eMail) {
		User newUser = new UserImpl(firstName, lastName, eMail);
		Preferences prefs = newUser.getPreferences();
		
		Locale loc;
		// for junit test case: use German Locale
		if (Settings.isJUnitTest()) { 
			loc = Locale.GERMAN;
		} else {
			loc = I18nModule.getDefaultLocale();
		}
		//Locale loc
		prefs.setLanguage(loc.toString());
		prefs.setFontsize("normal");
		prefs.setPresenceMessagesPublic(false);
		prefs.setInformSessionTimeout(false);
		return newUser;
	}

	/**
	 * @see org.olat.user.UserManager#createAndPersistUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public User createAndPersistUser(String firstName, String lastName, String email) {
		User user = new UserImpl(firstName, lastName, email);
		DBFactory.getInstance().saveObject(user);
		return user;
	}
	
	// fxdiff: check also for emails in change-workflow
	public boolean isEmailInUse(String email) {
		DB db = DBFactory.getInstance();
		String[] emailProperties = {UserConstants.EMAIL, UserConstants.INSTITUTIONALEMAIL};
		for(String emailProperty:emailProperties) {
			StringBuilder sb = new StringBuilder();
			sb.append("select count(user) from org.olat.core.id.User user where ")
				.append("user.properties['")
				.append(emailProperty)
				.append("']=:email_value");
			
			String query = sb.toString();
			DBQuery dbq = db.createQuery(query);
			dbq.setString("email_value", email);
			Number countEmail = (Number)dbq.uniqueResult();
			if(countEmail.intValue() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.olat.user.UserManager#findIdentityByEmail(java.lang.String)
	 */
	public Identity findIdentityByEmail(String email) {
		if (!MailHelper.isValidEmailAddress(email)) {
			throw new AssertException("Identity cannot be searched by email, if email is not valid. Used address: " + email);
		}
		
		DB db = DBFactory.getInstance();
		StringBuilder sb = new StringBuilder("select identity from ").append(IdentityImpl.class.getName()).append(" identity ")
			.append(" inner join identity.user user ")
			.append(" where ");
		
		//search email
		StringBuilder emailSb = new StringBuilder(sb);
		emailSb.append(" user.properties['").append(UserConstants.EMAIL).append("'] =:email");
		DBQuery emailQuery = db.createQuery(emailSb.toString());
		emailQuery.setString("email", email);
		List<Identity> identities = emailQuery.list();
		if (identities.size() > 1) {
			throw new AssertException("more than one identity found with email::" + email);
		}

		//search institutional email
		StringBuilder institutionalSb = new StringBuilder(sb);
		institutionalSb.append(" user.properties['").append(UserConstants.INSTITUTIONALEMAIL).append("'] =:email");
		DBQuery institutionalQuery = db.createQuery(institutionalSb.toString());
		institutionalQuery.setString("email", email);
		List<Identity> instIdentities = institutionalQuery.list();
		if (instIdentities.size() > 1) {
			throw new AssertException("more than one identity found with institutional-email::" + email);
		}

		// check if email found in both fields && identity is not the same
		if ( (identities.size() > 0) && (instIdentities.size() > 0) && 
				 ( identities.get(0) != instIdentities.get(0) ) ) {
			throw new AssertException("found two identites with same email::" + email + " identity1=" + identities.get(0) + " identity2=" + instIdentities.get(0));
		}
		if (identities.size() == 1) {
			return identities.get(0);
		}
		if (instIdentities.size() == 1) {
			return instIdentities.get(0);
		}
		return null;
	}
	
	@Override
	public List<Identity> findIdentitiesByEmail(List<String> emails) {
		for(Iterator<String> emailIt=emails.iterator(); emailIt.hasNext(); ) {
			String email = emailIt.next();
			if (!MailHelper.isValidEmailAddress(email)) {
				emailIt.remove();
				logWarn("Invalid email address: " + email, null);
			}
		}
		
		if(emails.isEmpty()) {
			return Collections.emptyList();
		}

		DB db = DBFactory.getInstance();
		StringBuilder sb = new StringBuilder("select identity from ").append(IdentityImpl.class.getName()).append(" identity ")
			.append(" inner join identity.user user ")
			.append(" where ");
		
		//search email
		StringBuilder emailSb = new StringBuilder(sb);
		emailSb.append(" user.properties['").append(UserConstants.EMAIL).append("']  in (:emails) ");
		DBQuery emailQuery = db.createQuery(emailSb.toString());
		emailQuery.setParameterList("emails", emails);
		List<Identity> identities = emailQuery.list();

		//search institutional email
		StringBuilder institutionalSb = new StringBuilder(sb);
		institutionalSb.append(" user.properties['").append(UserConstants.INSTITUTIONALEMAIL).append("'] in (:emails) ");
		if(!identities.isEmpty()) {
			institutionalSb.append(" and identity not in (:identities) ");
		}
		DBQuery institutionalQuery = db.createQuery(institutionalSb.toString());
		institutionalQuery.setParameterList("emails", emails);
		if(!identities.isEmpty()) {
			institutionalQuery.setParameterList("identities", identities);
		}
		List<Identity> instIdentities = institutionalQuery.list();
		identities.addAll(instIdentities);
		return identities;
	}

	/**
	 * @see org.olat.user.UserManager#findUserByEmail(java.lang.String)
	 */
	public User findUserByEmail(String email) {
		if (isLogDebugEnabled()){
			logDebug("Trying to find user with email '" + email + "'");
		}
		
		Identity ident = findIdentityByEmail(email);
		// if no user found return null
		if (ident == null) {
			if (isLogDebugEnabled()){
				logDebug("Could not find user '" + email + "'");
			}
			return null;
		} 
		return ident.getUser();
	}
	
	public boolean userExist(String email) {
		DB db = DBFactory.getInstance();
		StringBuilder sb = new StringBuilder("select distinct count(user) from ").append(UserImpl.class.getName()).append(" user where ");
		
		//search email
		StringBuilder emailSb = new StringBuilder(sb);
		emailSb.append(" user.properties['").append(UserConstants.EMAIL).append("'] =:email");
		DBQuery emailQuery = db.createQuery(emailSb.toString());
		emailQuery.setString("email", email);
		Number count = (Number)emailQuery.uniqueResult();
		if(count.intValue() > 0) {
			return true;
		}
		//search institutional email
		StringBuilder institutionalSb = new StringBuilder(sb);
		institutionalSb.append(" user.properties['").append(UserConstants.INSTITUTIONALEMAIL).append("'] =:email");
		DBQuery institutionalQuery = db.createQuery(institutionalSb.toString());
		institutionalQuery.setString("email", email);
		count = (Number)institutionalQuery.uniqueResult();
		if(count.intValue() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.olat.user.UserManager#loadUserByKey(java.lang.Long)
	 */
	public User loadUserByKey(Long key) {
		return (UserImpl) DBFactory.getInstance().loadObject(UserImpl.class, key);
		// User not loaded yet (lazy initialization). Need to access
		// a field first to really load user from database.
	}

	/**
	 * @see org.olat.user.UserManager#updateUser(org.olat.core.id.User)
	 */
	public void updateUser(User usr) {
		if (usr == null) throw new AssertException("User object is null!");
		DBFactory.getInstance().updateObject(usr);
		}

	/**
	 * @see org.olat.user.UserManager#saveUser(org.olat.core.id.User)
	 */
	public void saveUser(User user) {
		DBFactory.getInstance().saveObject(user);
	}

	/**
	 * @see org.olat.user.UserManager#updateUserFromIdentity(org.olat.core.id.Identity)
	 */
	public boolean updateUserFromIdentity(Identity identity) {
		this.updateUser(identity.getUser());
		return true;
	}

	/**
	 * @see org.olat.user.UserManager#setUserCharset(org.olat.core.id.Identity, java.lang.String)
	 */
	public void setUserCharset(Identity identity, String charset){
	    PropertyManager pm = PropertyManager.getInstance();
	    Property p = pm.findProperty(identity, null, null, null, CHARSET);
	    
	    if(p != null){
	        p.setStringValue(charset);
	        pm.updateProperty(p);
		} else {
	        Property newP = pm.createUserPropertyInstance(identity, null, CHARSET, null, null, charset, null);
	        pm.saveProperty(newP);
	    }
	}

	/**
	 * @see org.olat.user.UserManager#getUserCharset(org.olat.core.id.Identity)
	 */
	public String getUserCharset(Identity identity){
	   String charset;
	   charset = WebappHelper.getDefaultCharset();
	   PropertyManager pm = PropertyManager.getInstance();
	   Property p = pm.findProperty(identity, null, null, null, CHARSET);
	   if(p != null){
	       charset = p.getStringValue();
			// if after migration the system does not support the charset choosen by a
			// user
	       // (a rather rare case)
	       if(!Charset.isSupported(charset)){
	           charset = WebappHelper.getDefaultCharset();
	       }
		} else {
	       charset = WebappHelper.getDefaultCharset();
	   }
	   return charset;
	}

	/**
	 * Delete all user-properties which are deletable.
	 * @param user
	 */
	public void deleteUserProperties(User user) {
		// prevent stale objects, reload first
		user = loadUserByKey(user.getKey());
		// loop over user fields and remove them form the database if they are
		// deletable
		List<UserPropertyHandler> propertyHandlers = userPropertiesConfig.getAllUserPropertyHandlers();
		for (UserPropertyHandler propertyHandler : propertyHandlers) {
			String fieldName = propertyHandler.getName();
			if (propertyHandler.isDeletable()) {
				user.setProperty(fieldName, null);
			}		
		}
		// persist changes
		updateUser(user);
		if(isLogDebugEnabled()) logDebug("Delete all user-attributtes for user=" + user);
	}

	/**
	 * @see org.olat.user.UserManager#getUserDisplayName(org.olat.core.id.User)
	 */
	@Override
	public String getUserDisplayName(User user) {
		if (this.userDisplayNameCreator == null) return "";
		return this.userDisplayNameCreator.getUserDisplayName(user);
	}
	
	/**
	 * @see org.olat.user.UserManager#getUserDisplayName(org.olat.core.id.IdentityShort)
	 */
	@Override
	public String getUserDisplayName(IdentityShort user) {
		if (this.userDisplayNameCreator == null) return "";
		return this.userDisplayNameCreator.getUserDisplayName(user);
	}

	/**
	 * Sping setter method
	 * @param userDisplayNameCreator the userDisplayNameCreator to set
	 */
	public void setUserDisplayNameCreator(UserDisplayNameCreator userDisplayNameCreator) {
		this.userDisplayNameCreator = userDisplayNameCreator;
	}

	@Override
	public boolean isInstituteAllowToCreateUsers(String instituteId, int newUsersCount) {
		DB db = DBFactory.getInstance();
		
		StringBuilder sb = new StringBuilder("select distinct count(ident) from ").append(IdentityImpl.class.getName()).append(" ident ");
		sb.append(" where ");
		sb.append(" ident.instituteId=:instituteId ");
		sb.append(" and ident.status < :status ");
		
		DBQuery numberOfIdent = db.createQuery(sb.toString());
		numberOfIdent.setString("instituteId", instituteId);
		numberOfIdent.setInteger("status", Identity.STATUS_VISIBLE_LIMIT);
		
		Number identityCount = (Number) numberOfIdent.uniqueResult();
		
		
		StringBuilder sb1 = new StringBuilder("select inst.numberOfUser from ").append(InstituteImpl.class.getName()).append(" inst ");
		sb1.append(" where ");
		sb1.append(" inst.instituteId=:instituteId ");
		
		DBQuery allowedUsers = db.createQuery(sb1.toString());
		allowedUsers.setString("instituteId", instituteId);
		
		Number allowedIdentityCount = (Number) allowedUsers.uniqueResult();
		
		if(allowedIdentityCount != null){
			if(allowedIdentityCount.intValue() < (identityCount.intValue() + newUsersCount)){
				return false;
			}
		}
		
		return true;
	}

}
