package org.olat.finance.user.ui;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.util.Util;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.ui.FeeCreateController;
import org.olat.finance.user.payment.manager.UserPaymentService;
import org.olat.user.UserManager;

public class AddPaymentController extends BasicController {

	private AddPaymentFormController addPaymentFormController;
	private UserPaymentService service;
	protected FeeCategory feeCategory;
	protected Identity identity;
	
	public AddPaymentController(UserRequest ureq, WindowControl wControl,
			FeeCategory feeCategory, Identity identity) {
		
		super(ureq, wControl, Util.createPackageTranslator(FeeCreateController.class, ureq.getLocale()));
		this.feeCategory = feeCategory;
		this.identity = identity;
		
		Translator pT = UserManager.getInstance().getPropertyHandlerTranslator(getTranslator());		
		addPaymentFormController = new AddPaymentFormController(ureq, wControl, pT);		
		service = CoreSpringFactory.getImpl(UserPaymentService.class);

		this.listenTo(addPaymentFormController);
				
		VelocityContainer newUserVC = this.createVelocityContainer("addPayment");		
		newUserVC.put("addPaymentForm", addPaymentFormController.getInitialComponent());		
		this.putInitialPanel(newUserVC);
	}

	public void event(UserRequest ureq, Component source, Event event) {
	}
	
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == addPaymentFormController) {
			if (event == Event.DONE_EVENT) {
				Integer payment = addPaymentFormController.getPaymentAmount().getIntValue();
				service.addPayment(feeCategory.getKey(), identity.getKey(), payment);
				fireEvent(ureq, Event.DONE_EVENT);
			}
			else if (event == Event.CANCELLED_EVENT) {
				// workflow cancelled
				fireEvent(ureq, Event.CANCELLED_EVENT);
			}
		}
	}	

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	protected void doDispose() {
		// nothing to do
	}

}
