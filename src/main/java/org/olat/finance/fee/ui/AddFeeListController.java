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

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.table.TableMultiSelectEvent;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.MultipleFeeSelectEvent;

/**
 * Description: Lists all notes of a certain user. The user may choose or delete
 * a note. Works togheter with the NoteController.
 * 
 * @author Alexander Schneider
 */
public class AddFeeListController extends FeeListController{

	protected static final String TABLE_ACTION_ADD_CHILD = "feeTblAddChilds";
	private FeeCategory feeCategory;
	
	public AddFeeListController(UserRequest ureq, WindowControl wControl, FeeCategory feeCategory) {
		super(ureq, wControl);
		this.feeCategory = feeCategory;
		populateFeeTable();
	}
	@Override
	protected void initButtons(UserRequest ureq) {
		tableC.addMultiSelectAction("table.add.fee.mapping", TABLE_ACTION_ADD_CHILD);
	}
	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<Fee> selectedItems = feeModel
						.getObjects(te.getSelection());
				if (TABLE_ACTION_ADD_CHILD.equals(te.getAction())) {
					fireEvent(ureq, new MultipleFeeSelectEvent(selectedItems));
				} 
			} 
		}
	}
	public void populateFeeTable() {
		
		if(this.feeCategory == null)
			return;
		
		List<Fee> fees = feeService.findFeeByCategory(feeCategory.getKey(),false);
		feeModel = new FeeListTableDataModel(fees, locale);
		tableC.setTableDataModel(feeModel);
	}
}