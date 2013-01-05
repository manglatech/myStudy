/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/

package org.olat.admin.user;

import java.util.List;
import java.util.Locale;

import org.olat.admin.policy.PolicyController;
import org.olat.admin.user.groups.GroupOverviewController;
import org.olat.basesecurity.Authentication;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityManager;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.basesecurity.Constants;
import org.olat.basesecurity.SecurityGroup;
import org.olat.core.commons.modules.bc.FolderConfig;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.panel.Panel;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.Identity;
import org.olat.core.id.UserConstants;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.logging.OLATSecurityException;
import org.olat.core.util.WebappHelper;
import org.olat.core.util.resource.OresHelper;
import org.olat.core.util.vfs.QuotaManager;
import org.olat.notifications.NotificationUIFactory;
import org.olat.properties.Property;
import org.olat.user.ChangePrefsController;
import org.olat.user.DisplayPortraitController;
import org.olat.user.ProfileAndHomePageEditController;
import org.olat.user.PropFoundEvent;
import org.olat.user.UserPropertiesController;

/**
 *  Initial Date:  Jul 29, 2003
 *  @author Sabina Jeger
 *  <pre>
 *  Complete rebuild on 17. jan 2006 by Florian Gnaegi
 *  
 *  Functionality to change or view all kind of things for this user 
 *  based on the configuration for the user manager. 
 *  This controller should only be used by the UserAdminMainController.
 *  
 * </pre>
 */
public class UserAdminController extends BasicController implements Activateable2 {

		
	// NLS support
	
	private static final String NLS_ERROR_NOACCESS_TO_USER = "error.noaccess.to.user";
	private static final String NLS_FOUND_PROPERTY	= "found.property";
	private static final String NLS_EDIT_UPROFILE		= "edit.uprofile";
	private static final String NLS_EDIT_UPREFS			= "edit.uprefs";
	private static final String NLS_EDIT_UPWD 			= "edit.upwd";
	private static final String NLS_EDIT_UAUTH 			= "edit.uauth";
	private static final String NLS_EDIT_UPROP			= "edit.uprop";
	private static final String NLS_EDIT_UPOLICIES	= "edit.upolicies";
	private static final String NLS_EDIT_UROLES			= "edit.uroles";
	private static final String NLS_EDIT_UQUOTA			= "edit.uquota";
	private static final String NLS_VIEW_GROUPS 		= "view.groups";
	private static final String NLS_VIEW_SUBSCRIPTIONS 		= "view.subscriptions";
	
	private VelocityContainer myContent;
		
	private Identity myIdentity = null;

	// controllers used in tabbed pane
	private TabbedPane userTabP;
	private Controller prefsCtr, propertiesCtr, pwdCtr, quotaCtr, policiesCtr, rolesCtr, userShortDescrCtr;
	private DisplayPortraitController portraitCtr;
	private UserAuthenticationsEditorController authenticationsCtr;
	private Link backLink;
	private ProfileAndHomePageEditController userProfileCtr;
	private GroupOverviewController grpCtr;
	

	/**
	 * Constructor that creates a back - link as default
	 * @param ureq
	 * @param wControl
	 * @param identity
	 */
	public UserAdminController(UserRequest ureq, WindowControl wControl, Identity identity) {
		super(ureq, wControl);
		BaseSecurity mgr = BaseSecurityManager.getInstance();
		if (!mgr.isIdentityPermittedOnResourceable(
				ureq.getIdentity(), 
				Constants.PERMISSION_ACCESS, 
				OresHelper.lookupType(this.getClass())))
			throw new OLATSecurityException("Insufficient permissions to access UserAdminController");
		
		myIdentity = identity;
				
		if (allowedToManageUser(ureq, myIdentity)) {			
			myContent = this.createVelocityContainer("udispatcher");
			backLink = LinkFactory.createLinkBack(myContent, this);
			userShortDescrCtr = new UserShortDescription(ureq, wControl, identity);
			myContent.put("userShortDescription", userShortDescrCtr.getInitialComponent());
			
			setBackButtonEnabled(true); // default
			initTabbedPane(myIdentity, ureq);
			exposeUserDataToVC(ureq, myIdentity);					
			this.putInitialPanel(myContent);
		} else {
			String supportAddr = WebappHelper.getMailConfig("mailSupport");			
			this.showWarning(NLS_ERROR_NOACCESS_TO_USER, supportAddr);			
			this.putInitialPanel(new Panel("empty"));
		}
	}
	
