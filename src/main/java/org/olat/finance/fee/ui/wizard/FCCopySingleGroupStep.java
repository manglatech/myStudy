package org.olat.finance.fee.ui.wizard;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.elements.FormLinkImpl;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.group.ui.wizard.BGCopySingleGroupStep;
import org.olat.group.ui.wizard.BGCopySingleGroupStepController;

public class FCCopySingleGroupStep extends BasicStep {

	private final boolean lastCategory;
	private FeeCategory categoryToCopy;
	
	public FCCopySingleGroupStep(UserRequest ureq, List<FeeCategory> category) {
		super(ureq);
		
		categoryToCopy = category.remove(0);
		setI18nTitleAndDescr("copy.wizard.bgstep", "copy.wizard.bgstep");
		
		lastCategory = category.isEmpty();
		if(lastCategory) {
			setNextStep(Step.NOSTEP);
		} else {
			setNextStep(new FCCopySingleGroupStep(ureq, category));
		}
	}

	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		if(lastCategory) {
			return PrevNextFinishConfig.BACK_FINISH;
		}
		return new PrevNextFinishConfig(false, true, false);
	}

	@Override
	public FormItem getStepTitle() {
		String title = getTranslator().translate("copy.wizard.bgstep", new String[]{ categoryToCopy.getName() });
		FormLink fl = new FormLinkImpl("copy.wizard." + categoryToCopy.getKey(), null, title, Link.FLEXIBLEFORMLNK + Link.NONTRANSLATED);
		fl.setTranslator(getTranslator());
		return fl;
	}

	@Override
	public StepFormController getStepController(UserRequest ureq,
			WindowControl windowControl, StepsRunContext stepsRunContext,
			Form form) {
		FCCopySingleCategoryStepController copyForm = new FCCopySingleCategoryStepController(ureq, windowControl, form, stepsRunContext, categoryToCopy);
		return copyForm;
	}

}
