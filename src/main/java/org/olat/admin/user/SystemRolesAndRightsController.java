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

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityManager;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.basesecurity.Constants;
import org.olat.basesecurity.SecurityGroup;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.translator.PackageTranslator;
import org.olat.core.id.Identity;
import org.olat.core.util.Util;

/**
 * Initial Date:  Jan 27, 2006
 * @author gnaegi
 * <pre>
 * Description:
 * Controller that is used to manipulate the users system roles and rights. When calling
 * this controller make sure the user who calls the controller meets the following 
 * criterias:
 * - user is system administrator
 * or
 * - user tries not to modify a system administrator or user administrator
 * - user tries not to modify an author if author rights are not enabled for user managers
 * - user tries not to modify a group manager if group manager rights are not enabled for user managers 
 * - user tries not to modify a guest if guest rights are not enabled for user managers 
 * 
 * Usually this controller is called by the UserAdminController that takes care of all this. 
 * There should be no need to use it anywhere else.
 */
public class SystemRolesAndRightsController extends BasicController {
	private static final String PACKAGE = Util.getPackageName(SystemRolesAndRightsController.class);
	private static final String VELOCITY_ROOT = Util.getPackageVelocityRoot(PACKAGE);
	
	private VelocityContainer main;
	private PackageTranslator translator;
	private SystemRolesAndRightsForm sysRightsForm;
	private Identity identity;
	
	/**
	 * Constructor for a controller that lets you edit the users system roles and rights.
	 * @param wControl
	 * @param ureq
	 * @param identity identity to be edited
	 */
	public SystemRolesAndRightsController(WindowControl wControl, UserRequest ureq, Identity identity){
		super(ureq, wControl);		
		translator = new PackageTranslator(PACKAGE, ureq.getLocale());
		main = new VelocityContainer("sysRolesVC", VELOCITY_ROOT + "/usysRoles.html", translator, null);
		this.identity = identity;
		putInitialPanel(main);
		createForm(ureq, identity);
		main.put("sysRightsForm", sysRightsForm.getInitialComponent());		
	}
	
	/**
	 * Initialize a new SystemRolesAndRightsForm for the given identity using the
	 * security manager
	 * @param ureq
	 * @param identity
	 * @return SystemRolesAndRightsForm
	 */
	private void createForm(UserRequest ureq, Identity identity) {
		removeAsListenerAndDispose(sysRightsForm);
		sysRightsForm = new SystemRolesAndRightsForm(ureq, getWindowControl(), identity);
		listenTo (sysRightsForm);
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest, org.olat.core.gui.components.Component, org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}
	
