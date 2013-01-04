package org.olat.finance.user.ui;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.table.ColumnDescriptor;
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
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.course.member.MemberListController;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.user.manager.UserAccountService;
import org.olat.finance.user.model.UserAccountView;

public class SelectUserAccountController extends AbstractUserAccountController {

	private final UserAccountSearchController searchController;

	protected static final String USER_PROPS_ID = MemberListController.class
			.getCanonicalName();

	protected static final String TABLE_ACTION_PAY_IN_FULL = "uaTblPayInFull";
	protected static final String TABLE_ACTION_OPEN_ACCOUNT = "uaTblOpenAccount";
	protected static final String TABLE_ACTION_ASSIGN_TEMPLATE = "uaTblAssignFee";
	protected static final String TABLE_ACTION_EMAIL_INVOICE = "uaTblEmailInvoice";
	protected static final String TABLE_ACTION_MAKE_PAYMENT = "bgTblMakePayment";
	

	
	protected final UserAccountSearchDataModel userAccountSearchListModel;
	protected final TableController userAccountListCtr;
	protected final VelocityContainer mainVC;
	protected Locale locale;
	private UserAccountService userAccountService;
	protected UserAccountSearchParams lastSearchParams;
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
		userAccountListCtr.addMultiSelectAction("close.account", TABLE_ACTION_PAY_IN_FULL);
		userAccountListCtr.addMultiSelectAction("open.account", TABLE_ACTION_OPEN_ACCOUNT);
		//TODO:In Next Version. 
		//userAccountListCtr.addMultiSelectAction("Email Invoice", TABLE_ACTION_EMAIL_INVOICE);
	}
	
	protected void initColumns() {
		
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.name", 0, TABLE_ACTION_PAYMENT_DETAILS, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.email", 1, null, locale));
		
		DefaultColumnDescriptor amount = new DefaultColumnDescriptor("table.user.account.user.total.amount", 2, null, locale);
		amount.setAlignment(DefaultColumnDescriptor.ALIGNMENT_CENTER);
		userAccountListCtr.addColumnDescriptor(amount);
		
		DefaultColumnDescriptor paid = new DefaultColumnDescriptor("table.user.account.user.amount.paid", 3, null, locale);
		paid.setAlignment(DefaultColumnDescriptor.ALIGNMENT_CENTER);
		userAccountListCtr.addColumnDescriptor(paid);
		
		DefaultColumnDescriptor remaining = new DefaultColumnDescriptor(
				"table.user.account.user.amount.remaining", 4, null, locale);
		remaining.setAlignment(DefaultColumnDescriptor.ALIGNMENT_CENTER);
		userAccountListCtr.addColumnDescriptor(false,remaining);
		
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.paid.status", 5, null, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.pay", 6, TABLE_ACTION_MAKE_PAYMENT, locale));
		userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.template", 7, TABLE_ACTION_TEMPLLATE_DETAILS, locale));
		userAccountListCtr.addColumnDescriptor(false,new DefaultColumnDescriptor(
				"table.user.account.user.template.due.date", 8, null, locale));
		
		/*userAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.assign", 8, TABLE_ACTION_ASSIGN_TEMPLATE, locale));*/
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
					doPaymentForUser(ureq, view.getFeeCategory(), view.getIdentity());
				}/*else if(actionid.equals(TABLE_ACTION_ASSIGN_TEMPLATE)){
					doViewFee(ureq, view);
				}*/else if(actionid.equals(TABLE_ACTION_PAYMENT_DETAILS)){
					doViewAccountDetails(ureq,view.getIdentity());
				}else if(actionid.equals(TABLE_ACTION_TEMPLLATE_DETAILS)){
					doViewFeeCategoryDetails(ureq, view.getFeeCategory());
				}
			} else if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent)event;
				List<UserAccountView> selectedItems = userAccountSearchListModel.getObjects(te.getSelection());
				if(TABLE_ACTION_EMAIL_INVOICE.equals(te.getAction())) {
					for(UserAccountView view : selectedItems){
						String userName = view.getIdentity().getName();
						showWarning("Email Invoice To User "+userName);
					}
				} else if(TABLE_ACTION_PAY_IN_FULL.equals(te.getAction())) {
					doPayInFull(selectedItems);
				} else if(TABLE_ACTION_OPEN_ACCOUNT.equals(te.getAction())){
					doOpenAccount(selectedItems);
				}
			}
		} else if (source == addPayment) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}/*else if (source == assignController){
			if(event instanceof SingleFeeCategoryChosenEvent){
				SingleFeeCategoryChosenEvent feeCategory = (SingleFeeCategoryChosenEvent) event;
				doAssignFee(ureq, feeCategory);
				cmc.deactivate();
				removeAsListenerAndDispose(cmc);
				reloadModel();
			}
		}*/

		super.event(ureq, source, event);
	}
	
	protected UserAccountDetailController controller;
	
	

	private void reloadModel() {
		updateTableModel(lastSearchParams);
		userAccountListCtr.modelChanged();
	}
	
	


	private void doPayInFull(List<UserAccountView> selectedItems) {
		userAccountService.markAccountAsClosed(selectedItems);
		reloadModel();
	}
	private void doOpenAccount(List<UserAccountView> selectedItems) {
		userAccountService.markAccountAsOpen(selectedItems);
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
