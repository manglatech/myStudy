package org.olat.finance.fee.ui;

import java.util.ArrayList;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.table.TableGuiConfiguration;
import org.olat.core.gui.control.WindowControl;

public class AssingFeeCategoryListController<E> extends FeeCategoryListController {

	private List<E> entities = new ArrayList<E>();
	
	public AssingFeeCategoryListController(UserRequest ureq,
			WindowControl windowControl, List<E> selectedItems) {
		super(ureq,windowControl);
		this.entities = selectedItems;
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
	public List<E> getEntities() {
		return entities;
	}

	
}