	public void event(UserRequest ureq, Controller source, Event event) {
		
		if (source == sysRightsForm) {
			if (event == Event.DONE_EVENT) {
				saveFormData(ureq, identity, sysRightsForm);
			}
			createForm(ureq, identity);
			main.put("sysRightsForm", sysRightsForm.getInitialComponent());
		}
	}
	/**
	 * Persist form data in database. User needs to logout / login to activate changes. A bit tricky here
	 * is that only form elements should be gettet that the user is allowed to manipulate. See also the 
	 * comments in SystemRolesAndRightsForm. 
	 * @param myIdentity
	 * @param form
	 */
	private void saveFormData(UserRequest ureq, Identity myIdentity, SystemRolesAndRightsForm form) {
		boolean iAmOlatAdmin = ureq.getUserSession().getRoles().isOLATAdmin();
		boolean iAmUserManager = ureq.getUserSession().getRoles().isUserManager();
		BaseSecurity secMgr = BaseSecurityManager.getInstance();
		// 1) general user type - anonymous or user
		// anonymous users
		boolean isAnonymous = false;
		Boolean canGuestsByConfig = BaseSecurityModule.USERMANAGER_CAN_MANAGE_GUESTS;	
		if (canGuestsByConfig.booleanValue() || iAmOlatAdmin) {
			SecurityGroup anonymousGroup = secMgr.findSecurityGroupByName(Constants.GROUP_ANONYMOUS);
			boolean hasBeenAnonymous = secMgr.isIdentityInSecurityGroup(myIdentity, anonymousGroup);
			isAnonymous = form.isAnonymous();
			updateSecurityGroup(myIdentity, secMgr, anonymousGroup, hasBeenAnonymous, isAnonymous);
			// system users - oposite of anonymous users
			SecurityGroup usersGroup = secMgr.findSecurityGroupByName(Constants.GROUP_OLATUSERS);
			boolean hasBeenUser = secMgr.isIdentityInSecurityGroup(myIdentity, usersGroup);
			boolean isUser = !form.isAnonymous();
			updateSecurityGroup(myIdentity, secMgr, usersGroup, hasBeenUser, isUser);
		}
		// 2) system roles
		// group manager
		Boolean canGroupmanagerByConfig =BaseSecurityModule.USERMANAGER_CAN_MANAGE_GROUPMANAGERS;	
		if (canGroupmanagerByConfig.booleanValue() || iAmOlatAdmin) {
			SecurityGroup groupManagerGroup = secMgr.findSecurityGroupByName(Constants.GROUP_GROUPMANAGERS);
			boolean hasBeenGroupManager = secMgr.isIdentityInSecurityGroup(myIdentity, groupManagerGroup);
			boolean isGroupManager = form.isGroupmanager();
			updateSecurityGroup(myIdentity, secMgr, groupManagerGroup, hasBeenGroupManager, isGroupManager);
		}
		// author
		Boolean canAuthorByConfig = BaseSecurityModule.USERMANAGER_CAN_MANAGE_AUTHORS;	
		if (canAuthorByConfig.booleanValue() || iAmOlatAdmin) {
			SecurityGroup authorGroup = secMgr.findSecurityGroupByName(Constants.GROUP_AUTHORS);
			boolean hasBeenAuthor = secMgr.isIdentityInSecurityGroup(myIdentity, authorGroup);
			boolean isAuthor = form.isAuthor() || form.isInstitutionalResourceManager();
			updateSecurityGroup(myIdentity, secMgr, authorGroup, hasBeenAuthor, isAuthor);
		}
		// user manager, allowed by admin and User Managers
		Boolean canUserManagerByConfig = BaseSecurityModule.USERMANAGER_CAN_MANAGE_USER_MANAGER;	
		if (canUserManagerByConfig || iAmOlatAdmin) {
			SecurityGroup userManagerGroup = secMgr.findSecurityGroupByName(Constants.GROUP_USERMANAGERS);
			boolean hasBeenUserManager = secMgr.isIdentityInSecurityGroup(myIdentity, userManagerGroup);
			boolean isUserManager = form.isUsermanager();
			updateSecurityGroup(myIdentity, secMgr, userManagerGroup, hasBeenUserManager, isUserManager);
		}
	 	// institutional resource manager, only allowed by admin
		if (iAmUserManager || iAmOlatAdmin) {
			SecurityGroup institutionalResourceManagerGroup = secMgr.findSecurityGroupByName(Constants.GROUP_INST_ORES_MANAGER);
			boolean hasBeenInstitutionalResourceManager = secMgr.isIdentityInSecurityGroup(myIdentity, institutionalResourceManagerGroup);
			boolean isInstitutionalResourceManager = form.isInstitutionalResourceManager();
			updateSecurityGroup(myIdentity, secMgr, institutionalResourceManagerGroup, hasBeenInstitutionalResourceManager, isInstitutionalResourceManager);
		}
		// system administrator, only allowed by admin
		if (iAmOlatAdmin) {
			SecurityGroup adminGroup = secMgr.findSecurityGroupByName(Constants.GROUP_ADMIN);
			boolean hasBeenAdmin = secMgr.isIdentityInSecurityGroup(myIdentity, adminGroup);
			boolean isAdmin = form.isAdmin();
			updateSecurityGroup(myIdentity, secMgr, adminGroup, hasBeenAdmin, isAdmin);		
		}
		if (iAmOlatAdmin &&  !myIdentity.getStatus().equals(form.getStatus()) ) {
			secMgr.saveIdentityStatus(myIdentity, form.getStatus());
			identity.setStatus(form.getStatus());
		}
	}

	/**
	 * Update the security group in the database
	 * @param myIdentity
	 * @param secMgr
	 * @param securityGroup
	 * @param hasBeenInGroup
	 * @param isNowInGroup
	 */
	private void updateSecurityGroup(Identity myIdentity, BaseSecurity secMgr, SecurityGroup securityGroup, boolean hasBeenInGroup, boolean isNowInGroup) {
		if (!hasBeenInGroup && isNowInGroup) {
			// user not yet in security group, add him
			secMgr.addIdentityToSecurityGroup(myIdentity, securityGroup);
		} else if (hasBeenInGroup && !isNowInGroup) {
			// user not anymore in security group, remove him
			secMgr.removeIdentityFromSecurityGroup(myIdentity, securityGroup);
		}
	}
	
	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	protected void doDispose() {
		// nothing to do
	}

}
