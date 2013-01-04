package org.olat.finance.fee.ui.wizard;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.finance.fee.model.FeeCategory;

public class FCCopyPreparationStep extends BasicStep {

	private boolean feeCopyEnabled;
	public FCCopyPreparationStep(UserRequest ureq, List<FeeCategory> category, boolean feeCopyEnabled) {
		super(ureq);
		this.feeCopyEnabled = feeCopyEnabled;
		
		setI18nTitleAndDescr("fccopywizard.title", "fccopywizard.title");
		setNextStep(new FCCopySingleGroupStep(ureq, category));
	}

	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		return new PrevNextFinishConfig(false, true, false);
	}

	@Override
	public StepFormController getStepController(UserRequest ureq,
			WindowControl windowControl, StepsRunContext stepsRunContext,
			Form form) {
		FCCopyPreparationStepController copyForm = new FCCopyPreparationStepController(ureq, windowControl, form,
				stepsRunContext, feeCopyEnabled);
		return copyForm;
	}
	

}
