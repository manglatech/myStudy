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

	private SingleSelection instituteList;
	private String[] keys;
	private String[] values;
	
	public InstituteFeatureSettingsForm(UserRequest ureq,
			WindowControl wControl, Map<String,String> institutes) {
		super(ureq, wControl);
		
		keys = new String[institutes.keySet().size()];
		values = new String[institutes.keySet().size()];
		
		int index = 0;
		for(String key : institutes.keySet()){
			keys[index] = key;
			values[index] = institutes.get(key);
			index++;
		}
		
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		// First add title and context help
		setFormTitle("institute.feature.form.title");
		setFormDescription("institute.feature.intro");
		
		instituteList = uifactory.addDropdownSingleselect("features.numOfFeatures", formLayout, keys, values, null);
		instituteList.addActionListener(this, FormEvent.ONCHANGE);
		instituteList.select(keys[0], true);
		
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
	public SingleSelection getInstituteList() {
		return instituteList;
	}
	public void setInstituteList(SingleSelection instituteList) {
		this.instituteList = instituteList;
	}
}