	@Override
	//fxdiff BAKS-7 Resume function
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(entries == null || entries.isEmpty()) return;
		
		String entryPoint = entries.get(0).getOLATResourceable().getResourceableTypeName();
		if("tab".equals(entryPoint)) {
			userTabP.activate(ureq, entries, state);
		} else if (userTabP != null) {
				userTabP.setSelectedPane(translate(entryPoint));
		}
	}

	/**
	 * @param backButtonEnabled
	 */
	public void setBackButtonEnabled(boolean backButtonEnabled) {
		if (myContent != null)
			myContent.contextPut("showButton", Boolean.valueOf(backButtonEnabled));
	}
	
	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest, org.olat.core.gui.components.Component, org.olat.core.gui.control.Event)
	 */
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == backLink){
			fireEvent(ureq, Event.BACK_EVENT);
		//fxdiff BAKS-7 Resume function
		} else if (source == userTabP) {
			userTabP.addToHistory(ureq, getWindowControl());
		}
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest, org.olat.core.gui.control.Controller, org.olat.core.gui.control.Event)
	 */
	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == propertiesCtr) {
			if (event.getCommand().equals("PropFound")){
				PropFoundEvent foundEvent = (PropFoundEvent) event;
				Property myfoundProperty = foundEvent.getProperty();				
				this.showInfo(NLS_FOUND_PROPERTY,myfoundProperty.getKey().toString());								
			}
		} else if (source == pwdCtr) {
			if (event == Event.DONE_EVENT) {
				// rebuild authentication tab, could be wrong now
				if (authenticationsCtr != null) authenticationsCtr.rebuildAuthenticationsTableDataModel();
			}
		} else if (source == userProfileCtr){
			if (event == Event.DONE_EVENT){
				//reload profile data on top
				myIdentity = (Identity) DBFactory.getInstance().loadObject(myIdentity);
				exposeUserDataToVC(ureq, myIdentity);
				userProfileCtr.resetForm(ureq, getWindowControl());
			}
		}
	}

	/**
	 * Check if user allowed to modify this identity. Only modification of user
	 * that have lower rights is allowed. No one exept admins can manage usermanager
	 * and admins
	 * @param ureq
	 * @param identity
	 * @return boolean
	 */
	private boolean allowedToManageUser(UserRequest ureq, Identity identity) {
		
		//fxdiff 	FXOLAT-184 prevent editing of users that are in frentix-superadmin group (except "frentix" wants to change own profile)
		Identity editor = ureq.getUserSession().getIdentity();
		SecurityGroup frentixSuperAdminGroup =  BaseSecurityManager.getInstance().findSecurityGroupByName("fxadmins");
		if(BaseSecurityManager.getInstance().isIdentityInSecurityGroup(identity, frentixSuperAdminGroup)){
			if(editor.equals(identity)) return true;
			return false;
		}
		
		boolean isOlatAdmin = ureq.getUserSession().getRoles().isOLATAdmin();
		if (isOlatAdmin) return true;

		BaseSecurity secmgr = BaseSecurityManager.getInstance();
		// only admins can administrate admin 
		boolean isAdmin = secmgr.isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_ADMIN);
		if (isAdmin) return false;
		
		// User Manager can administrate User Manager
		Boolean canManageUserManager = BaseSecurityModule.USERMANAGER_CAN_MANAGE_USER_MANAGER;
		boolean isUserManager = secmgr.isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_USERMANAGER);
		if (isUserManager && !canManageUserManager.booleanValue()) return false;
		
		// if user is author ony allowed to edit if configured
		boolean isAuthor = secmgr.isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_AUTHOR);
		Boolean canManageAuthor = BaseSecurityModule.USERMANAGER_CAN_MANAGE_AUTHORS;
		if (isAuthor && !canManageAuthor.booleanValue()) return false;
		// if user is groupmanager ony allowed to edit if configured
		boolean isGroupManager = secmgr.isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_GROUPMANAGER);
		Boolean canManageGroupmanager = BaseSecurityModule.USERMANAGER_CAN_MANAGE_GROUPMANAGERS;
		if (isGroupManager && !canManageGroupmanager.booleanValue()) return false;
		// if user is guest ony allowed to edit if configured
		boolean isGuestOnly = secmgr.isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_GUESTONLY);
		Boolean canManageGuest = BaseSecurityModule.USERMANAGER_CAN_MANAGE_GUESTS;
		if (isGuestOnly && !canManageGuest.booleanValue()) return false;
		// passed all tests, current user is allowed to edit given identity
		return true;
	}
	
	/**
	 * Initialize the tabbed pane according to the users rights and the system
	 * configuration
	 * @param identity
	 * @param ureq
	 */
	private void initTabbedPane(Identity identity, UserRequest ureq) {
		// first Initialize the user details tabbed pane
		boolean isOlatAdmin = ureq.getUserSession().getRoles().isOLATAdmin();
		userTabP = new TabbedPane("userTabP", ureq.getLocale());
		//fxdiff BAKS-7 Resume function
		userTabP.addListener(this);
		
		/**
		 *  Determine, whether the user admin is or is not able to edit all fields in user
		 *  profile form. The system admin is always able to do so.
		 */
		Boolean canEditAllFields = BaseSecurityModule.USERMANAGER_CAN_EDIT_ALL_PROFILE_FIELDS;
		if (BaseSecurityManager.getInstance().isIdentityPermittedOnResourceable(identity, Constants.PERMISSION_HASROLE, Constants.ORESOURCE_ADMIN)) {
			canEditAllFields = Boolean.TRUE;
		}

		userProfileCtr = new ProfileAndHomePageEditController(ureq, getWindowControl(), identity, canEditAllFields.booleanValue());
		listenTo(userProfileCtr);
		userTabP.addTab(translate(NLS_EDIT_UPROFILE), userProfileCtr.getInitialComponent());
		
		Boolean canPref = BaseSecurityModule.USERMANAGER_ACCESS_TO_PREF;
		if(canPref.booleanValue() || isOlatAdmin){
			prefsCtr = new ChangePrefsController(ureq, getWindowControl(), identity);
			userTabP.addTab(translate(NLS_EDIT_UPREFS), prefsCtr.getInitialComponent());
		}
		
		Boolean canChangePwd = BaseSecurityModule.USERMANAGER_CAN_MODIFY_PWD;
		if (canChangePwd.booleanValue() || isOlatAdmin) {
			// show pwd form only if user has also right to create new passwords in case
			// of a user that has no password yet
			Boolean canCreatePwd = BaseSecurityModule.USERMANAGER_CAN_CREATE_PWD;
			Authentication OLATAuth = BaseSecurityManager.getInstance().findAuthentication(identity,BaseSecurityModule.getDefaultAuthProviderIdentifier());
			if (OLATAuth != null || canCreatePwd.booleanValue() || isOlatAdmin) {
				pwdCtr =  new UserChangePasswordController(ureq, getWindowControl(), identity);				
				this.listenTo(pwdCtr); // listen when finished to update authentications model
				userTabP.addTab(translate(NLS_EDIT_UPWD), pwdCtr.getInitialComponent());
			}
		}
		
		Boolean canAuth = BaseSecurityModule.USERMANAGER_ACCESS_TO_AUTH;
		if (canAuth.booleanValue() || isOlatAdmin) {
			authenticationsCtr =  new UserAuthenticationsEditorController(ureq, getWindowControl(), identity);
			userTabP.addTab(translate(NLS_EDIT_UAUTH), authenticationsCtr.getInitialComponent());
		}
		
		Boolean canProp = BaseSecurityModule.USERMANAGER_ACCESS_TO_PROP;
		if (canProp.booleanValue() || isOlatAdmin) {
			propertiesCtr = new UserPropertiesController(ureq, getWindowControl(), identity);			
			this.listenTo(propertiesCtr);
			userTabP.addTab(translate(NLS_EDIT_UPROP), propertiesCtr.getInitialComponent());
		}
		
		Boolean canPolicies = BaseSecurityModule.USERMANAGER_ACCESS_TO_POLICIES;
		if (canPolicies.booleanValue() || isOlatAdmin) {
			policiesCtr = new PolicyController(ureq, getWindowControl(), identity);
			userTabP.addTab(translate(NLS_EDIT_UPOLICIES), policiesCtr.getInitialComponent());
		}
		
		Boolean canStartGroups = BaseSecurityModule.USERMANAGER_CAN_START_GROUPS;
		grpCtr = new GroupOverviewController(ureq, getWindowControl(), identity, canStartGroups);
		listenTo(grpCtr);
		userTabP.addTab(translate(NLS_VIEW_GROUPS), grpCtr.getInitialComponent());

		Boolean canSubscriptions = BaseSecurityModule.USERMANAGER_CAN_MODIFY_SUBSCRIPTIONS;
		if (canSubscriptions.booleanValue() || isOlatAdmin) {
			Controller subscriptionsCtr = NotificationUIFactory.createSubscriptionListingController(identity, ureq, getWindowControl());
			listenTo(subscriptionsCtr); // auto-dispose
			userTabP.addTab(translate(NLS_VIEW_SUBSCRIPTIONS), subscriptionsCtr.getInitialComponent());			
		}
		
		rolesCtr = new SystemRolesAndRightsController(getWindowControl(), ureq, identity);
		userTabP.addTab(translate(NLS_EDIT_UROLES), rolesCtr.getInitialComponent());
		
		Boolean canQuota = BaseSecurityModule.USERMANAGER_ACCESS_TO_QUOTA;
		if (canQuota.booleanValue() || isOlatAdmin) {
			String relPath = FolderConfig.getUserHomes() + "/" + identity.getName();
			quotaCtr = QuotaManager.getInstance().getQuotaEditorInstance(ureq, getWindowControl(), relPath, false);
			userTabP.addTab(translate(NLS_EDIT_UQUOTA), quotaCtr.getInitialComponent());
		}
		
		// now push to velocity
		myContent.put("userTabP", userTabP);
	}

	/**
	 * Add some user data to velocity container including the users portrait
	 * @param ureq
	 * @param identity
	 */
	private void exposeUserDataToVC(UserRequest ureq, Identity identity) {		
		Locale loc = ureq.getLocale();
		myContent.contextPut("foundUserName", identity.getName());
		myContent.contextPut("foundFirstName", identity.getUser().getProperty(UserConstants.FIRSTNAME, loc));
		myContent.contextPut("foundLastName", identity.getUser().getProperty(UserConstants.LASTNAME, loc));
		myContent.contextPut("foundEmail", identity.getUser().getProperty(UserConstants.EMAIL, loc));
		removeAsListenerAndDispose(portraitCtr);
		portraitCtr = new DisplayPortraitController(ureq, getWindowControl(), identity, true, true);
		myContent.put("portrait", portraitCtr.getInitialComponent());
		removeAsListenerAndDispose(userShortDescrCtr);
		userShortDescrCtr = new UserShortDescription(ureq, getWindowControl(), identity);
		myContent.put("userShortDescription", userShortDescrCtr.getInitialComponent());
	}
	
	/**
	 * 
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	@Override
	protected void doDispose() {
		//child controllers registered with listenTo get disposed in BasicController
		if (quotaCtr != null) {
			quotaCtr.dispose();
			quotaCtr = null;
		}
		if (authenticationsCtr != null) {
			authenticationsCtr.dispose();
			authenticationsCtr = null;
		}
		if (prefsCtr != null) {
			prefsCtr.dispose();
			prefsCtr = null;
		}			
		if (policiesCtr != null) {
			policiesCtr.dispose();
			policiesCtr = null;
		}
		if (rolesCtr != null) {
			rolesCtr.dispose();
			rolesCtr = null;
		}
		if (portraitCtr != null) {
			portraitCtr.dispose();
			portraitCtr = null;
		}
		if (userShortDescrCtr!=null) {
			userShortDescrCtr.dispose();
			userShortDescrCtr = null;
		}
	}


}