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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.admin.user.bulkChange.UserBulkChangeManager;
import org.olat.admin.user.bulkChange.UserBulkChangeStep00;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityManager;
import org.olat.basesecurity.PermissionOnResourceable;
import org.olat.basesecurity.SecurityGroup;
import org.olat.basesecurity.events.SingleIdentityChosenEvent;
import org.olat.core.commons.fullWebApp.popup.BaseFullWebappPopupLayoutFactory;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.commons.persistence.PersistenceHelper;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.DateChooser;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.panel.Panel;
import org.olat.core.gui.components.table.Table;
import org.olat.core.gui.components.table.TableController;
import org.olat.core.gui.components.table.TableEvent;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.components.table.TableMultiSelectEvent;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.creator.ControllerCreator;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.gui.control.generic.popup.PopupBrowserWindow;
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepRunnerCallback;
import org.olat.core.gui.control.generic.wizard.StepsMainRunController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.Roles;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.id.context.StateMapped;
import org.olat.core.logging.AssertException;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.servlets.WebDAVManager;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.core.util.mail.ContactList;
import org.olat.core.util.mail.ContactMessage;
import org.olat.core.util.resource.OresHelper;
import org.olat.login.LoginModule;
import org.olat.login.auth.AuthenticationProvider;
import org.olat.login.auth.WebDAVAuthManager;
import org.olat.modules.co.ContactFormController;
import org.olat.user.UserInfoMainController;
import org.olat.user.UserManager;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.olat.util.logging.activity.LoggingResourceable;

/**
 * Initial Date: Jan 31, 2006
 * 
 * @author gnaegi
 * 
 * Description: This workflow has two constructors. The first one provides the
 * user an advanced user search form with many search criterias that can be
 * defined. The second one has the criterias in the constructor as attributes,
 * so the search form won't appear. The following is a list with the search
 * results. Form the list an identity can be selected which results in a
 * SingleIdentityChosenEvent Alternatively a Canceled Event is fired.
 * 
 */
public class UsermanagerUserSearchController extends BasicController implements Activateable2 {

	private static final String CMD_MAIL = "exeMail";
	private static final String CMD_BULKEDIT = "bulkEditUsers";

	private VelocityContainer userListVC, userSearchVC, mailVC;
	private Panel panel;

	private UsermanagerUserSearchForm searchform;
	private TableController tableCtr;
	private List<Identity> identitiesList, selectedIdentities;
	private ArrayList<String> notUpdatedIdentities = new ArrayList<String>();
	private ExtendedIdentitiesTableDataModel tdm;
	private Identity foundIdentity = null;
	private ContactFormController contactCtr;
	private Link backFromMail;
	private Link backFromList;
	private boolean showEmailButton = true;
	private StepsMainRunController userBulkChangeStepsController;
	private boolean isAdministrativeUser = false;

