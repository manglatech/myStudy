package org.olat.finance.fee.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.admin.user.UserSearchController;
import org.olat.basesecurity.events.MultiIdentityChosenEvent;
import org.olat.basesecurity.events.SingleIdentityChosenEvent;
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
import org.olat.core.id.Identity;
import org.olat.core.util.event.GenericEventListener;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeIdentityMapping;

public class FeeIdentityMappingListController extends BasicController implements
		GenericEventListener {

	private static final String TABLE_ACTION_DELETE = "feeMappingTblDelete";
	
	private FeeCategory feeCategory;
	private Locale locale;
	private FeeService feeService;
	private VelocityContainer myContent;
	private TableController tableC;
	private Link addUserButton;
	private FeeIdentityMappingListTDM feeMappingModel;
	//private FeeListController addUserButton;
	private CloseableModalController cmc;

	private UserSearchController usc;

	private List<Identity> toAdd;
	
	protected FeeIdentityMappingListController(UserRequest ureq,
			WindowControl wControl, FeeCategory feeCategory) {
		
		super(ureq, wControl);
		this.feeCategory = feeCategory;
		
		this.locale = ureq.getLocale();
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);
		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);
		populateMappingTable();
		
		myContent = createVelocityContainer("listFeeIdentityMapping");
		initButtons(ureq);
		myContent.put("feeIdentityList", tableC.getInitialComponent());

		putInitialPanel(myContent);
		
	}

	private void initButtons(UserRequest ureq) {
		
		tableC.addMultiSelectAction("table.delete.fee.identity.mapping", TABLE_ACTION_DELETE);
		//tableC.addMultiSelectAction("table.update.fee.mapping", TABLE_ACTION_UPDATE);

		addUserButton = LinkFactory.createButton("add.fee.identity.mapping", myContent, this);
		addUserButton.setElementCssClass("o_sel_group_create");
		
	}

	private void populateMappingTable() {
		
		List<FeeIdentityMapping> feeMappings = feeService.findFeeIdentityMapping(feeCategory);
		feeMappingModel = new FeeIdentityMappingListTDM(feeMappings, locale);
		tableC.setTableDataModel(feeMappingModel);
		
	}

	private void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {
		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator());
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.identity.maping.name", 0, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.identity.mapping.email", 1, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.identity.mapping.paid.price", 2, null, locale));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"table.fee.identity.mapping.remaining.price", 3, null, locale));
	}

	private TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(true);
		tableConfig.setTableEmptyMessage(translate("no.identity.assigned"));
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	@Override
	public void event(Event event) {
	}
	public void event(UserRequest ureq, Controller source, Event event) {
		if(source == tableC){
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<FeeIdentityMapping> selectedItems = feeMappingModel.getObjects(te
						.getSelection());
				if (TABLE_ACTION_DELETE.equals(te.getAction())) {
					deleteFeeMappings(ureq, selectedItems);
					reloadModel();
				}
			}
		}else if (source == usc) {
			if (event == Event.CANCELLED_EVENT) {
				cmc.deactivate();
			} else {
				if (event instanceof SingleIdentityChosenEvent) {
					SingleIdentityChosenEvent singleEvent = (SingleIdentityChosenEvent) event;
					Identity choosenIdentity = singleEvent.getChosenIdentity();
					if (choosenIdentity == null) {
						showError("msg.selectionempty");
						return;
					}
					toAdd = new ArrayList<Identity>();
					toAdd.add(choosenIdentity);
				} else if (event instanceof MultiIdentityChosenEvent) {
					MultiIdentityChosenEvent multiEvent = (MultiIdentityChosenEvent) event;
					toAdd = multiEvent.getChosenIdentities();
					if (toAdd.size() == 0) {
						showError("msg.selectionempty");
						return;
					}
				} else {
					throw new RuntimeException("unknown event ::" + event.getCommand());
				}
				
				if (toAdd.size() == 1) {
					//check if already in group [makes only sense for a single choosen identity]
					if (feeService.isIdentityInFeeCategory(toAdd.get(0), this.feeCategory)) {
						getWindowControl().setInfo(translate("msg.idenity.already.assigned.in.fee.category", new String[]{toAdd.get(0).getName()}));
						return;
					}
				} else if (toAdd.size() > 1) {
					//check if already in group
					boolean someAlreadyInGroup = false;
					List<Identity> alreadyInGroup = new ArrayList<Identity>();
					for (int i = 0; i < toAdd.size(); i++) {
						if (feeService.isIdentityInFeeCategory(toAdd.get(i), this.feeCategory)) {
							tableC.setMultiSelectSelectedAt(i, false);
							alreadyInGroup.add(toAdd.get(i));
							someAlreadyInGroup = true;
						}
					}
					if (someAlreadyInGroup) {
						String names = "";
						for(Identity ident: alreadyInGroup) {
							names +=" "+ident.getName();
							toAdd.remove(ident);
						}
						getWindowControl().setInfo(translate("msg.subjectsalreadyingroup", names));
					}
					if (toAdd.isEmpty()) {
						return;
					}
				}
				
				// in both cases continue adding the users or asking for the mail
				// template if available (=not null)
				cmc.deactivate();
				feeService.addIdentitiesToFeeCategory(toAdd,this.feeCategory);
				reloadModel();
			}
			// in any case cleanup this controller, not used anymore
			usc.dispose();
			usc = null;
		}
		/*else if(source == updateFeeMappingController){
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}*/
	}

	private void deleteFeeMappings(UserRequest ureq,
			List<FeeIdentityMapping> selectedItems) {
		if (selectedItems.isEmpty()) {
			showWarning("msg.delete.fee.identity.mappings");
			return;
		}
		List<Long> ids = new ArrayList<Long>();
		for (FeeIdentityMapping feeMapping : selectedItems) {
			ids.add(feeMapping.getKey());
		}
		feeService.removeFeeIdentityMappings(ids);
		
	}

	protected void reloadModel() {
		populateMappingTable();
		tableC.modelChanged();
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == addUserButton) {
			
			removeAsListenerAndDispose(usc);
			usc = new UserSearchController(ureq, getWindowControl(), true, true);			
			listenTo(usc);
			
			Component usersearchview = usc.getInitialComponent();
			removeAsListenerAndDispose(cmc);
			cmc = new CloseableModalController(getWindowControl(), translate("close"), usersearchview, true, translate("add.searchuser"));
			listenTo(cmc);
			cmc.activate();
		}
		/*if (source == createButton) {
			removeAsListenerAndDispose(addFeeController);
			addFeeController = new AddFeeListController(ureq, getWindowControl(),this.feeCategory);
			listenTo(addFeeController);

			cmc = new CloseableModalController(getWindowControl(),
					translate("close"), addFeeController.getInitialComponent(), true,
					translate("add.fee.mapping.title"));
			cmc.activate();
			listenTo(cmc);
		}*/
	}

	@Override
	protected void doDispose() {
		// TODO Auto-generated method stub

	}

}
