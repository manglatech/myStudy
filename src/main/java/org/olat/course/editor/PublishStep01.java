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
package org.olat.course.editor;

import java.util.HashMap;
import java.util.Map;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.gui.translator.PackageTranslator;
import org.olat.core.util.Util;
import org.olat.course.ICourse;
import org.olat.institute.feature.FeatureModule;
import org.olat.repository.PropPupForm;
import org.olat.repository.RepositoryEntry;

/**
 * Description:<br>
 * TODO: patrickb Class Description for PublishStep01
 * 
 * <P>
 * Initial Date:  21.01.2008 <br>
 * @author patrickb
 */
class PublishStep01 extends BasicStep {

	private PrevNextFinishConfig prevNextConfig;
	private boolean hasPublishableChanges;
	private boolean enabled = false;
	
	public PublishStep01(UserRequest ureq, ICourse course, boolean hasPublishableChanges, boolean hasCatalog) {
		super(ureq);
		setI18nTitleAndDescr("publish.access.header", null);
		
		if(enabled){
			//VCRP-3: add catalog entry in publish wizard
			this.hasPublishableChanges = hasPublishableChanges;
			setNextStep(new PublishStepCatalog(ureq, course, hasPublishableChanges));
			if(hasCatalog){
				prevNextConfig = PrevNextFinishConfig.BACK_NEXT_FINISH;
			}else{
				prevNextConfig = PrevNextFinishConfig.BACK_NEXT;
			}
		}else{
			if(hasPublishableChanges) {
				setNextStep(new PublishStep00a(ureq));
				prevNextConfig = PrevNextFinishConfig.BACK_NEXT_FINISH;
			} else {
				setNextStep(Step.NOSTEP);
				prevNextConfig = PrevNextFinishConfig.BACK_FINISH;
			}
		}
	}

	/**
	 * @see org.olat.core.gui.control.generic.wizard.BasicStep#getInitialPrevNextFinishConfig()
	 */
	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		//can go back and finish immediately
		return prevNextConfig;
	}

	/**
	 * @see org.olat.core.gui.control.generic.wizard.BasicStep#getStepController(org.olat.core.gui.UserRequest, org.olat.core.gui.control.WindowControl, org.olat.core.gui.control.generic.wizard.StepsRunContext, org.olat.core.gui.components.form.flexible.impl.Form)
	 */
	@Override
	public StepFormController getStepController(UserRequest ureq, WindowControl wControl, StepsRunContext stepsRunContext, Form form) {
		return new PublishStep01AccessForm(ureq, wControl, form, stepsRunContext, hasPublishableChanges);
	}

	class PublishStep01AccessForm extends StepFormBasicController {

		private SingleSelection accessSelbox;
		private String selectedAccess;

		PublishStep01AccessForm(UserRequest ureq, WindowControl control, Form rootForm, StepsRunContext runContext, boolean hasPublishableChanges2) {
			super(ureq, control, rootForm, runContext, LAYOUT_VERTICAL, null);
			selectedAccess = (String) getFromRunContext("selectedCourseAccess");
			initForm(ureq);
		}

		@Override
		protected void doDispose() {
			// TODO Auto-generated method stub			
		}

		@Override
		protected void formOK(UserRequest ureq) {
			String newAccess = accessSelbox.getKey(accessSelbox.getSelected());
			if(!selectedAccess.equals(newAccess)){
				//only change if access was changed
				addToRunContext("changedaccess", newAccess);
			}

			fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
		}

		@Override
		@SuppressWarnings("unused")
		protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
			PackageTranslator pt = (PackageTranslator) Util.createPackageTranslator(PropPupForm.class, getLocale(), getTranslator());
			
			FormItemContainer fic = FormLayoutContainer.createCustomFormLayout("access", pt, this.velocity_root+"/publish_courseaccess.html");
			formLayout.add(fic);
			
			Map<String, String> map = new HashMap<String, String>();
			if(FeatureModule.ONLY_OWNER_ACCESS){
				map.put("" + RepositoryEntry.ACC_OWNERS, pt.translate("cif.access.owners"));
			}
			if(FeatureModule.OWNER_AND_OTHER_AUTHOR_ACCESS){
				map.put("" + RepositoryEntry.ACC_OWNERS_AUTHORS, pt.translate("cif.access.owners_authors"));
			}
			if(FeatureModule.ALL_REGISTER_USER_ACCESS){
				map.put("" + RepositoryEntry.ACC_USERS, pt.translate("cif.access.users"));
			}
			if(FeatureModule.ALL_REGISTER_AND_GUEST_USER_ACCESS){
				map.put("" + RepositoryEntry.ACC_USERS_GUESTS, pt.translate("cif.access.users_guests"));
			}
			if(FeatureModule.MEMBER_ONLY_ACCESS){
				map.put(RepositoryEntry.MEMBERS_ONLY, pt.translate("cif.access.membersonly"));
			}
			
			String[] keys = new String[map.size()];
			String[] values = new String[map.size()];
			
			int counter = 0;
			for(String key : map.keySet()){
				keys[counter] = key;
				values[counter] = map.get(key);
				counter++;
			}
			
			/*
			String[] keys = new String[] {
					"" + RepositoryEntry.ACC_OWNERS,
					//"" + RepositoryEntry.ACC_OWNERS_AUTHORS,
					//"" + RepositoryEntry.ACC_USERS,
					//"" + RepositoryEntry.ACC_USERS_GUESTS,
					RepositoryEntry.MEMBERS_ONLY//fxdiff VCRP-1,2: access control of resources
				};
			
			
			String[] values = new String[] {
				pt.translate("cif.access.owners"),
				//pt.translate("cif.access.owners_authors"),
				//pt.translate("cif.access.users"),
				//pt.translate("cif.access.users_guests"),
				pt.translate("cif.access.membersonly"),//fxdiff VCRP-1,2: access control of resources
			};
			*/
			
			//use the addDropDownSingleselect method with null as label i18n - key, because there is no label to set. OLAT-3682
			accessSelbox = uifactory.addDropdownSingleselect("accessBox",null, fic, keys, values, null);
			accessSelbox.select(selectedAccess, true);
			
		}
		
	}

	
}