	/**
	 * Constructor to trigger the user search workflow using a generic search form
	 * 
	 * @param ureq
	 * @param wControl
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);

		userSearchVC = createVelocityContainer("usermanagerUsersearch");

		mailVC = createVelocityContainer("usermanagerMail");
		backFromMail = LinkFactory.createLinkBack(mailVC, this);

		userListVC = createVelocityContainer("usermanagerUserlist");

		backFromList = LinkFactory.createLinkBack(userListVC, this);

		userListVC.contextPut("showBackButton", Boolean.TRUE);
		userListVC.contextPut("emptyList", Boolean.FALSE);
		userListVC.contextPut("showTitle", Boolean.TRUE);

		searchform = new UsermanagerUserSearchForm(ureq, wControl);
		listenTo(searchform);
		
		userSearchVC.put("usersearch", searchform.getInitialComponent());

		panel = putInitialPanel(userSearchVC);
	}

	/**
	 * Constructor to trigger the user search workflow using the given attributes.
	 * The user has no possibility to manually search, the search will be
	 * performed using the constructor attributes.
	 * 
	 * @param ureq
	 * @param wControl
	 * @param searchGroups
	 * @param searchPermissionOnResources
	 * @param searchAuthProviders
	 * @param searchCreatedAfter
	 * @param searchCreatedBefore
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl, SecurityGroup[] searchGroups,
			PermissionOnResourceable[] searchPermissionOnResources, String[] searchAuthProviders, Date searchCreatedAfter,
			Date searchCreatedBefore, Integer status, boolean showEmailButton) {
		super(ureq, wControl);

		mailVC = createVelocityContainer("usermanagerMail");

		backFromMail = LinkFactory.createLinkBack(mailVC, this);

		userListVC = createVelocityContainer("usermanagerUserlist");
		this.showEmailButton = showEmailButton;

		userListVC.contextPut("showBackButton", Boolean.FALSE);
		userListVC.contextPut("showTitle", Boolean.TRUE);

		identitiesList = BaseSecurityManager.getInstance().getIdentitiesByPowerSearch(null, null, true, searchGroups, searchPermissionOnResources, searchAuthProviders,
				searchCreatedAfter, searchCreatedBefore, null, null, status,getUserInst());

		initUserListCtr(ureq, identitiesList, status);
		userListVC.put("userlist", tableCtr.getInitialComponent());
		userListVC.contextPut("emptyList", (identitiesList.size() == 0 ? Boolean.TRUE : Boolean.FALSE));

		panel = putInitialPanel(userListVC);
	}
	
	/**
	 * Constructor to trigger the user search workflow using the predefined list of
	 * identities. The user has no possibility to manually search.
	 * 
	 * @param ureq
	 * @param wControl
	 * @param identitiesList
	 * @param status
	 * @param showEmailButton
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl, List<Identity> identitiesList,
			Integer status, boolean showEmailButton, boolean showTitle) {
		super(ureq, wControl);

		mailVC = createVelocityContainer("usermanagerMail");

		backFromMail = LinkFactory.createLinkBack(mailVC, this);

		userListVC = createVelocityContainer("usermanagerUserlist");
		this.showEmailButton = showEmailButton;

		userListVC.contextPut("showBackButton", Boolean.FALSE);
		userListVC.contextPut("showTitle", new Boolean(showTitle));

		initUserListCtr(ureq, identitiesList, status);
		userListVC.put("userlist", tableCtr.getInitialComponent());
		userListVC.contextPut("emptyList", (identitiesList.size() == 0 ? Boolean.TRUE : Boolean.FALSE));

		panel = putInitialPanel(userListVC);
	}
	
	//fxdiff BAKS-7 Resume function
	public WindowControl getTableControl() {
		return tableCtr == null ? null : tableCtr.getWindowControlForDebug();
	}

	@Override
	//fxdiff BAKS-7 Resume function
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(state instanceof StateMapped) {
			StateMapped searchState = (StateMapped)state;
			searchform.setStateEntry(searchState);
			
			if(entries != null && entries.size() > 0) {
				String table = entries.get(0).getOLATResourceable().getResourceableTypeName();
				if("table".equals(table)) {
					entries.remove(0);
					event(ureq, searchform, Event.DONE_EVENT);
				}
			}
		}
		
		if(entries == null || entries.isEmpty()) return;
		
		Long identityKey = entries.get(0).getOLATResourceable().getResourceableId();
		if(identityKey != null && identitiesList != null) {
			for(Identity identity:identitiesList) {
				if(identityKey.equals(identity.getKey())) {
					foundIdentity = identity;
					fireEvent(ureq, new SingleIdentityChosenEvent(foundIdentity));
					entries.remove(0);
					break;
				}
			}
		}
	}

	/**
	 * Remove the given identites from the list of identites in the table model
	 * and reinitialize the table controller
	 * 
	 * @param ureq
	 * @param tobeRemovedIdentities
	 */
	public void removeIdentitiesFromSearchResult(UserRequest ureq, List<Identity> tobeRemovedIdentities) {
		PersistenceHelper.removeObjectsFromList(identitiesList, tobeRemovedIdentities);
		initUserListCtr(ureq, identitiesList, null);
		userListVC.put("userlist", tableCtr.getInitialComponent());
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component,
	 *      org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Component source, Event event) {

		if (source == backFromMail) {
			panel.setContent(userListVC);
		} else if (source == backFromList) {
			panel.setContent(userSearchVC);
		}
	}

