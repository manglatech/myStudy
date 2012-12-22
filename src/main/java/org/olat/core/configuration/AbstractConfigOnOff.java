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
* <p>
*/ 

package org.olat.core.configuration;

import org.olat.institute.feature.configuration.InstituteSpecificConfiguration;
import org.olat.institute.feature.manager.FC;
import org.olat.institute.feature.util.Features;



/**
 * Used for enable/disable Spring beans. Default value is 'enabled'.
 * @author Christian Guretzki
 */
public abstract class AbstractConfigOnOff implements ConfigOnOff,InstituteSpecificConfiguration {
	
	private boolean configEnabled = true;
	private Integer featureId = Features.NOT_DEFINED.getId();

	/**
	 * [used by spring]
	 * @param configEnabled
	 */
	public void setEnabled(boolean configEnabled) {
		this.configEnabled = configEnabled;
	}
	public boolean isEnabled() {
		return configEnabled;
	}
	
	/**
	 * 
	 * @return true it the extension is enabled otherwise false
	 */
	public boolean isEnabled(String instituteId) {
		return FC.isEnabled(configEnabled, instituteId, featureId);
	}
	
	public void setFeatureId(Integer featureId) {
		this.featureId = featureId;
	}

}

