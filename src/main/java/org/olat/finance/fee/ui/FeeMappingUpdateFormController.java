package org.olat.finance.fee.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.finance.fee.model.FeeMapping;

public class FeeMappingUpdateFormController extends FormBasicController {
	
	private OLog log = Tracing.createLoggerFor(this.getClass());
	private TextElement[] price;
	private static final String FEENAME = "feename";
	private static final String PRICE = "price";
	List<FeeMapping> feeMappings;
	
	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param showPasswordFields: true the password fields are used, the user can 
	 * enter a password for the new user; false: the passwort is not used at all
	 */
	public FeeMappingUpdateFormController(UserRequest ureq, WindowControl wControl, Translator translator, List<FeeMapping> feeMappings) {
		super(ureq, wControl);
		this.setTranslator(translator);
		this.feeMappings = feeMappings;
		initForm(ureq);
	}	 
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {						
		
		uifactory.addStaticTextElement("heading1", null, translate("update.fee.mapping.form.enter"), formLayout);
		
		price = new TextElement[this.feeMappings.size()];
		int index = 0;
		for(FeeMapping feeMapping : this.feeMappings){
			
			String value = "0";
			if(feeMapping.getPrice() != null){
				value = feeMapping.getPrice().toString();
			}
			price[index] = uifactory.addTextElement(PRICE+"_"+index, feeMapping.getFee().getName() , 10, value, formLayout);
			price[index].setMandatory(true);
			index++;
			
		}
		
		uifactory.addFormSubmitButton("save", "submit.save", formLayout);
	}	
	
	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		// validate if fee does match the syntactical requirements
		for(TextElement p : price){
			if(p.isEmpty()){
				p.setErrorKey("new.error.price.empty", new String[]{});
				return false;
			}
			p.clearError();
		}
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

	public TextElement[] getPrice() {
		return price;
	}

	public void setPrice(TextElement[] price) {
		this.price = price;
	}

	public List<FeeMapping> getFeeMappings() {
		return feeMappings;
	}

	public void setFeeMappings(List<FeeMapping> feeMappings) {
		this.feeMappings = feeMappings;
	}
	
}