	/**
	 * Initialize the table controller using the list of identities
	 * 
	 * @param ureq
	 * @param identitiesList
	 */
	private void initUserListCtr(UserRequest ureq, List<Identity> myIdentities, Integer searchStatusField) {
		boolean actionEnabled = true;
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setTableEmptyMessage(translate("error.no.user.found"));
		if ((searchStatusField != null) && (searchStatusField.equals(Identity.STATUS_DELETED))) {
			actionEnabled = false;
		}
		tdm = ExtendedIdentitiesTableControllerFactory.createTableDataModel(ureq, myIdentities, actionEnabled);
		
		removeAsListenerAndDispose(tableCtr);
		//fxdiff BAKS-7 Resume function
		OLATResourceable ores = OresHelper.createOLATResourceableInstance("table", 0l);
		ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
		WindowControl bwControl = addToHistory(ureq, ores, null);
		tableCtr = ExtendedIdentitiesTableControllerFactory.createController(tdm, ureq, bwControl, actionEnabled);
		listenTo(tableCtr);
		
		if (showEmailButton) {
			tableCtr.addMultiSelectAction("command.mail", CMD_MAIL);
		}
		if (actionEnabled){
			tableCtr.addMultiSelectAction("action.bulkedit", CMD_BULKEDIT);
		}
		if (showEmailButton || actionEnabled){
			tableCtr.setMultiSelect(true);
		}
	}

