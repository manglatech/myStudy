package org.olat.finance.fee.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.finance.fee.model.FeeCategory;

public /**
 * <pre>
 * 
 *  Initial Date:  Jul 31, 2003
 * 
 *  @author gnaegi
 *  
 *  Comment:  
 *  Form for creating new a new user as administrator
 *  
 * </pre>
 */

class FeeCategoryFormController extends FormBasicController {
	
	private OLog log = Tracing.createLoggerFor(this.getClass());
	private static final String formIdentifyer = FeeCategoryFormController.class.getCanonicalName();
	
	private TextElement feeName;
	private TextElement feeDesc;
	
	private static final String FEENAME = "feename";
	private static final String FEEDESC = "feedesc";

	private static final String FEE_CREATE_SUCCESS = "fee successfully created: ";
	private FeeCategory feeCategory;
	private boolean embbeded = false;

	
	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param showPasswordFields: true the password fields are used, the user can 
	 * enter a password for the new user; false: the passwort is not used at all
	 */
	public FeeCategoryFormController(UserRequest ureq, WindowControl wControl, Translator translator) {
		super(ureq, wControl);
		this.setTranslator(translator);
		initForm(ureq);
	}	 
	
	public FeeCategoryFormController(UserRequest ureq,
			WindowControl windowControl, FeeCategory originalCategory,
			Form rootForm) {
		super(ureq, windowControl, FormBasicController.LAYOUT_DEFAULT, null, rootForm);
		this.feeCategory = originalCategory;
		initForm(ureq);	
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {						
		
		uifactory.addStaticTextElement("heading1", null, translate("new.fee.form.please.enter"), formLayout);
		feeName = uifactory.addTextElement(FEENAME, "Name", 128, "", formLayout);
		feeName.setMandatory(true);
		feeName.setDisplaySize(30);
		
		feeDesc = uifactory.addTextElement(FEEDESC, "Description", 128, "", formLayout);
		feeDesc.setMandatory(true);
		feeDesc.setDisplaySize(30);
		
		if(isEmbbeded())
			uifactory.addFormSubmitButton("save", "submit.save", formLayout);
	}	
	
	@Override
	public boolean validateFormLogic(UserRequest ureq) {
		// validate if fee does match the syntactical requirements
		if (feeName.isEmpty()) {			
			feeName.setErrorKey("new.error.loginname.empty", new String[]{});
			return false;
		}
		feeName.clearError();
		return true;
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

	public TextElement getFeeName() {
		return feeName;
	}

	public void setFeeName(TextElement feeName) {
		this.feeName = feeName;
	}

	public TextElement getFeeDesc() {
		return feeDesc;
	}

	public void setFeeDesc(TextElement feeDesc) {
		this.feeDesc = feeDesc;
	}

	public boolean isEmbbeded() {
		return embbeded;
	}

	public void setEmbbeded(boolean embbeded) {
		this.embbeded = embbeded;
	}
	
	
}
