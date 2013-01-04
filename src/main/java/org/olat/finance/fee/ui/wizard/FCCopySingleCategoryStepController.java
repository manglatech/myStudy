package org.olat.finance.fee.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.ui.FeeCategoryFormController;

public class FCCopySingleCategoryStepController extends StepFormBasicController {

	
	private FeeCategory originalCategory;
	private FeeCategoryFormController feeFormController;
	
	public FCCopySingleCategoryStepController(UserRequest ureq, WindowControl wControl, Form rootForm, StepsRunContext runContext,
			FeeCategory originalCategory) {
		super(ureq, wControl, rootForm, runContext, LAYOUT_CUSTOM, "wrapper");
		
		this.originalCategory = originalCategory;
		feeFormController = new FeeCategoryFormController(ureq, getWindowControl(), originalCategory, mainForm);
		
		listenTo(feeFormController);
		feeFormController.getFeeName().setValue(originalCategory.getName() + " " + translate("fccopywizard.copyform.name.copy"));	
		
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		formLayout.add("wrapped", feeFormController.getInitialFormItem());
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		return feeFormController.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		@SuppressWarnings("unchecked")
		List<FCCopyFeeCategory> copies = (List<FCCopyFeeCategory>)getFromRunContext("categoryCopy");
		if(copies == null) {
			copies = new ArrayList<FCCopyFeeCategory>();
			addToRunContext("categoryCopy", copies);
		}
		
		FCCopyFeeCategory currentCopy = getWithOriginal(copies);
		if(currentCopy == null) {
			FCCopyFeeCategory feeCategory = new FCCopyFeeCategory(originalCategory);
			feeCategory.setName(feeFormController.getFeeName().getValue());
			feeCategory.setDescription(feeFormController.getFeeDesc().getValue());
			copies.add(feeCategory);
		} else {
			currentCopy.setName(feeFormController.getFeeName().getValue());
			currentCopy.setDescription(feeFormController.getFeeDesc().getValue());
		}
		fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
	}
	
	private FCCopyFeeCategory getWithOriginal(List<FCCopyFeeCategory> copies) {
		for(FCCopyFeeCategory copy:copies) {
			if(copy.getOriginal().equals(originalCategory)) {
				return copy;
			}
		}
		return null;
	}

}
