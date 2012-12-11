package org.olat.finance.fee.ui;

import java.util.Locale;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.util.event.GenericEventListener;
import org.olat.finance.fee.model.FeeCategory;

public class FeeCategoryViewContoller extends BasicController implements
GenericEventListener{
	
	protected Locale locale;
	protected VelocityContainer mainVC;
	private FeeCategory chosenFeeCategory;
	
	public FeeCategoryViewContoller(UserRequest ureq, WindowControl wControl,FeeCategory feeCategory) {
		super(ureq, wControl);
		this.locale = ureq.getLocale();
		this.chosenFeeCategory = feeCategory;
		
		mainVC = createVelocityContainer("viewFeeCategory");
		initContent();
		putInitialPanel(mainVC);
		
	}
	private void initContent() {
		mainVC.contextPut("name", this.chosenFeeCategory.getName());
		mainVC.contextPut("description", this.chosenFeeCategory.getDescription());
	}
	@Override
	public void event(Event event) {
		
	}
	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
	}

	@Override
	protected void doDispose() {
		
	}
	public void setBackButtonEnabled(boolean backButtonEnabled) {
		if (mainVC != null)
			mainVC.contextPut("showButton", Boolean.valueOf(backButtonEnabled));
	}

}
