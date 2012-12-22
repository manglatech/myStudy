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

public class CreateUserAccountSearchController extends FormBasicController implements Activateable2  {

	private TextElement userName; 
	private TextElement groupName;
	private TextElement className;
	private FormSubmit searchButton;
	private SingleSelection assignedStatusEl;
	//private MultipleSelectionElement headlessEl;
	
	private String[] accountStatusKeys = {"unAssigned","assigned"};

	public CreateUserAccountSearchController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		uifactory.addStaticTextElement("heading1", null, translate("create.user.account.form.info"), formLayout);
		
		userName = uifactory.addTextElement("usc_username", "usc.username", 255, "", formLayout);
		userName.setFocus(true);
		userName.setDisplaySize(28);
		
		groupName = uifactory.addTextElement("usc_groupname", "usc.groupname", 255, "", formLayout);
		groupName.setDisplaySize(28);
		
		className = uifactory.addTextElement("usc_coursegname", "usc.coursename", 255, "", formLayout);
		className.setDisplaySize(28);
		
		String[] accountStatusValues = new String[accountStatusKeys.length];
		for(int i=accountStatusKeys.length; i-->0; ) {
			accountStatusValues[i] = translate("search." + accountStatusKeys[i]);
		}
		assignedStatusEl = uifactory.addRadiosHorizontal("accountStatus", "search.account.status", formLayout, accountStatusKeys, accountStatusValues);
		assignedStatusEl.select("unAssigned", true);

		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
		formLayout.add(buttonLayout);
		buttonLayout.setElementCssClass("o_sel_group_search_groups_buttons");
		searchButton = uifactory.addFormSubmitButton("search", "search.user.account", buttonLayout);
	}

	protected void doDispose() {
		//
	}
	
	
	public String getGroupName() {
		return groupName.getValue();
	}
	public String getCourseName() {
		return className.getValue();
	}
	public String getUserName() {
		return userName.getValue();
	}
	
	public boolean isEmpty() {
		return groupName.isEmpty() && userName.isEmpty() && className.isEmpty();
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
		if(state instanceof CreateUserAccountSearchEvent) {
			setSearchEvent((CreateUserAccountSearchEvent)state);
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
	private void setSearchEvent(CreateUserAccountSearchEvent e) {
		if(StringHelper.containsNonWhitespace(e.getUserName())) {
			userName.setValue(e.getUserName());
		}
		if(StringHelper.containsNonWhitespace(e.getGroupName())) {
			groupName.setValue(e.getGroupName());
		}
		if(StringHelper.containsNonWhitespace(e.getCourseName())) {
			groupName.setValue(e.getCourseName());
		}
		
		if(assignedStatusEl != null) {
			if(e.isAssignedStatus()) {
				assignedStatusEl.select("assigned", true);
			} else if(e.isUnAssignedStatus()) {
				assignedStatusEl.select("unAssigned", true);
			}
		}
	}

	private void fireSearchEvent(UserRequest ureq) {
		CreateUserAccountSearchEvent e = new CreateUserAccountSearchEvent();
		e.setUserName(getUserName());
		e.setGroupName(getGroupName());
		e.setCourseName(getCourseName());
		
		if(assignedStatusEl != null && assignedStatusEl.isOneSelected()) {
			e.setAssignedStatus(assignedStatusEl.isSelected(0));
			e.setUnAssignedStatus(assignedStatusEl.isSelected(1));
		}
		fireEvent(ureq, e);
	}

}
