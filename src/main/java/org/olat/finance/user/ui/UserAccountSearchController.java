package org.olat.finance.user.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.FormSubmit;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.StringHelper;

public class UserAccountSearchController extends FormBasicController implements Activateable2  {

	private TextElement userName; 
	private TextElement templateName;
	private TextElement groupName;
	private FormSubmit searchButton;
	private SingleSelection paidStatusEl;
	//private MultipleSelectionElement headlessEl;
	
	private String[] paidStatusKeys = {"all", "unPaid", "paid", "partialPaid"};

	public UserAccountSearchController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl, "user_account_search");
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		uifactory.addStaticTextElement("heading1", null, translate("search.user.account.info"), formLayout);
		
		FormLayoutContainer leftContainer = FormLayoutContainer.createDefaultFormLayout("left_1", getTranslator());
		leftContainer.setRootForm(mainForm);
		formLayout.add(leftContainer);

		userName = uifactory.addTextElement("usc_username", "usc.username", 255, "", leftContainer);
		userName.setFocus(true);
		userName.setDisplaySize(28);
		
		templateName = uifactory.addTextElement("usc_templatename", "usc.templatename", 255, "", leftContainer);
		templateName.setDisplaySize(28);
		
		groupName = uifactory.addTextElement("usc_groupname", "usc.groupname", 255, "", leftContainer);
		groupName.setDisplaySize(28);

		FormLayoutContainer rightContainer = FormLayoutContainer.createDefaultFormLayout("right_1", getTranslator());
		rightContainer.setRootForm(mainForm);
		formLayout.add(rightContainer);
		
		//roles
		String[] paidStatusValues = new String[paidStatusKeys.length];
		for(int i=paidStatusKeys.length; i-->0; ) {
			paidStatusValues[i] = translate("search." + paidStatusKeys[i]);
		}
		paidStatusEl = uifactory.addRadiosHorizontal("roles", "search.roles", rightContainer, paidStatusKeys, paidStatusValues);
		paidStatusEl.select("all", true);

		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
		formLayout.add(buttonLayout);
		buttonLayout.setElementCssClass("o_sel_group_search_groups_buttons");
		searchButton = uifactory.addFormSubmitButton("search", "search", buttonLayout);
	}

	@Override
	protected void doDispose() {
		//
	}
	
	
	public String getTemplateName() {
		return templateName.getValue();
	}
	public String getGroupName() {
		return groupName.getValue();
	}
	
	public String getUserName() {
		return userName.getValue();
	}

	/**
	 * @return True if the text search fields are empty
	 */
	public boolean isEmpty() {
		return templateName.isEmpty() && groupName.isEmpty() && userName.isEmpty();
	}
	
	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;

		/*if(isEmpty()) {
			showError("search.mising.param");
			allOk = false;
		}*/
		return allOk && super.validateFormLogic(ureq);
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(state instanceof UserAccountSearchEvent) {
			setSearchEvent((UserAccountSearchEvent)state);
		}
	}
	@Override
	protected void formOK (UserRequest ureq) {
		fireSearchEvent(ureq);
	}
	
	@Override
	protected void formInnerEvent (UserRequest ureq, FormItem source, FormEvent event) {
		if (source == searchButton) {
			fireSearchEvent(ureq);
		}
	}
	private void setSearchEvent(UserAccountSearchEvent e) {
		if(StringHelper.containsNonWhitespace(e.getUserName())) {
			userName.setValue(e.getUserName());
		}
		if(StringHelper.containsNonWhitespace(e.getTemplateName())) {
			templateName.setValue(e.getTemplateName());
		}
		if(StringHelper.containsNonWhitespace(e.getGroupName())) {
			templateName.setValue(e.getGroupName());
		}
		if(paidStatusEl != null) {
			if(e.isAllStatus()) {
				paidStatusEl.select("all", true);
			} else if(e.isUnPaidStatus()) {
				paidStatusEl.select("unPaid", true);
			} else if(e.isPaidStatus()) {
				paidStatusEl.select("paid", true);
			} else if(e.isPartialPaidStatus()) {
				paidStatusEl.select("partialPaid", true);
			}
		}
	}

	private void fireSearchEvent(UserRequest ureq) {
		UserAccountSearchEvent e = new UserAccountSearchEvent();
		e.setUserName(getUserName());
		e.setTemplateName(getTemplateName());
		e.setGroupName(getGroupName());
		
		if(paidStatusEl != null && paidStatusEl.isOneSelected()) {
			e.setAllStatus(paidStatusEl.isSelected(0));
			e.setUnPaidStatus(paidStatusEl.isSelected(1));
			e.setPaidStatus(paidStatusEl.isSelected(2));
			e.setPartialPaidStatus(paidStatusEl.isSelected(3));
		}
		fireEvent(ureq, e);
	}

}
