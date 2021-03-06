package org.olat.finance.user.ui;

import java.util.Date;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.DateChooser;
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
	private DateChooser dueDate;
	
	private FormSubmit searchButton;
	private SingleSelection paidStatusEl;
	//private MultipleSelectionElement headlessEl;
	
	private String[] paidStatusKeys = {"all", "unPaid", "paid", "partialPaid","markAsPaid"};

	public UserAccountSearchController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		uifactory.addStaticTextElement("heading1", null, translate("search.user.account.info"), formLayout);
		
		userName = uifactory.addTextElement("usc_username", "usc.username", 255, "", formLayout);
		userName.setFocus(true);
		userName.setDisplaySize(28);
		
		templateName = uifactory.addTextElement("usc_templatename", "usc.templatename", 255, "", formLayout);
		templateName.setDisplaySize(28);
		
		dueDate = uifactory.addDateChooser("usc_due_date", "usc.due.date", "", formLayout);
		dueDate.setDateChooserTimeEnabled(false);
		dueDate.setDateChooserDateFormat("%d.%m.%Y %H:%M");
		dueDate.setCustomDateFormat("EEE, MMM d, yyyy");
		
		//roles
		String[] paidStatusValues = new String[paidStatusKeys.length];
		for(int i=paidStatusKeys.length; i-->0; ) {
			paidStatusValues[i] = translate("search." + paidStatusKeys[i]);
		}
		paidStatusEl = uifactory.addRadiosHorizontal("paidStatus", "search.paid.status", formLayout, paidStatusKeys, paidStatusValues);
		paidStatusEl.select("unPaid", true);

		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
		formLayout.add(buttonLayout);
		buttonLayout.setElementCssClass("o_sel_group_search_groups_buttons");
		searchButton = uifactory.addFormSubmitButton("search", "search.user.account", buttonLayout);
	}

	protected void doDispose() {
		//
	}
	
	
	public String getTemplateName() {
		return templateName.getValue();
	}
	
	
	public String getUserName() {
		return userName.getValue();
	}
	
	public Date getDueDate(){
		return dueDate.getDate();
	}
	
	public boolean isEmpty() {
		return templateName.isEmpty() && userName.isEmpty();
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
		if(paidStatusEl != null) {
			if(e.isAllStatus()) {
				paidStatusEl.select("all", true);
			} else if(e.isUnPaidStatus()) {
				paidStatusEl.select("unPaid", true);
			} else if(e.isPaidStatus()) {
				paidStatusEl.select("paid", true);
			} else if(e.isPartialPaidStatus()) {
				paidStatusEl.select("partialPaid", true);
			} else if(e.isMarkAsPaid()) {
				paidStatusEl.select("markAsPaid", true);
			}
		}
	}

	private void fireSearchEvent(UserRequest ureq) {
		UserAccountSearchEvent e = new UserAccountSearchEvent();
		e.setUserName(getUserName());
		e.setTemplateName(getTemplateName());
		e.setDueDate(getDueDate());
		
		if(paidStatusEl != null && paidStatusEl.isOneSelected()) {
			e.setAllStatus(paidStatusEl.isSelected(0));
			e.setUnPaidStatus(paidStatusEl.isSelected(1));
			e.setPaidStatus(paidStatusEl.isSelected(2));
			e.setPartialPaidStatus(paidStatusEl.isSelected(3));
			e.setMarkAsPaid(paidStatusEl.isSelected(4));
		}
		fireEvent(ureq, e);
	}

}