	/**
	 * @return List of identities that match the criterias from the search form
	 */
	private List<Identity> findIdentitiesFromSearchForm() {
		BaseSecurity secMgr = BaseSecurityManager.getInstance();
		// get user attributes from form
		String login = searchform.getStringValue("login");
		Integer status = null;

		// get user fields from form
		// build user fields search map
		Map<String, String> userPropertiesSearch = new HashMap<String, String>();
		for (UserPropertyHandler userPropertyHandler : searchform.getPropertyHandlers()) {
			if (userPropertyHandler == null) continue;
			FormItem ui = searchform.getItem(userPropertyHandler.getName());
			String uiValue = userPropertyHandler.getStringValue(ui);
			if (StringHelper.containsNonWhitespace(uiValue)) {
				userPropertiesSearch.put(userPropertyHandler.getName(), uiValue);
			}
		}
		if (userPropertiesSearch.isEmpty()) userPropertiesSearch = null;

		// get group memberships from form
		List<SecurityGroup> groupsList = new ArrayList<SecurityGroup>();
		if (searchform.getRole("admin")) {
			SecurityGroup group = secMgr.findSecurityGroupByName(org.olat.basesecurity.Constants.GROUP_ADMIN);
			groupsList.add(group);
		}
		if (searchform.getRole("author")) {
			SecurityGroup group = secMgr.findSecurityGroupByName(org.olat.basesecurity.Constants.GROUP_AUTHORS);
			groupsList.add(group);
		}
		if (searchform.getRole("groupmanager")) {
			SecurityGroup group = secMgr.findSecurityGroupByName(org.olat.basesecurity.Constants.GROUP_GROUPMANAGERS);
			groupsList.add(group);
		}
		if (searchform.getRole("usermanager")) {
			SecurityGroup group = secMgr.findSecurityGroupByName(org.olat.basesecurity.Constants.GROUP_USERMANAGERS);
			groupsList.add(group);
		}
		if (searchform.getRole("oresmanager")) {
			SecurityGroup group = secMgr.findSecurityGroupByName(org.olat.basesecurity.Constants.GROUP_INST_ORES_MANAGER);
			groupsList.add(group);
		}
		
		status = searchform.getStatus();
		
		SecurityGroup[] groups = groupsList.toArray(new SecurityGroup[groupsList.size()]);

		// no permissions in this form so far
		PermissionOnResourceable[] permissionOnResources = null;

		
		String[] authProviders = searchform.getAuthProviders();
		
		
		// get date constraints from form
		Date createdBefore = searchform.getBeforeDate();
		Date createdAfter = searchform.getAfterDate();
		Date userLoginBefore = searchform.getUserLoginBefore();
		Date userLoginAfter = searchform.getUserLoginAfter();

		// now perform power search
		List<Identity> myIdentities = secMgr.getIdentitiesByPowerSearch((login.equals("") ? null : login), userPropertiesSearch, true, groups,
				permissionOnResources, authProviders, createdAfter, createdBefore, userLoginAfter, userLoginBefore, status,getUserInst());

		return myIdentities;
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.Controller, org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == searchform) {
			if (event == Event.DONE_EVENT) {
				// form validation was ok
				identitiesList = findIdentitiesFromSearchForm();
				initUserListCtr(ureq, identitiesList, null);
				userListVC.put("userlist", tableCtr.getInitialComponent());
				userListVC.contextPut("emptyList", (identitiesList.size() == 0 ? Boolean.TRUE : Boolean.FALSE));
				panel.setContent(userListVC);

				//fxdiff BAKS-7 Resume function
				ContextEntry currentEntry = getWindowControl().getBusinessControl().getCurrentContextEntry();
				if(currentEntry != null) {
					currentEntry.setTransientState(searchform.getStateEntry());
				}
				addToHistory(ureq, tableCtr);
			} else if (event == Event.CANCELLED_EVENT) {
				fireEvent(ureq, Event.CANCELLED_EVENT);
			}
		} else if (source == tableCtr) {
			if (event.getCommand().equals(Table.COMMANDLINK_ROWACTION_CLICKED)) {
				TableEvent te = (TableEvent) event;
				String actionid = te.getActionId();
				if (actionid.equals(ExtendedIdentitiesTableControllerFactory.COMMAND_SELECTUSER)) {
					int rowid = te.getRowId();
					foundIdentity = tdm.getIdentityAt(rowid);
					// Tell parentController that a subject has been found
					fireEvent(ureq, new SingleIdentityChosenEvent(foundIdentity));
				} else if (actionid.equals(ExtendedIdentitiesTableControllerFactory.COMMAND_VCARD)) {
					// get identitiy and open new visiting card controller in new window
					int rowid = te.getRowId();
					final Identity identity = tdm.getIdentityAt(rowid);
					ControllerCreator userInfoMainControllerCreator = new ControllerCreator() {
						public Controller createController(UserRequest lureq, WindowControl lwControl) {
							return new UserInfoMainController(lureq, lwControl, identity);
						}
					};
					// wrap the content controller into a full header layout
					ControllerCreator layoutCtrlr = BaseFullWebappPopupLayoutFactory.createAuthMinimalPopupLayout(ureq, userInfoMainControllerCreator);
					// open in new browser window
					PopupBrowserWindow pbw = getWindowControl().getWindowBackOffice().getWindowManager().createNewPopupBrowserWindowFor(ureq,
							layoutCtrlr);
					pbw.open(ureq);
					//
				}
			}
			if (event instanceof TableMultiSelectEvent) {
				// Multiselect events
				TableMultiSelectEvent tmse = (TableMultiSelectEvent) event;
				if (tmse.getAction().equals(CMD_BULKEDIT)) {
					if (tmse.getSelection().isEmpty()) {
						// empty selection
						showWarning("msg.selectionempty");
						return;
					}
					selectedIdentities = tdm.getIdentities(tmse.getSelection());
					final UserBulkChangeManager ubcMan = UserBulkChangeManager.getInstance();
					// valid selection: load in wizard
					Step start = new UserBulkChangeStep00(ureq, selectedIdentities);
					Roles roles = ureq.getUserSession().getRoles();
					isAdministrativeUser = (roles.isAuthor() || roles.isGroupManager() || roles.isUserManager() || roles.isOLATAdmin());

					// callback executed in case wizard is finished.
					StepRunnerCallback finish = new StepRunnerCallback() {
						public Step execute(UserRequest ureq1, WindowControl wControl1, StepsRunContext runContext) {
							// all information to do now is within the runContext saved
							boolean hasChanges = false;
							try {
								if (runContext.containsKey("validChange") && ((Boolean) runContext.get("validChange")).booleanValue()) {
									HashMap<String, String> attributeChangeMap = (HashMap<String, String>) runContext.get("attributeChangeMap");
									HashMap<String, String> roleChangeMap = (HashMap<String, String>) runContext.get("roleChangeMap");
									List<Long> ownGroups = (List<Long>) runContext.get("ownerGroups");
									List<Long> partGroups = (List<Long>) runContext.get("partGroups");
									List<Long> mailGroups = (List<Long>) runContext.get("mailGroups");
									if (attributeChangeMap.size() != 0 || roleChangeMap.size() != 0 || ownGroups.size() != 0 || partGroups.size() != 0){
										Identity addingIdentity = ureq1.getIdentity();
										ubcMan.changeSelectedIdentities(selectedIdentities, attributeChangeMap, roleChangeMap, notUpdatedIdentities,
											isAdministrativeUser, ownGroups, partGroups, mailGroups, getTranslator(), addingIdentity);
										hasChanges = true;
									}
								}
							} catch (Exception any) {
								// return new ErrorStep
							}
							// signal correct completion and tell if changes were made or not.
							return hasChanges ? StepsMainRunController.DONE_MODIFIED : StepsMainRunController.DONE_UNCHANGED;
						}
					};

					removeAsListenerAndDispose(userBulkChangeStepsController);
					userBulkChangeStepsController = new StepsMainRunController(ureq, getWindowControl(), start, finish, null,
							translate("bulkChange.title"), "o_sel_user_bulk_change_wizard");
					listenTo(userBulkChangeStepsController);
					
					getWindowControl().pushAsModalDialog(userBulkChangeStepsController.getInitialComponent());

				} else if (tmse.getAction().equals(CMD_MAIL)) {
					if (tmse.getSelection().isEmpty()) {
						// empty selection
						showWarning("msg.selectionempty");
						return;
					}
					// create e-mail message
					ContactMessage cmsg = new ContactMessage(ureq.getIdentity());

					selectedIdentities = tdm.getIdentities(tmse.getSelection());
					ContactList contacts = new ContactList(translate("mailto.userlist"));
					contacts.addAllIdentites(selectedIdentities);
					cmsg.addEmailTo(contacts);

					// create contact form controller with ContactMessage
					removeAsListenerAndDispose(contactCtr);
					contactCtr = new ContactFormController(ureq, getWindowControl(), false, true, false, false, cmsg);
					listenTo(contactCtr);

					mailVC.put("mailform", contactCtr.getInitialComponent());
					panel.setContent(mailVC);
				}
			}
		} else if (source == contactCtr) {
			// in any case go back to list (events: done, failed or cancel)
			panel.setContent(userListVC);
		} else if (source == userBulkChangeStepsController) {
			if (event == Event.CANCELLED_EVENT) {
				getWindowControl().pop();
			} else if (event == Event.CHANGED_EVENT) {
				getWindowControl().pop();
				Integer selIdentCount = selectedIdentities.size();
				if (notUpdatedIdentities.size() > 0) {
					Integer notUpdatedIdentCount = notUpdatedIdentities.size();
					Integer sucChanges = selIdentCount - notUpdatedIdentCount;
					String changeErrors = "";
					for (String err : notUpdatedIdentities) {
						changeErrors += err + "<br />";
					}
					getWindowControl().setError(translate("bulkChange.partialsuccess",
							new String[] { sucChanges.toString(), selIdentCount.toString(), changeErrors }));
				} else {
					showInfo("bulkChange.success");
				}
				// update table model - has changed
				reloadDataModel(ureq);

			} else if (event == Event.DONE_EVENT){
				showError("bulkChange.failed");
			}
			
		}
	}

	/**
	 * Reload the currently used identitiesList and rebuild the table controller
	 * 
	 * @param ureq
	 */
	private void reloadDataModel(UserRequest ureq) {
		if (identitiesList == null) return;
		BaseSecurity secMgr = BaseSecurityManager.getInstance();
		for (int i = 0; i < identitiesList.size(); i++) {
			Identity ident = identitiesList.get(i);
			Identity refrshed = secMgr.loadIdentityByKey(ident.getKey());
			identitiesList.set(i, refrshed);
		}
		initUserListCtr(ureq, identitiesList, null);
		userListVC.put("userlist", tableCtr.getInitialComponent());
	}

	/**
	 * Reload the identity used currently in the workflow and in the currently
	 * activated user table list model. The identity will be reloaded from the
	 * database to have accurate values.
	 */
	public void reloadFoundIdentity() {
		if (foundIdentity == null) throw new AssertException("reloadFoundIdentity called but foundIdentity is null");
		// reload the found identity
		foundIdentity = (Identity) DBFactory.getInstance().loadObject(foundIdentity);
		// replace the found identity in the table list model to display changed
		// values
		List identities = tdm.getObjects();
		PersistenceHelper.replaceObjectInListByKey(identities, foundIdentity);
	}

	/**
	 * 
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	protected void doDispose() {
		//
	}

}

/** 
 * Initial Date: Jan 31, 2006
 * 
 * @author gnaegi
 * 
 * Description: Search form for the usermanager power search. Should only be
 * used by the UserManagerSearchController
 */
