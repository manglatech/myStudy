package org.olat.finance.user.ui;

import java.util.Collections;
import java.util.List;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.core.id.Identity;
import org.olat.core.id.UserConstants;

public class CreateUserAccountSearchDataModel extends
		DefaultTableDataModel<Identity> {

	// private final List<UserAccountView> userAccountViews;

	public CreateUserAccountSearchDataModel(List<Identity> identites) {
		super(identites);
	}

	public CreateUserAccountSearchDataModel() {
		super(Collections.<Identity> emptyList());
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	public void setEntries(List<Identity> data) {
		setObjects(data);
	}

	@Override
	public Object getValueAt(int row, int col) {
		Identity identity = getObject(row);
		if (identity != null) {
			switch (col) {
				case 0:
					return identity.getName();
				case 1:
					return identity.getUser().getProperty(UserConstants.FIRSTNAME, null);
				case 2:
					return identity.getUser().getProperty(UserConstants.LASTNAME, null);
				case 3:
					return identity.getUser().getProperty(UserConstants.EMAIL, null);
				default: {
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public Object createCopyWithEmptyList() {
		CreateUserAccountSearchDataModel copy = new CreateUserAccountSearchDataModel();
		return copy;
	}

	public enum Cols {

		name("table.header.name"), email("table.header.email"), templatename(
				"table.header.templatename"), totalamount(
				"table.header.totalamount"), paidamount(
				"table.header.paidamount"), remainingamount(
				"table.header.remainingamount"), paidstatus(
				"table.header.paidstatus"), payinfull(
				"table.header.pay.in.full");

		private final String i18n;

		private Cols(String i18n) {
			this.i18n = i18n;
		}

		public String i18n() {
			return i18n;
		}
	}

}
