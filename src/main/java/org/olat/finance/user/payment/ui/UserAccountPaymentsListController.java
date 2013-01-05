package org.olat.finance.user.payment.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.table.DefaultColumnDescriptor;
import org.olat.core.gui.components.table.TableController;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.components.table.TableMultiSelectEvent;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.gui.control.generic.modal.DialogBoxUIFactory;
import org.olat.core.id.Identity;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.fee.ui.AssingFeeCategoryListController;
import org.olat.finance.user.payment.manager.UserPaymentService;
import org.olat.finance.user.payment.model.UserPaymentInfo;
import org.olat.finance.user.ui.AbstractUserAccountController;
import org.olat.finance.user.ui.AddPaymentController;

public class UserAccountPaymentsListController extends AbstractUserAccountController {

	protected static final String TABLE_ACTION_DELETE = "feeTblDelete";
	protected static final String TABLE_TRASFER_PAYMENT = "feeTblDeleteChilds";

	protected UserAccountListTDM uaPaymentModel;
	protected UserPaymentService userPaymentService = null;
	protected TableController tableC;
	protected Locale locale;
	protected CloseableModalController cmc;
	protected VelocityContainer mainVC;
	private DialogBoxController confirmRemoveResource;

	private Identity identity;
	protected AddPaymentController addPaymentContoller;

	public UserAccountPaymentsListController(UserRequest ureq,
			WindowControl wControl, Identity identity) {

		super(ureq, wControl);
		this.locale = ureq.getLocale();
		this.userPaymentService = CoreSpringFactory
				.getImpl(UserPaymentService.class);
		this.identity = identity;

		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);

		populateUserPaymentInfo();

		mainVC = createVelocityContainer("listPayments");
		initButtons(ureq);
		mainVC.put("payments", tableC.getInitialComponent());
		putInitialPanel(mainVC);

	}

	protected void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {

		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator(), true);
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.payment.fee.category.name", 0, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.payment.amount", 1, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.payment.date", 2, null, locale));
	}

	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(getNoFeeMsg());
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	protected String getNoFeeMsg() {
		return translate("msg.no.payments.found");
	}

	protected void initButtons(UserRequest ureq) {
		tableC.addMultiSelectAction("table.delete.payment", TABLE_ACTION_DELETE);
		tableC.addMultiSelectAction("table.transfer.payment", TABLE_TRASFER_PAYMENT);
	}

	protected void reloadModel() {
		populateUserPaymentInfo();
		tableC.modelChanged();
	}

	private void deletePayment(UserRequest ureq,
			List<UserPaymentInfo> selectedItems) {
		if (selectedItems.isEmpty()) {
			showWarning("msg.no.payment.selected");
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (UserPaymentInfo fee : selectedItems) {
			ids.add(fee.getKey());
		}
		userPaymentService.deletePayments(ids);
	}

	protected void doDispose() {
	}

	@SuppressWarnings("unused")
	public void event(UserRequest ureq, Component source, Event event) {
	}
	
	private AssingFeeCategoryListController<UserPaymentInfo> assignController;
	private void doSelectFeeCategory(UserRequest ureq,
			List<UserPaymentInfo> selectedItems) {
		
		removeAsListenerAndDispose(assignController);
		assignController = new AssingFeeCategoryListController<UserPaymentInfo>(ureq, getWindowControl(), selectedItems);
		listenTo(assignController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), assignController.getInitialComponent(), true,
				translate("transfer.payment.form.title"));
		cmc.activate();
		listenTo(cmc);

	}
	
	private void doTransferToFeeCategory(UserRequest ureq,
			SingleFeeCategoryChosenEvent feeCategory) {
		List<UserPaymentInfo> info = assignController.getEntities();
		userPaymentService.transferPayments(info,this.identity, feeCategory.getChosenFeeCategory());
	}
	
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<UserPaymentInfo> selectedItems = uaPaymentModel
						.getObjects(te.getSelection());
				if (TABLE_ACTION_DELETE.equals(te.getAction())) {
					String text = getTranslator().translate(
							"payment.remove.confim",
							new String[] { "FeeName", "FeeDisplayName" });
					confirmRemoveResource = activateYesNoDialog(ureq, null,
							text, this.confirmRemoveResource);
					confirmRemoveResource.setUserObject(selectedItems);
				}else if(TABLE_TRASFER_PAYMENT.equals(te.getAction())){
					doSelectFeeCategory(ureq,selectedItems);
				}
			}
		} else if (source == addPaymentContoller) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		} else if (source == confirmRemoveResource) {
			if (DialogBoxUIFactory.isYesEvent(event)) { // yes case
				List<UserPaymentInfo> selectedItems = (List<UserPaymentInfo>) confirmRemoveResource
						.getUserObject();
				deletePayment(ureq, selectedItems);
				reloadModel();
			}
		}else if (source == assignController){
			if(event instanceof SingleFeeCategoryChosenEvent){
				SingleFeeCategoryChosenEvent feeCategory = (SingleFeeCategoryChosenEvent) event;
				doTransferToFeeCategory(ureq, feeCategory);
				cmc.deactivate();
				removeAsListenerAndDispose(cmc);
				reloadModel();
			}
		}
	}
	public void populateUserPaymentInfo() {
		List<UserPaymentInfo> payments = userPaymentService
				.findUserPayments(this.identity.getKey());
		uaPaymentModel = new UserAccountListTDM(payments, locale);
		tableC.setTableDataModel(uaPaymentModel);
	}
	
	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries,
			StateEntry state) {
	}
}