class UsermanagerUserSearchForm extends FormBasicController {
	private static final String formIdentifyer = UsermanagerUserSearchForm.class.getCanonicalName();
	private TextElement login;
	private SelectionElement roles;
	private SingleSelection status;
	private SelectionElement auth;
	private DateChooser beforeDate, afterDate, userLoginBefore, userLoginAfter;
	private FormLink searchButton;
	
	
	private List<UserPropertyHandler> userPropertyHandlers;
	
	private String[] statusKeys, statusValues;
	private String[] roleKeys, roleValues;
	private String[] authKeys, authValues;
	
	Map <String,FormItem>items;
	/**
	 * @param binderName
	 * @param cancelbutton
	 */
	public UsermanagerUserSearchForm(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		
		UserManager um = UserManager.getInstance();
		userPropertyHandlers = um.getUserPropertyHandlersFor(formIdentifyer, true);
		
		items = new HashMap<String,FormItem>(); 
		
		roleKeys = new String[] {
				"admin", "author", "groupmanager", "usermanager", "oresmanager"
		};
		
		roleValues = new String[]{
				translate("search.form.constraint.admin"),
				translate("search.form.constraint.author"),
				translate("search.form.constraint.groupmanager"),
				translate("search.form.constraint.usermanager"),
				translate("search.form.constraint.oresmanager")
		};
		
		statusKeys = new String[] { 
				Integer.toString(Identity.STATUS_VISIBLE_LIMIT),
				Integer.toString(Identity.STATUS_ACTIV),
				Integer.toString(Identity.STATUS_PERMANENT),
				Integer.toString(Identity.STATUS_LOGIN_DENIED)
		};
		statusValues = new String[] {
				translate("rightsForm.status.any.visible"),
				translate("rightsForm.status.activ"),
				translate("rightsForm.status.permanent"),
				translate("rightsForm.status.login_denied")
		};
		
		// take all providers from the config file
		// convention is that a translation key "search.form.constraint.auth." +
		// providerName
		// must exist. the element is stored using the name "auth." + providerName
		List <String>authKeyList = new ArrayList<String>();
		List <String>authValueList = new ArrayList<String>();
		
		Collection<AuthenticationProvider> providers = LoginModule.getAuthenticationProviders();
		for (AuthenticationProvider provider:providers) {
			if (provider.isEnabled()) {
				authKeyList.add(provider.getName());
				authValueList.add(translate(
						"search.form.constraint.auth." +provider.getName()
				));
			}
		}
		if(WebDAVManager.getInstance().isEnabled()) {
			authKeyList.add(WebDAVAuthManager.PROVIDER_WEBDAV);
			authValueList.add(translate("search.form.constraint.auth.WEBDAV"));
		}
		
		// add additional no authentication element
		authKeyList.add("noAuth");
		authValueList.add(translate("search.form.constraint.auth.none"));
		
		authKeys   = authKeyList.toArray(new String[authKeyList.size()]);
		authValues = authValueList.toArray(new String[authValueList.size()]);
		

		initForm(ureq);
	}

