package org.olat.finance.user.ui;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.olat.basesecurity.BaseSecurityManager;
import org.olat.basesecurity.events.SingleIdentityChosenEvent;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.table.DefaultColumnDescriptor;
import org.olat.core.gui.components.table.Table;
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
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.Identity;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.course.member.MemberListController;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.user.manager.UserAccountService;
import org.olat.finance.user.model.UserAccountView;
import org.olat.finance.user.payment.ui.UserAccountPaymentsListController;

public class SelectUserAccountController extends BasicController implements
		Activateable2 {

	private final UserAccountSearchController searchController;

	protected static final String USER_PROPS_ID = MemberListController.class
			.getCanonicalName();

	protected static final String TABLE_ACTION_PAY_IN_FULL = "uaTblPayInFull";
	protected static final String TABLE_ACTION_ASSIGN_TEMPLATE = "uaTblAssignFee";
	protected static final String TABLE_ACTION_EMAIL_INVOICE = "uaTblEmailInvoice";
	protected static final String TABLE_ACTION_MAKE_PAYMENT = "bgTblMakePayment";
	protected static final String TABLE_ACTION_PAYMENT_DETAILS = "uaTblPaymentDetails";
	protected static final String TABLE_ACTION_TEMPLLATE_DETAILS = "uaTblTemplateDetails";

	
	protected final UserAccountSearchDataModel userAccountSearchListModel;
	protected final TableController userAccountListCtr;
	protected final VelocityContainer mainVC;
	protected Locale locale;
	private UserAccountService userAccountService;
	protected UserAccountSearchParams lastSearchParams;

	private AddPaymentController addPayment;
	protected CloseableModalController cmc;
	private AssingFeeCategoryListController assignController;
	protected UserAccountDetailController userAccountDetailContoller;
	
	//TODO: Remove this:
	FeeService feeService; 
	
	public SelectUserAccountController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);

		this.userAccountService = CoreSpringFactory.getImpl(UserAccountService.class);
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);

		this.locale = ureq.getLocale();
		mainVC = createVelocityContainer("user_account_list_search");

		// table
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig
				.setPreferencesOffered(true, this.getClass().getSimpleName());
		tableConfig.setTableEmptyMessage(translate("nomembers"));

		userAccountListCtr = new TableController(tableConfig, ureq,
				getWindowControl(), getTranslator(), true);
		listenTo(userAccountListCtr);
		initColumns();

		userAccountSearchListModel = new UserAccountSearchDataModel(null);

		userAccountListCtr.setTableDataModel(userAccountSearchListModel);
		userAccountListCtr.setMultiSelect(true);

		mainVC.put("userAccountList", userAccountListCtr.getInitialComponent());
		
		// search controller
		searchController = new UserAccountSearchController(ureq, wControl);
		listenTo(searchController);
		mainVC.put("search", searchController.getInitialComponent());

		initButtons(ureq);
		putInitialPanel(mainVC);
		
	}
	
	protected void initButtons(UserRequest ureq) {
		userAccountListCtr.setMultiSelect(true);
		userAccountListCtr.addMultiSelectAction("Pay In Full", TABLE_ACTION_PAY_IN_FULL);
		userAccountListCtr.addMultiSelectAction("Email Invoice", TABLE_ACTION_EMAIL_INVOICE);
	}
	
	protected void initColumns() {
		
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.name", 0, TABLE_ACTION_PAYMENT_DETAILS, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.email", 1, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.group", 2, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.template", 3, TABLE_ACTION_TEMPLLATE_DETAILS, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.total.amount", 4, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.amount.paid", 5, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.amount.remaining", 6, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.paid.status", 7, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.pay", 8, TABLE_ACTION_MAKE_PAYMENT, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.assign", 9, TABLE_ACTION_ASSIGN_TEMPLATE, locale));
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == searchController) {
			if (event instanceof UserAccountSearchEvent) {
				doSearch(ureq, (UserAccountSearchEvent) event);
			}
		}
		if (source == userAccountListCtr) {
			
			if (event.getCommand().equals(Table.COMMANDLINK_ROWACTION_CLICKED)) {
				
				TableEvent te = (TableEvent) event;
				String actionid = te.getActionId();
				
				UserAccountView view = userAccountSearchListModel.getObject(te.getRowId());
				if (actionid.equals(TABLE_ACTION_MAKE_PAYMENT)) {
					doPaymentForUser(ureq, view);
				}else if(actionid.equals(TABLE_ACTION_ASSIGN_TEMPLATE)){
					doViewFee(ureq, view);
				}else if(actionid.equals(TABLE_ACTION_PAYMENT_DETAILS)){
					doViewAccountDetails(ureq,view);
				}else if(actionid.equals(TABLE_ACTION_TEMPLLATE_DETAILS)){
					doViewFeeCategoryDetails(ureq, view);
				}
			} else if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent)event;
				List<UserAccountView> selectedItems = userAccountSearchListModel.getObjects(te.getSelection());
				if(TABLE_ACTION_EMAIL_INVOICE.equals(te.getAction())) {
					for(UserAccountView view : selectedItems){
						String userName = view.getName();
						showWarning("Email Invoice To User "+userName);
					}
				} else if(TABLE_ACTION_PAY_IN_FULL.equals(te.getAction())) {
					doPayInFull(selectedItems);
				}
			}
		} else if (source == addPayment) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}else if (source == assignController){
			if(event instanceof SingleFeeCategoryChosenEvent){
				SingleFeeCategoryChosenEvent feeCategory = (SingleFeeCategoryChosenEvent) event;
				doAssignFee(ureq, feeCategory);
				cmc.deactivate();
				removeAsListenerAndDispose(cmc);
				reloadModel();
			}
		}

		super.event(ureq, source, event);
	}
	
	protected UserAccountDetailController controller;
	private void doViewAccountDetails(UserRequest ureq, UserAccountView view) {
		
		Long identityId = view.getIdentityId();
		Identity identity = BaseSecurityManager.getInstance().loadIdentityByKey(identityId);
		SingleIdentityChosenEvent identityEvent = new SingleIdentityChosenEvent(identity);
		fireEvent(ureq, identityEvent);

	}
	private void doViewFeeCategoryDetails(UserRequest ureq, UserAccountView view){
		FeeCategory feeCategory = feeService.findFeeCategoryById(view.getTemplateId());
		fireEvent(ureq, new SingleFeeCategoryChosenEvent(feeCategory));
	}

	protected void doViewFee(UserRequest ureq, UserAccountView view) {
		removeAsListenerAndDispose(assignController);
		assignController = new AssingFeeCategoryListController(ureq, getWindowControl(), view);
		listenTo(assignController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), assignController.getInitialComponent(), true,
				translate("add.payment.form.title"));
		cmc.activate();
		listenTo(cmc);
		
	}
	
	protected void doAssignFee(UserRequest ureq, SingleFeeCategoryChosenEvent feeCategory) {
		FeeCategory category = feeCategory.getChosenFeeCategory();
		UserAccountView view = assignController.getView();
		userAccountService.assingFeeCategory(category,view);
	}

	private void reloadModel() {
		updateTableModel(lastSearchParams);
		userAccountListCtr.modelChanged();
	}
	
	private void doPaymentForUser(UserRequest ureq, UserAccountView view) {
		
		removeAsListenerAndDispose(addPayment);
		addPayment = new AddPaymentController(ureq, getWindowControl(), view);
		listenTo(addPayment);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), addPayment.getInitialComponent(), true,
				translate("add.payment.form.title"));
		cmc.activate();
		listenTo(cmc);
		
	}


	private void doPayInFull(List<UserAccountView> selectedItems) {
		userAccountService.markAccountAsPaidInFull(selectedItems);
		reloadModel();
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries,
			StateEntry state) {
		if (state instanceof UserAccountSearchEvent) {
			searchController.activate(ureq, entries, state);
			doSearch(ureq, (UserAccountSearchEvent) state);
		}
	}

	protected void updateSearch(UserRequest ureq) {
		doSearch(ureq, null);
	}

	private void doSearch(UserRequest ureq, UserAccountSearchEvent event) {
		long start = isLogDebugEnabled() ? System.currentTimeMillis() : 0;

		search(event);
		userAccountListCtr.modelChanged();

		if (isLogDebugEnabled()) {
			logDebug("User Account search takes (ms): "
					+ (System.currentTimeMillis() - start), null);
		}
	}

	private void search(UserAccountSearchEvent event) {
		if (event == null) {
			UserAccountSearchParams params = new UserAccountSearchParams();
			params.setInstituteId(getUserInst());
			updateTableModel(params);
		} else {
			UserAccountSearchParams params = event
					.convertToSearchBusinessGroupParams(getIdentity());
			updateTableModel(params);
		}
	}

	private void updateTableModel(UserAccountSearchParams params) {
		if(params == null){
			userAccountSearchListModel.setEntries(Collections.<UserAccountView>emptyList());
		}else{
			List<UserAccountView> views = userAccountService.searchUserAccountSummary(params);
			userAccountSearchListModel.setEntries(views);
		}
		lastSearchParams = params;
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		
	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub
		
	}
}
