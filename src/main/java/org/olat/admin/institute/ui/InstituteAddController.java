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

package org.olat.admin.institute.ui;

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
import org.olat.institute.manager.InstituteService;
import org.olat.institute.model.Institute;
import org.olat.user.UserManager;

/**
 *  Initial Date:  Jul 31, 2003
 *  @author gnaegi
 *  
 *  Comment:  
 *  Displays a form to create a new user on the OLAT plattform
 */
public class InstituteAddController extends BasicController  {

	private InstituteAddForm instituteFormController;
	private InstituteService instituteService;
	private Institute institute;
	
	/**
	 * @param ureq
	 * @param wControl
	 */
	public InstituteAddController (UserRequest ureq, WindowControl wControl, Institute view) {
		super(ureq, wControl, Util.createPackageTranslator(InstituteAddForm.class, ureq.getLocale()));
		Translator pT = UserManager.getInstance().getPropertyHandlerTranslator(getTranslator());		
		instituteFormController = new InstituteAddForm(ureq, wControl, pT);		
		instituteService = CoreSpringFactory.getImpl(InstituteService.class);
		
		this.institute = view;
		if(this.institute != null){
			setDefaultValue();
		}
		
		this.listenTo(instituteFormController);
				
		VelocityContainer newUserVC = this.createVelocityContainer("newInstitute");		
		newUserVC.put("createInstituteForm", instituteFormController.getInitialComponent());		
		this.putInitialPanel(newUserVC);
	}

	private void setDefaultValue() {
		instituteFormController.getName().setValue(institute.getName());
		instituteFormController.getNumberOfUsers().setValue(institute.getInstituteId());
		instituteFormController.getInstituteId().setValue(institute.getInstituteId());
		instituteFormController.getInstituteId().setEnabled(false);
	}

	public void event(UserRequest ureq, Component source, Event event) {
	}
	
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == instituteFormController) {
			if (event == Event.DONE_EVENT) {
				
				String name = instituteFormController.getName().getValue();
				String id = instituteFormController.getInstituteId().getValue();
				String numberOfUsers = instituteFormController.getNumberOfUsers().getValue();
				
				if(this.institute != null){
					instituteService.updateInstitute(institute.getKey(), name, numberOfUsers);
				}else{
					instituteService.createInstitute(id, name, numberOfUsers);
				}
				fireEvent(ureq, Event.DONE_EVENT);
			}
			else if (event == Event.CANCELLED_EVENT) {
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