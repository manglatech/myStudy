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

package org.olat.finance.fee.model;

import org.olat.core.gui.control.Event;

/**
 * Initial Date: Feb 19, 2004
 * 
 * @author jeger
 * 
 *         Comment: The SingleIdentityChosenEvent has an additional field that
 *         tells which identity has been found
 */
public class SingleFeeCategoryChosenEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3127037159329612086L;
	private FeeCategory feeCategory;

	public SingleFeeCategoryChosenEvent(FeeCategory feeCategory) {
		super("FeeCategorySelected");
		this.feeCategory = feeCategory;
	}

	public FeeCategory getChosenFeeCategory() {
		return feeCategory;
	}
}
