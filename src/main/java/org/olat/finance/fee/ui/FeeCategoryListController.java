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
import org.olat.core.gui.components.table.TableEvent;
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
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepRunnerCallback;
import org.olat.core.gui.control.generic.wizard.StepsMainRunController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.util.event.GenericEventListener;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.fee.ui.wizard.FCCopyFeeCategory;
import org.olat.finance.fee.ui.wizard.FCCopyPreparationStep;

public class FeeCategoryListController extends BasicController implements
		GenericEventListener {

	protected static final String TABLE_ACTION_DELETE = "feeTblDelete";
	protected static final String TABLE_ACTION_DUPLICATE = "feeTblDuplicate";

	protected static final String TABLE_ACTION_DELETE_CHILD = "feeTblDeleteChilds";
	protected static final String TABLE_ACTION_ADD_CHILD = "feeTblAddChilds";
	protected static final String TABLE_ACTION_CREATE = "feeTblCreate";
	protected static final String TABLE_ENTITY_SELECT = "choose";

	protected FeeCategoryListTableDataModel feeModel;
	protected FeeCategory chosenFee = null;
	protected FeeService feeService = null;

	protected TableController tableC;
	protected Locale locale;
	protected CloseableModalController cmc;
	private Link createButton;
	protected VelocityContainer mainVC;
	protected FeeCategoryCreateController feeCategoryCreateController;
	private DialogBoxController confirmRemoveResource;

	/**
	 * @param ureq
	 * @param wControl
	 */
	public FeeCategoryListController(UserRequest ureq, WindowControl wControl) {
		this(ureq, wControl, null);
	}

	public FeeCategoryListController(UserRequest ureq, WindowControl wControl,
			FeeCategory feeCategory) {
		super(ureq, wControl);
		this.locale = ureq.getLocale();
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);

		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);

		this.chosenFee = feeCategory;
		populateFeeCategoryTable();

		mainVC = createVelocityContainer("listFeeCategory");
		initButtons(ureq);
		mainVC.put("feeCategoryList", tableC.getInitialComponent());

		putInitialPanel(mainVC);

	}

	protected void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {

		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator(),true);
		listenTo(tableC);
		addColumns();
	}

	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(getNoFeeMsg());
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	protected void initButtons(UserRequest ureq) {
		tableC.addMultiSelectAction("table.fee.category.delete", TABLE_ACTION_DELETE);
		tableC.addMultiSelectAction("table.fee.category.duplicate", TABLE_ACTION_DUPLICATE);

		createButton = LinkFactory.createButton("create.category.fee", mainVC, this);
		createButton.setElementCssClass("o_sel_group_create");

	}

	protected void initLinkButtons(UserRequest ureq) {
	}

	protected void reloadModel() {
		populateFeeCategoryTable();
		tableC.modelChanged();
	}

	private void deleteFees(UserRequest ureq, List<FeeCategory> selectedItems) {
		if (selectedItems.isEmpty()) {
			showWarning("msg.alleastone.editable.group");
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (FeeCategory feeCategory : selectedItems) {
			ids.add(feeCategory.getKey());
		}
		feeService.deleteFeeCategory(ids);
	}

	protected void doDispose() {
	}

	@SuppressWarnings("unused")
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == createButton) {
			doCreate(ureq, getWindowControl());
		}
	}

	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<FeeCategory> selectedItems = feeModel.getObjects(te
						.getSelection());
				if (TABLE_ACTION_DELETE.equals(te.getAction())) {
					String text = getTranslator().translate("category.remove", new String[] { "EntityName", "DisplayName" });
					confirmRemoveResource = activateYesNoDialog(ureq, null, text, this.confirmRemoveResource);
					confirmRemoveResource.setUserObject(selectedItems);
				} else if (TABLE_ACTION_DUPLICATE.equals(te.getAction())) {
					doCopy(ureq, selectedItems);
				}
			} else if (event instanceof TableEvent) {
				TableEvent te = (TableEvent) event;
				String action = te.getActionId();
				if (TABLE_ENTITY_SELECT.equalsIgnoreCase(action)) {
					chosenFee = (FeeCategory) feeModel.getObject(te.getRowId());
					doViewFeeCategory(ureq, getWindowControl());
				}
			}
		} else if (source == feeCategoryCreateController) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}else if (source == confirmRemoveResource) {
			if (DialogBoxUIFactory.isYesEvent(event)) { // yes case
				List<FeeCategory> selectedItems = (List<FeeCategory>) confirmRemoveResource.getUserObject();
				deleteFees(ureq, selectedItems);
				reloadModel();
				//fireEvent(ureq, Event.CHANGED_EVENT);
			}
		} else if (source == feeCategoryWizard) { 
			if(event == Event.CANCELLED_EVENT || event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
				getWindowControl().pop();
				removeAsListenerAndDispose(feeCategoryWizard);
				feeCategoryWizard = null;
				if(event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
					reloadModel();
				}
			}
		}
		
		
	}

	private StepsMainRunController feeCategoryWizard;

	private void doCopy(UserRequest ureq, List<FeeCategory> items) {
		removeAsListenerAndDispose(feeCategoryWizard);
		if(items == null || items.isEmpty()) return;
		boolean enableFeeCopy = true;
		
		Step start = new FCCopyPreparationStep(ureq, items, enableFeeCopy);
		
		StepRunnerCallback finish = new StepRunnerCallback() {
			@Override
			public Step execute(UserRequest ureq, WindowControl wControl, StepsRunContext runContext) {
				@SuppressWarnings("unchecked")
				List<FCCopyFeeCategory> copies = (List<FCCopyFeeCategory>)runContext.get("categoryCopy");
				if(copies != null && !copies.isEmpty()) {
					boolean copyFee = convertToBoolean(runContext, "copyFee");
					for(FCCopyFeeCategory copy:copies) {
						feeService.copyFeeCategory(getIdentity(),copy.getOriginal(), copy.getName(), copy.getDescription(),copyFee);
					}
					return StepsMainRunController.DONE_MODIFIED;
				} else {
					return StepsMainRunController.DONE_UNCHANGED;
				}
			}
		};
		
		feeCategoryWizard = new StepsMainRunController(ureq, getWindowControl(), start, finish, null, translate("copy.group"), "o_sel_group_copy_wizard");
		listenTo(feeCategoryWizard);
		getWindowControl().pushAsModalDialog(feeCategoryWizard.getInitialComponent());
	}
	private boolean convertToBoolean(StepsRunContext runContext, String key) {
		Object obj = runContext.get(key);
		if(obj instanceof Boolean) {
			return ((Boolean)obj).booleanValue();
		} else {
			return false;
		}
	}
	public void event(Event event) {
		System.out.println("Event Called");
	}

	protected void doCreate(UserRequest ureq, WindowControl wControl) {

		removeAsListenerAndDispose(feeCategoryCreateController);
		feeCategoryCreateController = new FeeCategoryCreateController(ureq,
				wControl);
		listenTo(feeCategoryCreateController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"),
				feeCategoryCreateController.getInitialComponent(), true,
				translate("create.form.title"));
		cmc.activate();
		listenTo(cmc);
	}

	public FeeCategoryListTableDataModel getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(FeeCategoryListTableDataModel feeModel) {
		this.feeModel = feeModel;
	}

	protected void doViewFeeCategory(UserRequest ureq,
			WindowControl windowControl) {
		fireEvent(ureq, new SingleFeeCategoryChosenEvent(this.chosenFee));
	}

	public void populateFeeCategoryTable() {
		List<FeeCategory> fees = feeService.findAllFeeCategory(getIdentity());
		feeModel = new FeeCategoryListTableDataModel(fees, locale);
		tableC.setTableDataModel(feeModel);
	}

	protected void addColumns() {
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.template.name", 0, TABLE_ENTITY_SELECT, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.template.desc", 1, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.template.due.date", 2, null, locale));
	}

	protected String getNoFeeMsg() {
		return translate("fee.template.nofees");
	}

	protected int getTotalColumns() {
		return 2;
	}

}
