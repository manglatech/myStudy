package org.olat.finance.fee.ui;

import java.util.ArrayList;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.id.Identity;
import org.olat.finance.fee.ui.FeeCategoryListController;
import org.olat.finance.user.model.UserAccountView;

public class AssingFeeCategoryListController extends FeeCategoryListController {

	//private final UserAccountView view;
	private List<Identity> identites = new ArrayList<Identity>();
	
	/*public AssingFeeCategoryListController(UserRequest ureq,
			WindowControl windowControl, UserAccountView view) {
		super(ureq,windowControl);
		this.view = view;
	}*/
	
	public AssingFeeCategoryListController(UserRequest ureq,
			WindowControl windowControl, List<Identity> selectedItems) {
		super(ureq,windowControl);
		this.identites = selectedItems;
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

	public List<Identity> getIdentites() {
		return identites;
	}
	
	/*public UserAccountView getView() {
		return view;
	}*/
	
}
