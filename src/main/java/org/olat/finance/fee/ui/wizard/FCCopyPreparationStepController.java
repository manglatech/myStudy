package org.olat.finance.fee.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;

public class FCCopyPreparationStepController extends StepFormBasicController {

	private SelectionElement ce;
	private String[] keys, values;
	private final boolean feeCopyEnabled;
	
	public FCCopyPreparationStepController(UserRequest ureq,
			WindowControl windowControl, Form form,
			StepsRunContext stepsRunContext, boolean feeCopyEnabled) {
		
		super(ureq, windowControl, form, stepsRunContext, LAYOUT_DEFAULT, null);

		this.feeCopyEnabled = feeCopyEnabled;
		
		List<String> keyList = new ArrayList<String>(1);
		if(feeCopyEnabled) {
			keyList.add("copyFee");
		}
		
		keys = keyList.toArray(new String[keyList.size()]);
		values = new String[keys.length];
		for(int i=keys.length; i-->0; ) {
				values[i] = translate("fccopywizard.copyform." + keys[i].toLowerCase());
		};
		
		initForm(ureq);
	}
	
	public Boolean getCopyFee() {
		return getBool("copyFee");
	}
	
	private Boolean getBool (String k) {
		int index = -1;
		for(int i=keys.length; i-->0; ) {
			if(k.equals(keys[i])) {
				index = i;
				break;
			}
		}
		if(index >= 0) {
			return new Boolean(ce.isSelected(index));
		}
		return Boolean.FALSE;
	}
	
	@Override
	protected void formOK(UserRequest ureq) {
		addToRunContext("copyFee", getCopyFee());
		fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		ce = uifactory.addCheckboxesVertical("toCopy", "fccopywizard.copyform.label", formLayout, keys, values, null, 1);
		ce.select("copyFee", true);
	}

	@Override
	protected void doDispose() {
		//
	}

}