	public List<UserPropertyHandler> getPropertyHandlers() {
		return userPropertyHandlers;
	}

	protected Date getBeforeDate() {
		return beforeDate.getDate();
	}
	protected Date getAfterDate() {
		return afterDate.getDate();
	}
	
	protected Date getUserLoginBefore() {
		return userLoginBefore.getDate();
	}

	protected Date getUserLoginAfter() {
		return userLoginAfter.getDate();
	}

	protected FormItem getItem(String name) {
		return items.get(name);
	}

	protected String getStringValue(String key) {
		FormItem f = items.get(key);
		if (f == null) return null;
		if (f instanceof TextElement) {
			return ((TextElement) f).getValue();
		}
		return null;
	}

	//fxdiff BAKS-7 Resume function
	protected void setStringValue(String key, String value) {
		FormItem f = items.get(key);
		if (f == null) return;
		if (f instanceof TextElement) {
			((TextElement) f).setValue(value);
		}
	}
	
	protected boolean getRole (String key) {
		return roles.isSelected(Arrays.asList(roleKeys).indexOf(key));
	}
	
	protected Integer getStatus () {
		return new Integer(status.getSelectedKey());
	}
	
	protected String[] getAuthProviders () {
		List<String> apl = new ArrayList<String>();
		for (int i=0; i<authKeys.length; i++) {
			if (auth.isSelected(i)) {
				if("noAuth".equals(authKeys[i])) {
					apl.add(null);//special case
				} else {
					apl.add(authKeys[i]);
				}
			}
		}
		return apl.toArray(new String[apl.size()]);
	}

