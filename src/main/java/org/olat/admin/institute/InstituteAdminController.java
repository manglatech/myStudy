package org.olat.admin.institute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.admin.institute.ui.InstituteAddController;
import org.olat.admin.institute.ui.InstituteTableDataModel;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
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
import org.olat.core.util.Util;
import org.olat.finance.user.model.UserAccountView;
import org.olat.institute.manager.InstituteService;
import org.olat.institute.model.Institute;

/**
 * 
 * Description:<br>
 * This is a controller to configure the SimpleVersionConfig, the configuration
 * of the versioning system for briefcase.
 * 
 * <P>
 * Initial Date:  21 sept. 2009 <br>
 *
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
//fxdiff FXOLAT-127: file versions maintenance tool
public class InstituteAdminController extends BasicController {
	
	private static final String ADD_INSTITUTE_ACTION = "tbl_AddAction";
	private static final String UPDATE_ACTION = "tbl_UpdateAction";
	private static final String DELETE_ACTION = "tbl_DeleteAction";
	
	private InstituteAddController addInstituteController;
	
	private VelocityContainer mainVC;
	protected InstituteTableDataModel instituteFeatureModel;
	protected InstituteService instituteService = null;

	protected TableController tableC;
	private Link createButton;
	protected CloseableModalController cmc;

	
	
	public InstituteAdminController(UserRequest ureq, WindowControl wControl) {
		
		super(ureq, wControl, Util.createPackageTranslator(InstituteAdminController.class, ureq.getLocale()));
		this.instituteService =CoreSpringFactory.getImpl(InstituteService.class);; 
				
		/*settingsForm = new InstituteAddForm(ureq, getWindowControl(),loadFeatures());
		listenTo(settingsForm);
		mainVC.put("institutes", settingsForm.getInitialComponent());
*/
		
		mainVC = createVelocityContainer("institute");
		
		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);
		initButtons();
		instituteFeatureModel = new InstituteTableDataModel();
		tableC.setTableDataModel(instituteFeatureModel);
		
		loadDataModel();
		
		mainVC.put("institutes", tableC.getInitialComponent());
		
		putInitialPanel(mainVC);
	}

	private void initButtons() {
		tableC.addMultiSelectAction("table.institute.delete.action", DELETE_ACTION);
		createButton = LinkFactory.createButton("create.institute", mainVC, this);
		createButton.setElementCssClass("o_sel_group_create");
	}

	protected void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {

		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator(),true);
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.internal.id", 0, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.id", 1, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.name", 2, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.update", 3, UPDATE_ACTION, getLocale()));
	}
	
	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(translate("no.institute.feature.found"));
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}

	@Override
	protected void doDispose() {
		//
	}


	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == createButton) {
			
			removeAsListenerAndDispose(addInstituteController);
			addInstituteController = new InstituteAddController(ureq, getWindowControl(),null);
			listenTo(addInstituteController);

			cmc = new CloseableModalController(getWindowControl(),
					translate("close"), addInstituteController.getInitialComponent(), true,
					translate("create.form.title"));
			
			cmc.activate();
			listenTo(cmc);
			
		}
	}
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<Institute> selectedItems = instituteFeatureModel.getObjects(te.getSelection());
				if (DELETE_ACTION.equals(te.getAction())) {
					doDelete(selectedItems);
				}
				reloadModel();
				
			}
		}else if (source == addInstituteController) {
			cmc.deactivate();
			removeAsListenerAndDispose(cmc);
			reloadModel();
		}else if (event.getCommand().equals(Table.COMMANDLINK_ROWACTION_CLICKED)) {
			
			TableEvent te = (TableEvent) event;
			String actionid = te.getActionId();
			
			Institute view = instituteFeatureModel.getObject(te.getRowId());
			if (actionid.equals(UPDATE_ACTION)) {
				doUpdateInstitute(ureq, view);
			}
		}
	}
	private void doUpdateInstitute(UserRequest ureq, Institute view) {
		removeAsListenerAndDispose(addInstituteController);
		addInstituteController = new InstituteAddController(ureq, getWindowControl(),view);
		listenTo(addInstituteController);

		cmc = new CloseableModalController(getWindowControl(),
				translate("close"), addInstituteController.getInitialComponent(), true,
				translate("create.form.title"));
		
		cmc.activate();
		listenTo(cmc);
	}

	private void reloadModel() {
		loadDataModel();
		tableC.modelChanged();
	}

	private void doDelete(List<Institute> selectedItems) {
		instituteService.deleteInstitute(selectedItems);
	}

	private void loadDataModel() {
		List<Institute> institutes = instituteService.findAllInstitute();
		instituteFeatureModel = new InstituteTableDataModel(institutes, getLocale());
		tableC.setTableDataModel(instituteFeatureModel);
	}
}
