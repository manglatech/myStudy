/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/

package org.olat.finance.fee.ui;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.user.UserManager;

/**
 *  Initial Date:  Jul 31, 2003
 *  @author gnaegi
 *  
 *  Comment:  
 *  Displays a form to create a new user on the OLAT plattform
 */
public class FeeCategoryCreateController extends BasicController  {

	private FeeCategoryFormController feeFormController;
	private FeeService feeService;

	/**
	 * @param ureq
	 * @param wControl
	 */
	public FeeCategoryCreateController (UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl, Util.createPackageTranslator(FeeCategoryCreateController.class, ureq.getLocale()));
		Translator pT = UserManager.getInstance().getPropertyHandlerTranslator(getTranslator());		
		feeFormController = new FeeCategoryFormController(ureq, wControl, pT);		
		feeService = CoreSpringFactory.getImpl(FeeService.class);
		this.listenTo(feeFormController);
				
		VelocityContainer newUserVC = this.createVelocityContainer("newFeeCategory");		
		newUserVC.put("createFeeCategoryForm", feeFormController.getInitialComponent());		
		this.putInitialPanel(newUserVC);
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest, org.olat.core.gui.components.Component, org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Component source, Event event) {
		//empty		
	}
	
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == feeFormController) {
			if (event == Event.DONE_EVENT) {
				String name = feeFormController.getFeeName().getValue();
				String desc = feeFormController.getFeeDesc().getValue();
				
				FeeCategory newFeeCategory = new FeeCategoryImpl();
				newFeeCategory.setName(name);
				newFeeCategory.setDescription(desc);
				newFeeCategory.setInstituteId(getUserInst());
				
				feeService.createFeeCategory(getIdentity(), newFeeCategory);
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