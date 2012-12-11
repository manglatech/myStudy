package org.olat.finance.user.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.IntegerElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;

public class AddPaymentFormController extends FormBasicController {
		
		private OLog log = Tracing.createLoggerFor(this.getClass());
		
		private IntegerElement paymentAmount;
		private static final String PAYMENTNAME = "paymentAmount";
		
		public AddPaymentFormController(UserRequest ureq, WindowControl wControl, Translator translator) {
			super(ureq, wControl);
			this.setTranslator(translator);
			initForm(ureq);
		}	 
		
		@Override
		protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {						
			
			uifactory.addStaticTextElement("heading1", null, translate("new.add.feeform.please.enter"), formLayout);
			paymentAmount = uifactory.addIntegerElement(PAYMENTNAME, "Name", 0, formLayout);
			paymentAmount.setMandatory(true);
			paymentAmount.setDisplaySize(30);
			
			uifactory.addFormSubmitButton("save", "submit.save", formLayout);
			
		}	
		
		@Override
		protected boolean validateFormLogic(UserRequest ureq) {
			// validate if fee does match the syntactical requirements
			if (paymentAmount.isEmpty() || paymentAmount.getIntValue() == 0) {			
				paymentAmount.setErrorKey("new.error.payment.amount.empty", new String[]{});
				return false;
			}
			paymentAmount.clearError();
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

		public IntegerElement getPaymentAmount() {
			return paymentAmount;
		}

		public void setPaymentAmount(IntegerElement paymentAmount) {
			this.paymentAmount = paymentAmount;
		}
		
	}
