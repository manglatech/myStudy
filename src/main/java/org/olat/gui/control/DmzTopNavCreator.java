/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.gui.control;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.creator.AutoCreator;

/**
 * 
 * <h3>Description:</h3>
 * <p>
 * Initial Date:  30 nov. 2010 <br>
 * @author srosse, srosse@frentix.com, www.frentix.com
 */
public class DmzTopNavCreator extends AutoCreator {

	private boolean impressum;
	private boolean languageSelector;
	
	@Override
	public Controller createController(UserRequest ureq, WindowControl wControl) {
		return new OlatDmzTopNavController(ureq, wControl, impressum,languageSelector);
	}


	public boolean isImpressum() {
		return impressum;
	}

	public void setImpressum(boolean impressum) {
		this.impressum = impressum;
	}


	public boolean isLanguageSelector() {
		return languageSelector;
	}


	public void setLanguageSelector(boolean languageSelector) {
		this.languageSelector = languageSelector;
	}
	
	
}
