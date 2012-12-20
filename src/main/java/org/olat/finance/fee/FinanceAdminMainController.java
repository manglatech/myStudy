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

package org.olat.finance.fee;

import org.olat.basesecurity.events.SingleIdentityChosenEvent;
import org.olat.core.commons.fullWebApp.LayoutMain3ColsController;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.panel.Panel;
import org.olat.core.gui.components.tree.GenericTreeModel;
import org.olat.core.gui.components.tree.GenericTreeNode;
import org.olat.core.gui.components.tree.MenuTree;
import org.olat.core.gui.components.tree.TreeModel;
import org.olat.core.gui.components.tree.TreeNode;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.MainLayoutBasicController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.context.BusinessControlFactory;
import org.olat.core.logging.AssertException;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.resource.OresHelper;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.fee.ui.FeeCategoryAdminContoller;
import org.olat.finance.fee.ui.FeeCategoryListController;
import org.olat.finance.fee.ui.FeeListController;
import org.olat.finance.user.ui.SelectUserAccountController;
import org.olat.finance.user.ui.UserAccountDetailController;
import org.olat.finance.user.ui.UserAccountSearchController;
import org.olat.util.logging.activity.LoggingResourceable;

/**
 * <pre>
 * Initial Date:  October 12 2012
 * @author Dhaval Patel
 * 
 * Comment:
 * This controller offers user and system group administrative functionality. The 
 * features can be enabled / disabled in the spring file for the user
 * manager, OLAT administrators always have full access to the tools.
 * </pre>
 */
public class FinanceAdminMainController extends MainLayoutBasicController {

	private static final Object FEE_TEMPLATE_LIST_UOBJECT = "feetemplatelist";
	private static final Object FEE_LIST_UOBJECT = "feelist";
	private static final Object USER_ACCOUNT_SEARCH_LIST_UOBJECT = "userAccountSearchList";
	//private static final Object ALL_UNPAID_USERS_LIST_UOBJECT = "allUnPaidUsersList";
	private static final Object FINANCE_ADMIN_UOBJECT = "financeadmin";
	private static final Object FEE_ADMIN_UOBJECT = "feeadmin";
	private static final Object USER_ACCOUNT_PREDEFINE_SEARCH_LIST_UOBJECT = "userAccountPredefineSearch";
	OLog log = Tracing.createLoggerFor(this.getClass());

	private MenuTree olatMenuTree;
	private Panel content;

