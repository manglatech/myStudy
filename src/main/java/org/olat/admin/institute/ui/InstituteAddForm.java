package org.olat.admin.institute.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;

public class InstituteAddForm extends FormBasicController {

	private TextElement name;
	private TextElement instituteId;
	private TextElement numberOfUsers;
	
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String NUMBER = "Number";
	
	public InstituteAddForm(UserRequest ureq, WindowControl wControl,Translator translator) {
		super(ureq, wControl);
		this.setTranslator(translator);
		initForm(ureq);
		
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		uifactory.addStaticTextElement("heading1", null, translate("new.fee.form.please.enter"), formLayout);
		name = uifactory.addTextElement(NAME, "Name", 128, "", formLayout);
		name.setMandatory(true);
		name.setDisplaySize(30);
		
		instituteId = uifactory.addTextElement(ID, "Id", 128, "", formLayout);
		instituteId.setMandatory(true);
		instituteId.setDisplaySize(30);
		
		numberOfUsers = uifactory.addTextElement(NUMBER, "Users", 128, "", formLayout);
		numberOfUsers.setMandatory(true);
		numberOfUsers.setDisplaySize(30);
		
		uifactory.addFormSubmitButton("save", "submit.save", formLayout);
		
	}

	@Override
	protected void formOK(UserRequest ureq) {
		fireEvent(ureq, Event.DONE_EVENT);
	}

	/**
	 * @see org.olat.core.gui.components.form.flexible.impl.FormBasicController#formCancelled(org.olat.core.gui.UserRequest)
	 */
	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
	/**
	 * @see org.olat.core.gui.components.form.flexible.impl.FormBasicController#formNOK(org.olat.core.gui.UserRequest)
	 */
	@Override
	protected void formNOK(UserRequest ureq) {
		fireEvent(ureq, Event.FAILED_EVENT);
	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub
	}

	public TextElement getName() {
		return name;
	}

	public TextElement getInstituteId() {
		return instituteId;
	}

	public TextElement getNumberOfUsers() {
		return numberOfUsers;
	}
	
	
}
