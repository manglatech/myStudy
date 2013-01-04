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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.table.DefaultColumnDescriptor;
import org.olat.core.gui.components.table.TableController;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.components.table.TableMultiSelectEvent;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.gui.control.generic.modal.DialogBoxUIFactory;
import org.olat.core.util.event.GenericEventListener;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.MultipleFeeSelectEvent;

/**
 * Description: Lists all notes of a certain user. The user may choose or delete
 * a note. Works togheter with the NoteController.
 * 
 * @author Alexander Schneider
 */
public class FeeListController extends BasicController implements
		GenericEventListener {

	protected static final String TABLE_ACTION_DELETE = "feeTblDelete";
	protected static final String TABLE_ACTION_DELETE_CHILD = "feeTblDeleteChilds";
	protected static final String TABLE_ACTION_ADD_CHILD = "feeTblAddChilds";
	protected static final String TABLE_ACTION_CREATE = "feeTblCreate";
	protected static final String TABLE_ENTITY_SELECT = "choose";

	protected FeeListTableDataModel feeModel;
	protected Fee chosenFee = null;
	protected FeeService feeService = null;

	protected TableController tableC;
	protected Locale locale;
	protected CloseableModalController cmc;
	private Link createButton;
	protected VelocityContainer mainVC;
	protected FeeCreateController feeCreate;
	private DialogBoxController confirmRemoveResource;
	
	/**
	 * @param ureq
	 * @param wControl
	 */
	public FeeListController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		this.locale = ureq.getLocale();
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);

		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);
		
		populateFeeTable();

		mainVC = createVelocityContainer("listFee");
		initButtons(ureq);
		mainVC.put("feeList", tableC.getInitialComponent());

		putInitialPanel(mainVC);
	}

	protected void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {

		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator(),true);
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.name", 0, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.desc", 1, null, locale));
	}

	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(getNoFeeMsg());
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	protected String getNoFeeMsg() {
		return translate("fee.nofees");
	}

	protected void initButtons(UserRequest ureq) {
		tableC.addMultiSelectAction("table.delete.fee", TABLE_ACTION_DELETE);
		createButton = LinkFactory.createButton("create.fee", mainVC, this);
		createButton.setElementCssClass("o_sel_group_create");
	}
	
	protected void reloadModel() {
		populateFeeTable();
		tableC.modelChanged();
	}

	private void deleteFees(UserRequest ureq, List<Fee> selectedItems) {
		if (selectedItems.isEmpty()) {
			showWarning("msg.alleastone.editable.group");
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (Fee fee : selectedItems) {
			ids.add(fee.getKey());
		}
		feeService.deleteFee(ids);
	}

	protected void doDispose() {
	}

	@SuppressWarnings("unused")
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == createButton) {
			
			removeAsListenerAndDispose(feeCreate);
			feeCreate = new FeeCreateController(ureq, getWindowControl());
			listenTo(feeCreate);

			cmc = new CloseableModalController(getWindowControl(),
					translate("close"), feeCreate.getInitialComponent(), true,
					translate("create.form.title"));
			cmc.activate();
			listenTo(cmc);
			
		}
	}

	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<Fee> selectedItems = feeModel
						.getObjects(te.getSelection());
				if (TABLE_ACTION_DELETE.equals(te.getAction())) {
					String text = getTranslator().translate("resource.fee.remove", new String[] { "FeeName", "FeeDisplayName" });
					confirmRemoveResource = activateYesNoDialog(ureq, null, text, this.confirmRemoveResource);
					confirmRemoveResource.setUserObject(selectedItems);
				} else if (TABLE_ACTION_ADD_CHILD.equals(te.getAction())) {
					fireEvent(ureq, new MultipleFeeSelectEvent(selectedItems));
				}
			} 
		} else if (source == feeCreate) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}else if (source == confirmRemoveResource) {
			if (DialogBoxUIFactory.isYesEvent(event)) { // yes case
				List<Fee> selectedItems = (List<Fee>) confirmRemoveResource.getUserObject();
				deleteFees(ureq, selectedItems);
				reloadModel();
			}
		}
		
	}

	public void event(Event event) {
		System.out.println("Event Called");
	}

	
	public FeeListTableDataModel getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(FeeListTableDataModel feeModel) {
		this.feeModel = feeModel;
	}

	public void populateFeeTable() {
		List<Fee> fees = feeService.findAllFee(getIdentity());
		feeModel = new FeeListTableDataModel(fees, locale);
		tableC.setTableDataModel(feeModel);
	}
}