	private LayoutMain3ColsController columnLayoutCtr;
	private Controller contentCtr;
	private FeeCategoryAdminContoller feeViewController;
	private UserAccountDetailController userAccountDetails;
	
	
	/**
	 * Constructor of the home main controller
	 * 
	 * @param ureq
	 *            The user request
	 * @param wControl
	 *            The current window controller
	 */
	public FinanceAdminMainController(UserRequest ureq, WindowControl wControl) {

		super(ureq, wControl);

		olatMenuTree = new MenuTree("olatMenuTree");
		olatMenuTree.setExpandSelectedNode(false);
		TreeModel tm = buildTreeModel(ureq);
		olatMenuTree.setTreeModel(tm);
		TreeNode firstNode = (TreeNode) tm.getRootNode().getChildAt(0);
		olatMenuTree.setSelectedNodeId(firstNode.getIdent());
		olatMenuTree.addListener(this);

		// NEED TO FIND WHY????
		OLATResourceable ores = OresHelper.createOLATResourceableInstance(
				firstNode.getUserObject().toString(), 0l);
		ThreadLocalUserActivityLogger
				.addLoggingResourceInfo(LoggingResourceable
						.wrapBusinessPath(ores));
		WindowControl bwControl = addToHistory(ureq, ores, null);

		contentCtr = new SelectUserAccountController(ureq, bwControl);
		listenTo(contentCtr); // auto dispose later

		content = new Panel("content");
		content.setContent(contentCtr.getInitialComponent());

		columnLayoutCtr = new LayoutMain3ColsController(ureq,
				getWindowControl(), olatMenuTree, null, content,
				"financeadminmain");
		columnLayoutCtr.addCssClassToMain("o_useradmin");

		listenTo(columnLayoutCtr); // auto dispose later
		putInitialPanel(columnLayoutCtr.getInitialComponent());
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component,
	 *      org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == olatMenuTree) {
			if (event.getCommand().equals(MenuTree.COMMAND_TREENODE_CLICKED)) {
				TreeNode selTreeNode = olatMenuTree.getSelectedNode();
				Object userObject = selTreeNode.getUserObject();
				Component resComp = initComponentFromMenuCommand(userObject,
						ureq);
				content.setContent(resComp);
			} else { // the action was not allowed anymore
				content.setContent(null); // display an empty field (empty
											// panel)
			}
		} else {
			log.warn("Unhandled olatMenuTree event: " + event.getCommand());
		}
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.Controller,
	 *      org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Controller source, Event event) {
		if(source == contentCtr){
			if (event instanceof SingleFeeCategoryChosenEvent){
				SingleFeeCategoryChosenEvent feeSelectEvent = (SingleFeeCategoryChosenEvent) event;
				FeeCategory feeCategory = feeSelectEvent.getChosenFeeCategory();
				feeViewController = new FeeCategoryAdminContoller(ureq,getWindowControl(),feeCategory);
				feeViewController.setBackButtonEnabled(true);
				content.setContent(feeViewController.getInitialComponent());
				listenTo(feeViewController);
			} else if(event instanceof SingleIdentityChosenEvent){
				SingleIdentityChosenEvent selectedIdentity = (SingleIdentityChosenEvent) event;
				userAccountDetails = new UserAccountDetailController(ureq, getWindowControl(), selectedIdentity.getChosenIdentity());
				userAccountDetails.setBackButtonEnabled(true);
				content.setContent(userAccountDetails.getInitialComponent());
				listenTo(userAccountDetails);
			}
		}else if (source == feeViewController) {
			if (event == Event.BACK_EVENT) {
				removeAsListenerAndDispose(feeViewController);
				feeViewController = null;
				content.setContent(contentCtr.getInitialComponent());
			}
		}else if (source == userAccountDetails){
			if (event == Event.BACK_EVENT) {
				removeAsListenerAndDispose(userAccountDetails);
				userAccountDetails = null;
				content.setContent(contentCtr.getInitialComponent());
			}
		}
	}

	private Component initComponentFromMenuCommand(Object uobject,
			UserRequest ureq) {

		OLATResourceable ores = OresHelper.createOLATResourceableInstance(
				uobject.toString(), 0l);
		WindowControl bwControl = BusinessControlFactory.getInstance()
				.createBusinessWindowControl(ores, null, getWindowControl());

		removeAsListenerAndDispose(contentCtr);
		if (uobject.equals(FEE_LIST_UOBJECT)
				|| uobject.equals(FINANCE_ADMIN_UOBJECT)
				|| uobject.equals(FEE_ADMIN_UOBJECT)) {
			contentCtr = new FeeListController(ureq, bwControl);
			addToHistory(ureq, bwControl);
			listenTo(contentCtr);
			return contentCtr.getInitialComponent();
		} else if (uobject.equals(FEE_TEMPLATE_LIST_UOBJECT)) {
			contentCtr = new FeeCategoryListController(ureq, bwControl);
			addToHistory(ureq, bwControl);
			listenTo(contentCtr);
			return contentCtr.getInitialComponent();
		} else if (uobject.equals(USER_ACCOUNT_SEARCH_LIST_UOBJECT)) {
			contentCtr = new SelectUserAccountController(ureq, bwControl);
			addToHistory(ureq, bwControl);
			listenTo(contentCtr);
			return contentCtr.getInitialComponent();
		} else {
			throw new AssertException(
					"did not expect to land here in UserAdminMainController this is because uboject is "
							+ uobject.toString());
		}
	}

	private TreeModel buildTreeModel(UserRequest ureq) {

		GenericTreeNode gtnChild, admin, feeAdmin;
		Translator translator = getTranslator();

		GenericTreeModel gtm = new GenericTreeModel();
		admin = new GenericTreeNode();
		admin.setTitle(translator.translate("menu.financeadmin"));
		admin.setUserObject("financeadmin");
		admin.setAltText(translator.translate("menu.financeadmin.alt"));
		gtm.setRootNode(admin);

		gtnChild = new GenericTreeNode();
		gtnChild.setTitle(translator.translate("user.account.search.list"));
		gtnChild.setUserObject(USER_ACCOUNT_SEARCH_LIST_UOBJECT);
		gtnChild.setAltText(translator.translate("user.account.search.list.alt"));
		admin.setDelegate(gtnChild);
		admin.addChild(gtnChild);
		
		gtnChild = new GenericTreeNode();
		gtnChild.setTitle(translator.translate("user.account.predefine.search"));
		gtnChild.setUserObject(USER_ACCOUNT_PREDEFINE_SEARCH_LIST_UOBJECT);
		gtnChild.setAltText(translator.translate("user.account.predefine.search.alt"));
		admin.setDelegate(gtnChild);
		admin.addChild(gtnChild);
		
		feeAdmin = new GenericTreeNode();
		feeAdmin.setTitle(translator.translate("fee.admin"));
		feeAdmin.setUserObject("feeadmin");
		feeAdmin.setAltText(translator.translate("fee.admin.alt"));
		admin.setDelegate(feeAdmin);
		admin.addChild(feeAdmin);

		gtnChild = new GenericTreeNode();
		gtnChild.setTitle(translator.translate("fee.list"));
		gtnChild.setUserObject(FEE_LIST_UOBJECT);
		gtnChild.setAltText(translator.translate("fee.list.alt"));
		feeAdmin.setDelegate(gtnChild);
		feeAdmin.addChild(gtnChild);

		gtnChild = new GenericTreeNode();
		gtnChild.setTitle(translator.translate("fee.template.list"));
		gtnChild.setUserObject(FEE_TEMPLATE_LIST_UOBJECT);
		gtnChild.setAltText(translator.translate("fee.template.list.alt"));
		feeAdmin.setDelegate(gtnChild);
		feeAdmin.addChild(gtnChild);
		
		
		/*gtnChild = new GenericTreeNode();
		gtnChild.setTitle(translator.translate("all.unpaid.user.list"));
		gtnChild.setUserObject(ALL_UNPAID_USERS_LIST_UOBJECT);
		gtnChild.setAltText(translator.translate("all.unpaid.user.list.alt"));
		feeAdmin.setDelegate(gtnChild);
		feeAdmin.addChild(gtnChild);*/
		

		return gtm;
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	protected void doDispose() {
	}

}
