package org.olat.finance.fee.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.control.WindowControl;
import org.olat.finance.fee.ui.FeeCategoryListController;
import org.olat.finance.user.model.UserAccountView;

public class AssingFeeCategoryListController extends FeeCategoryListController {

	private final UserAccountView view;
	
	public AssingFeeCategoryListController(UserRequest ureq,
			WindowControl windowControl, UserAccountView view) {
		super(ureq,windowControl);
		this.view = view;
	}
	
	@Override
	protected void initButtons(UserRequest ureq) {
	}
	
	@Override
	protected TableGuiConfiguration initTableConfiguration() {
		TableGuiConfiguration tableConfig = super.initTableConfiguration();
		tableConfig.setMultiSelect(false);
		return tableConfig;
	}
	public UserAccountView getView() {
		return view;
	}
	
}
