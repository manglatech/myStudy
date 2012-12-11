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

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.user.UserManager;

/**
 * Initial Date: Jul 31, 2003
 * 
 * @author gnaegi
 * 
 *         Comment: Displays a form to create a new user on the OLAT plattform
 */
public class FeeMappingController extends BasicController {

	private FeeMappingUpdateFormController feeMappingFormController;
	private FeeService feeService;

	/**
	 * @param ureq
	 * @param wControl
	 */
	public FeeMappingController(UserRequest ureq, WindowControl wControl,
			List<FeeMapping> feeMappings) {
		
		super(ureq, wControl, Util.createPackageTranslator(
				FeeCategoryCreateController.class, ureq.getLocale()));
		Translator pT = UserManager.getInstance().getPropertyHandlerTranslator(
				getTranslator());
		feeMappingFormController = new FeeMappingUpdateFormController(ureq,
				wControl, pT, feeMappings);
		feeService = CoreSpringFactory.getImpl(FeeService.class);
		this.listenTo(feeMappingFormController);

		VelocityContainer mainVC = this
				.createVelocityContainer("updateFeeMapping");
		mainVC.put("updateFeeMappingForm",
				feeMappingFormController.getInitialComponent());
		this.putInitialPanel(mainVC);
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component,
	 *      org.olat.core.gui.control.Event)
	 */
	public void event(UserRequest ureq, Component source, Event event) {
		// empty
	}

	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == feeMappingFormController) {
			if (event == Event.DONE_EVENT) {
				TextElement[] prices = feeMappingFormController.getPrice();
				List<FeeMapping> mappings = feeMappingFormController.getFeeMappings();
				int index = 0;
				for (TextElement price : prices) {
					String value = price.getValue();
					Integer updatedPrice = Integer.valueOf(value);
					FeeMapping mapping = mappings.get(index);
					mapping.setPrice(updatedPrice);
					index++;
				}
				feeService.updateFeeMapping(mappings);
			}
			fireEvent(ureq, Event.DONE_EVENT);
		} else if (event == Event.CANCELLED_EVENT) {
			// workflow cancelled
			fireEvent(ureq, Event.CANCELLED_EVENT);
		}

	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	protected void doDispose() {
		// nothing to do
	}
}