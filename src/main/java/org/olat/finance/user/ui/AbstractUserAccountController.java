package org.olat.finance.user.ui;

import org.olat.basesecurity.events.SingleIdentityChosenEvent;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.Identity;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;

public abstract class AbstractUserAccountController extends BasicController
		implements Activateable2 {

	protected AddPaymentController addPayment;
	protected CloseableModalController cmc;
	protected static final String TABLE_ACTION_PAYMENT_DETAILS = "uaTblPaymentDetails";
	protected static final String TABLE_ACTION_TEMPLLATE_DETAILS = "uaTblTemplateDetails";
	
	protected AbstractUserAccountController(UserRequest ureq,
			WindowControl wControl) {
		super(ureq, wControl);
	}
	
	protected void doViewFeeCategoryDetails(UserRequest ureq, FeeCategory category){
		fireEvent(ureq, new SingleFeeCategoryChosenEvent(category));
	}
	
	protected void doViewAccountDetails(UserRequest ureq, Identity identity) {
		SingleIdentityChosenEvent identityEvent = new SingleIdentityChosenEvent(identity);
		fireEvent(ureq, identityEvent);
	}
	
	protected void doPaymentForUser(UserRequest ureq, FeeCategory feeCategory, Identity identity) {

		removeAsListenerAndDispose(addPayment);
		addPayment = new AddPaymentController(ureq, getWindowControl(), feeCategory,identity);
		listenTo(addPayment);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), addPayment.getInitialComponent(), true,
				translate("add.payment.form.title"));
		cmc.activate();
		listenTo(cmc);

	}

}
