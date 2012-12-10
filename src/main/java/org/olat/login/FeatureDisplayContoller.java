package org.olat.login;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;

public class FeatureDisplayContoller extends BasicController implements
		Controller {

	VelocityContainer mainVC;
	
	protected FeatureDisplayContoller(UserRequest uReq, WindowControl wControl) {
		super(uReq,wControl);
		mainVC = createVelocityContainer("features");
		putInitialPanel(mainVC);
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub

	}

}
