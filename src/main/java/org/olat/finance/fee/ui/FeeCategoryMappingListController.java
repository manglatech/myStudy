package org.olat.finance.fee.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
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
import org.olat.core.util.event.GenericEventListener;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.Fee;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeCategoryImpl;
import org.olat.finance.fee.model.FeeMapping;
import org.olat.finance.fee.model.MultipleFeeSelectEvent;

public class FeeCategoryMappingListController extends BasicController implements
		GenericEventListener {

	private static final String TABLE_ACTION_DELETE = "feeMappingTblDelete";
	private static final String TABLE_ACTION_UPDATE = "feeMappingTblUpdate";
	private FeeCategory feeCategory;
	private Locale locale;
	private FeeService feeService;
	private VelocityContainer myContent;
	private TableController tableC;
	private Link createButton;
	private FeeMappingListTableDataModel feeMappingModel;
	private FeeListController addFeeController;
	private FeeMappingController updateFeeMappingController;
	private CloseableModalController cmc;
	
	protected FeeCategoryMappingListController(UserRequest ureq,
			WindowControl wControl, FeeCategory feeCategory) {
		super(ureq, wControl);
		this.feeCategory = feeCategory;
		
		this.locale = ureq.getLocale();
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);
		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);
		populateMappingTable();
		
		myContent = createVelocityContainer("listFeeCategoryMapping");
		initButtons(ureq);
		myContent.put("feeList", tableC.getInitialComponent());

		putInitialPanel(myContent);
		
	}

	private void initButtons(UserRequest ureq) {
		tableC.addMultiSelectAction("table.delete.fee.category.mapping", TABLE_ACTION_DELETE);
		tableC.addMultiSelectAction("table.update.fee.mapping", TABLE_ACTION_UPDATE);

		createButton = LinkFactory.createButton("add.fee.category.mapping", myContent, this);
		createButton.setElementCssClass("o_sel_group_create");
		
	}

	private void populateMappingTable() {
		
		List<FeeMapping> feeMappings = feeService.findFeeMapping(feeCategory);
		feeMappingModel = new FeeMappingListTableDataModel(feeMappings, locale);
		tableC.setTableDataModel(feeMappingModel);
		
	}

	private void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {
		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator());
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.mapping.name", 0, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.mapping.desc", 1, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.mapping.price", 2, null, locale));
	}

	private TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(translate("fee.nofees"));
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	@Override
	public void event(Event event) {
		// TODO Auto-generated method stub

	}
	public void event(UserRequest ureq, Controller source, Event event) {
		if(source == addFeeController){
			if (event instanceof MultipleFeeSelectEvent) {
				MultipleFeeSelectEvent te = (MultipleFeeSelectEvent) event;
				List<Fee> selectedFees = te.getChosenFees();
				List<Long> feeIds = new ArrayList<Long>();
				for(Fee fee : selectedFees){
					feeIds.add(fee.getKey());
				}
				feeService.addFeeMapping(this.feeCategory.getKey(), feeIds);
			}
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}else if(source == tableC){
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<FeeMapping> selectedItems = feeMappingModel.getObjects(te
						.getSelection());
				if (TABLE_ACTION_DELETE.equals(te.getAction())) {
					deleteFeeMappings(ureq, selectedItems);
					reloadModel();
				}else if(TABLE_ACTION_UPDATE.equals(te.getAction())){
					updateFeeMappings(ureq,selectedItems);
				}
			}
		}else if(source == updateFeeMappingController){
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}
	}
	
	private void updateFeeMappings(UserRequest ureq,
			List<FeeMapping> selectedItems) {
		
		removeAsListenerAndDispose(updateFeeMappingController);
		updateFeeMappingController = new FeeMappingController(ureq, getWindowControl(), selectedItems);
		listenTo(updateFeeMappingController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), updateFeeMappingController.getInitialComponent(), true,
				translate("update.fee.mapping.title"));
		
		cmc.activate();
		listenTo(cmc);
	}

	private void deleteFeeMappings(UserRequest ureq,
			List<FeeMapping> selectedItems) {
		if (selectedItems.isEmpty()) {
			showWarning("msg.delete.fee.mappings");
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (FeeMapping feeMapping : selectedItems) {
			ids.add(feeMapping.getKey());
		}
		feeService.removeFeeMappings(ids);
		
	}

	protected void reloadModel() {
		populateMappingTable();
		tableC.modelChanged();
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == createButton) {
			removeAsListenerAndDispose(addFeeController);
			addFeeController = new AddFeeListController(ureq, getWindowControl(),this.feeCategory);
			listenTo(addFeeController);

			cmc = new CloseableModalController(getWindowControl(),
					translate("close"), addFeeController.getInitialComponent(), true,
					translate("add.fee.mapping.title"));
			cmc.activate();
			listenTo(cmc);
		}
	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub

	}

}