	//fxdiff BAKS-7 Resume function
	protected StateMapped getStateEntry() {
		StateMapped state = new StateMapped();
		if(items != null) {
			for(Map.Entry<String, FormItem> itemEntry : items.entrySet()) {
				String key = itemEntry.getKey();
				FormItem f = itemEntry.getValue();
				if (f instanceof TextElement) {
					state.getDelegate().put(key, ((TextElement) f).getValue());
				}	
			}	
		}
		
		if(auth.isMultiselect()) {
			//auth.
		}
		if(roles.isMultiselect()) {
			//
		}
		if(status.isOneSelected()) {
			//
		}
		
		return state;
	}

	//fxdiff BAKS-7 Resume function
	protected void setStateEntry(StateMapped state) {
		for(Map.Entry<String, String> entry:state.getDelegate().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			setStringValue(key, value);
		}	
	}
	
	@Override
	protected void formOK(UserRequest ureq) {
		fireEvent (ureq, Event.DONE_EVENT);
	}
	
	@Override
	@SuppressWarnings("unused")
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
	
		login = uifactory.addTextElement("login", "search.form.login", 128, "", formLayout);
		items.put("login", login);

		Translator tr = Util.createPackageTranslator(
				UserPropertyHandler.class,
				getLocale(), getTranslator()
		);
		
