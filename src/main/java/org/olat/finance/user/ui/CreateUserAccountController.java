package org.olat.finance.user.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.SingleFeeCategoryChosenEvent;
import org.olat.finance.fee.ui.AssingFeeCategoryListController;
import org.olat.finance.user.manager.UserAccountService;
import org.olat.finance.user.model.UserAccountView;

public class CreateUserAccountController extends BasicController implements
		Activateable2 {

	private final CreateUserAccountSearchController searchController;

	protected static final String TABLE_ACTION_ASSIGN_TEMPLATE = "uaTblAssignFee";
	
	protected final CreateUserAccountSearchDataModel createUserAccountSearchListModel;
	protected final TableController createUserAccountListCtr;
	protected final VelocityContainer mainVC;
	protected Locale locale;
	private UserAccountService userAccountService;
	protected CreateUserAccountSearchParams lastSearchParams;
	
	protected CloseableModalController cmc;
	private AssingFeeCategoryListController<Identity> assignController;
	protected UserAccountDetailController userAccountDetailContoller;
	
	//TODO: Remove this:
	FeeService feeService; 
	
	public CreateUserAccountController(UserRequest ureq, WindowControl wControl) {
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

		createUserAccountListCtr = new TableController(tableConfig, ureq,
				getWindowControl(), getTranslator(), true);
		listenTo(createUserAccountListCtr);
		initColumns();

		createUserAccountSearchListModel = new CreateUserAccountSearchDataModel(null);

		createUserAccountListCtr.setTableDataModel(createUserAccountSearchListModel);
		createUserAccountListCtr.setMultiSelect(true);

		mainVC.put("userAccountList", createUserAccountListCtr.getInitialComponent());
		
		// search controller
		searchController = new CreateUserAccountSearchController(ureq, wControl);
		listenTo(searchController);
		mainVC.put("search", searchController.getInitialComponent());

		initButtons(ureq);
		putInitialPanel(mainVC);
		
	}
	
	protected void initButtons(UserRequest ureq) {
		createUserAccountListCtr.setMultiSelect(true);
		createUserAccountListCtr.addMultiSelectAction("assign.fee", TABLE_ACTION_ASSIGN_TEMPLATE);
	}
	
	protected void initColumns() {
		
		createUserAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.name", 0, null, locale));
		createUserAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.firstname", 1, null, locale));
		createUserAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.lastname", 2, null, locale));
		createUserAccountListCtr.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.user.account.user.email", 3, null, locale));
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		
		if (event instanceof TableMultiSelectEvent) {
			TableMultiSelectEvent te = (TableMultiSelectEvent)event;
			List<Identity> selectedItems = createUserAccountSearchListModel.getObjects(te.getSelection());
			if(TABLE_ACTION_ASSIGN_TEMPLATE.equals(te.getAction())) {
				doAssignFeeCategory(ureq, selectedItems);
			}
		} else if (source == searchController) {
			if (event instanceof CreateUserAccountSearchEvent) {
				doSearch(ureq, (CreateUserAccountSearchEvent) event);
			}
		}else if (source == assignController){
			if(event instanceof SingleFeeCategoryChosenEvent){
				SingleFeeCategoryChosenEvent feeCategory = (SingleFeeCategoryChosenEvent) event;
				doAssignFeeCategory(ureq, feeCategory);
				cmc.deactivate();
				removeAsListenerAndDispose(cmc);
				reloadModel();
			}
		}
		super.event(ureq, source, event);
	}
	
	private void doAssignFeeCategory(UserRequest ureq,
			SingleFeeCategoryChosenEvent feeCategory) {
		FeeCategory category = feeCategory.getChosenFeeCategory();
		List<Identity> toAdd = assignController.getEntities();
		//check if already in group
		boolean someAlreadyInGroup = false;
		List<Identity> alreadyInGroup = new ArrayList<Identity>();
		for (int i = 0; i < toAdd.size(); i++) {
			if (feeService.isIdentityInFeeCategory(toAdd.get(i), category)) {
				createUserAccountListCtr.setMultiSelectSelectedAt(i, false);
				alreadyInGroup.add(toAdd.get(i));
				someAlreadyInGroup = true;
			}
		}
		if (someAlreadyInGroup) {
			String msg = getNames(alreadyInGroup);
			toAdd.removeAll(alreadyInGroup);
			getWindowControl().setInfo(translate("msg.already.assigned", msg));
		}
		if (toAdd.isEmpty()) {
			return;
		}else{
			feeService.addIdentitiesToFeeCategory(toAdd,category);
			String msg = getNames(toAdd);
			getWindowControl().setInfo(translate("msg.added.successfully", msg));
		}
	}
	private String getNames(List<Identity> entities){
		StringBuilder names = new StringBuilder();
		for(Identity ident: entities) {
			names.append(" ").append(ident.getName());
		}
		return names.toString();
	}

	private void doAssignFeeCategory(UserRequest ureq, List<Identity> selectedItems) {
		removeAsListenerAndDispose(assignController);
		assignController = new AssingFeeCategoryListController<Identity>(ureq, getWindowControl(), selectedItems);
		listenTo(assignController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), assignController.getInitialComponent(), true,
				translate("add.payment.form.title"));
		cmc.activate();
		listenTo(cmc);
		
	}

	private void reloadModel() {
		updateTableModel(lastSearchParams);
		createUserAccountListCtr.modelChanged();
	}
	
	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries,
			StateEntry state) {
		if (state instanceof UserAccountSearchEvent) {
			searchController.activate(ureq, entries, state);
			doSearch(ureq, (CreateUserAccountSearchEvent) state);
		}
	}

	protected void updateSearch(UserRequest ureq) {
		doSearch(ureq, null);
	}

	private void doSearch(UserRequest ureq, CreateUserAccountSearchEvent event) {
		long start = isLogDebugEnabled() ? System.currentTimeMillis() : 0;

		search(event);
		createUserAccountListCtr.modelChanged();

		if (isLogDebugEnabled()) {
			logDebug("User Account search takes (ms): "
					+ (System.currentTimeMillis() - start), null);
		}
	}

	private void search(CreateUserAccountSearchEvent event) {
		if (event == null) {
			CreateUserAccountSearchParams params = new CreateUserAccountSearchParams();
			params.setInstituteId(getUserInst());
			updateTableModel(params);
		} else {
			CreateUserAccountSearchParams params = event
					.convertToSearchBusinessGroupParams(getIdentity());
			updateTableModel(params);
		}
	}

	private void updateTableModel(CreateUserAccountSearchParams params) {
		if(params == null){
			createUserAccountSearchListModel.setEntries(Collections.<Identity>emptyList());
		}else{
			List<Identity> views = userAccountService.searchUserAccountSummary(params);
			createUserAccountSearchListModel.setEntries(views);
		}
		lastSearchParams = params;
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		System.out.println("test");
	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub
		
	}
}
