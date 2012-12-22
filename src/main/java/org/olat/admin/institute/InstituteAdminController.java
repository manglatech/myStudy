package org.olat.admin.institute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.admin.institute.ui.InstituteFeatureSettingsForm;
import org.olat.admin.institute.ui.InstituteFeatureTableDataModel;
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
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.util.Util;
import org.olat.institute.feature.manager.InstituteFeatureService;
import org.olat.institute.feature.model.InstituteFeature;

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
	
	private static final String ENABLE_ACTION = "tbl_EnableAction";
	private static final String DISABLE_ACTION = "tbl_DisableAction";

	private final InstituteFeatureSettingsForm settingsForm;
	private String selectedInstitute; 
	
	private VelocityContainer mainVC;
	protected InstituteFeatureTableDataModel instituteFeatureModel;
	protected String featureId = null;
	protected InstituteFeatureService instituteFeatureService = null;

	protected TableController tableC;
	
	
	public InstituteAdminController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl, Util.createPackageTranslator(InstituteAdminController.class, ureq.getLocale()));
		
		this.instituteFeatureService = CoreSpringFactory.getImpl(InstituteFeatureService.class);

		settingsForm = new InstituteFeatureSettingsForm(ureq, getWindowControl(),loadFeatures());
		listenTo(settingsForm);
		
		mainVC = createVelocityContainer("admin");
		mainVC.put("institutes", settingsForm.getInitialComponent());
		
		TableGuiConfiguration tableConfig = initTableConfiguration();
		initTable(ureq, tableConfig);
		initButtons();
		instituteFeatureModel = new InstituteFeatureTableDataModel();
		tableC.setTableDataModel(instituteFeatureModel);
		
		mainVC.put("featuresList", tableC.getInitialComponent());
		
		putInitialPanel(mainVC);
	}
	
	private void initButtons() {
		tableC.addMultiSelectAction("table.institute.enable.action", ENABLE_ACTION);
		tableC.addMultiSelectAction("table.institute.disable.action", DISABLE_ACTION);
	}

	protected void initTable(UserRequest ureq, TableGuiConfiguration tableConfig) {

		tableC = new TableController(tableConfig, ureq, getWindowControl(),
				getTranslator(),true);
		listenTo(tableC);
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.control.id", 0, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.control.institute.id", 1, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.control.feature.id", 2, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.control.feature.name", 3, null, getLocale()));
		tableC.addColumnDescriptor(new DefaultColumnDescriptor(
				"institute.control.feature.status", 4, null, getLocale()));
	}
	
	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = new TableGuiConfiguration();
		tableConfig.setDownloadOffered(false);
		tableConfig.setTableEmptyMessage(translate("no.institute.feature.found"));
		tableConfig.setMultiSelect(true);
		return tableConfig;
	}
	
	private Map<String, String> loadFeatures() {
		List<InstituteFeature> f = instituteFeatureService.loadUniqueInstitutes();
		Map<String,String> map = new HashMap<String, String>();
		for(InstituteFeature feature : f){
			map.put(feature.getInstituteId(), feature.getFeatureName());
		}
		return map;
	}

	@Override
	protected void doDispose() {
		//
	}


	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(source == settingsForm){
			if(event.getCommand() == Event.CHANGED_EVENT.getCommand()){
				String key = settingsForm.getFeaturesList().getSelectedKey();
				loadDataModel(key);
			}
		}else if (source == tableC) {
			if (event instanceof TableMultiSelectEvent) {
				TableMultiSelectEvent te = (TableMultiSelectEvent) event;
				List<InstituteFeature> selectedItems = instituteFeatureModel.getObjects(te.getSelection());
				if (ENABLE_ACTION.equals(te.getAction())) {
					doChangeStatus(selectedItems,true);
				}else if (DISABLE_ACTION.equals(te.getAction())) {
					doChangeStatus(selectedItems,false);
				}
				loadDataModel(selectedInstitute);
			} 
		}
	}

	private void doChangeStatus(List<InstituteFeature> selectedItems,boolean enable) {
		if(enable){
			instituteFeatureService.changeStatus(selectedItems,1);
		}else{
			instituteFeatureService.changeStatus(selectedItems,0);
		}
		
	}

	private void loadDataModel(String key) {
		List<InstituteFeature> features = instituteFeatureService.findInstituteFeaturesByInstituteId(key);
		instituteFeatureModel = new InstituteFeatureTableDataModel(features, getLocale());
		tableC.setTableDataModel(instituteFeatureModel);
		tableC.modelChanged();
		selectedInstitute = key;
	}

}