		String currentGroup = null;
		// Add all available user fields to this form
		for (UserPropertyHandler userPropertyHandler : userPropertyHandlers) {
			if (userPropertyHandler == null) continue;
			
			String group = userPropertyHandler.getGroup();
			if (!group.equals(currentGroup)) {
				if (currentGroup != null) {
					uifactory.addSpacerElement("spacer_" + group, formLayout, false);
				}
				currentGroup = group;
			}

			FormItem fi = userPropertyHandler.addFormItem(
					getLocale(), null, getClass().getCanonicalName(), false, formLayout
			);
			// OO-155: Do not validate items, this is a search form!
			if (fi instanceof TextElement) {
				TextElement textElement = (TextElement) fi;
				textElement.setItemValidatorProvider(null);
			}
			
			fi.setTranslator(tr);
			items.put(fi.getName(), fi);
		}

		uifactory.addSpacerElement("space1", formLayout, false);
		roles = uifactory.addCheckboxesVertical(
				"roles", "search.form.title.roles", formLayout, roleKeys, roleValues,
				null, 1
		);
		
		
		uifactory.addSpacerElement("space2", formLayout, false);
		auth = uifactory.addCheckboxesVertical(
				"auth", "search.form.title.authentications",
				formLayout, authKeys, authValues, null, 1
		);
		
		
		uifactory.addSpacerElement("space3", formLayout, false);
		status = uifactory.addRadiosVertical(
				"status", "search.form.title.status", formLayout, statusKeys, statusValues
		);
		status.select(statusKeys[0], true);		
		
		uifactory.addSpacerElement("space4", formLayout, false);
		afterDate  = uifactory.addDateChooser("search.form.afterDate","",formLayout);
		afterDate.setValidDateCheck("error.search.form.no.valid.datechooser");
		beforeDate = uifactory.addDateChooser("search.form.beforeDate","",formLayout);
		beforeDate.setValidDateCheck("error.search.form.no.valid.datechooser");
		
		uifactory.addSpacerElement("space5", formLayout, false);
		userLoginAfter = uifactory.addDateChooser("search.form.userLoginAfterDate", "", formLayout);
		userLoginAfter.setValidDateCheck("error.search.form.no.valid.datechooser");
		userLoginBefore = uifactory.addDateChooser("search.form.userLoginBeforeDate", "", formLayout);
		userLoginBefore.setValidDateCheck("error.search.form.no.valid.datechooser");
		
		// creation date constraints
		/*
		addFormElement("space3", new SpacerElement(true, false));
		addFormElement("title.date", new TitleElement("search.form.title.date"));
		afterDate = new DateElement("search.form.afterDate", getLocale());
		addFormElement("afterDate", afterDate);
		beforeDate = new DateElement("search.form.beforeDate", getLocale());
		addFormElement("beforeDate", beforeDate);

		addSubmitKey("submit.search", "submit.search");
		 */
		
		uifactory.addSpacerElement("spaceBottom", formLayout, false);
		
		// Don't use submit button, form should not be marked as dirty since this is
		// not a configuration form but only a search form (OLAT-5626)
		searchButton = uifactory.addFormLink("search", formLayout, Link.BUTTON);
		searchButton.addActionListener(this, FormEvent.ONCLICK);
		
	}
	
	/**
	 * @see org.olat.core.gui.components.form.flexible.impl.FormBasicController#formInnerEvent(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.form.flexible.FormItem,
	 *      org.olat.core.gui.components.form.flexible.impl.FormEvent)
	 */
	@Override
	@SuppressWarnings("unused")
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if (source == searchButton) {
			source.getRootForm().submit(ureq);			
		}
	}

	@Override
	protected void doDispose() {
		//
	}
}