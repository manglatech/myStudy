package org.olat.admin.institute.ui;

import java.util.Map;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;

public class InstituteFeatureSettingsForm extends FormBasicController {

	private SingleSelection featuresList;
	private String[] keys;
	private String[] values;
	
	public InstituteFeatureSettingsForm(UserRequest ureq,
			WindowControl wControl, Map<String,String> features) {
		super(ureq, wControl);
		
		keys = new String[features.keySet().size()];
		values = new String[features.keySet().size()];
		
		int index = 0;
		for(String key : features.keySet()){
			keys[index] = key;
			values[index] = features.get(key);
			index++;
		}
		
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		// First add title and context help
		setFormTitle("institute.feature.form.title");
		setFormDescription("institute.feature.intro");
		
		featuresList = uifactory.addDropdownSingleselect("features.numOfFeatures", formLayout, keys, values, null);
		featuresList.addActionListener(this, FormEvent.ONCHANGE);
	}

	@Override
	protected void doDispose() {
	}

	@Override
	protected void formOK(UserRequest ureq) {
		System.out.println("Event Called");
	}
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		System.out.println("Event Called");
	}
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		System.out.println("Event Component Called");
		fireEvent(ureq, Event.CHANGED_EVENT);
	}
	public SingleSelection getFeaturesList() {
		return featuresList;
	}
	public void setFeaturesList(SingleSelection featuresList) {
		this.featuresList = featuresList;
	}
}
