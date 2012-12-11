package org.olat.finance.user.ui;

import java.util.Collections;
import java.util.List;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.finance.fee.ui.FeeCategoryListTableDataModel;
import org.olat.finance.user.model.UserAccountView;
import org.olat.group.ui.main.BGTableItem;

public class UserAccountSearchDataModel extends
		DefaultTableDataModel<UserAccountView> {

	// private final List<UserAccountView> userAccountViews;

	public UserAccountSearchDataModel(List<UserAccountView> userAccountViews) {
		super(userAccountViews);
	}

	public UserAccountSearchDataModel() {
		super(Collections.<UserAccountView> emptyList());
	}

	@Override
	public int getColumnCount() {
		return 10;
	}

	public void setEntries(List<UserAccountView> data) {
		setObjects(data);
	}

	@Override
	public Object getValueAt(int row, int col) {
		UserAccountView userAccount = getObject(row);
		if (userAccount != null) {
			switch (col) {
			case 0:
				return userAccount.getName();
			case 1:
				return userAccount.getEmail();
			case 2:
				return userAccount.getGroupName();
			case 3:
				return userAccount.getTemplateName();
			case 4:
				return userAccount.getTotalAmount();
			case 5:
				return userAccount.getPaidAmount();
			case 6:
				return userAccount.getRemainingAmount();
			case 7:
				return userAccount.getPaidStatus();
			case 8:
				if (userAccount.getTotalAmount() > 0) {
					return "Pay Now!";
				}
				return null;
			case 9:
				if(userAccount.getTemplateId() == null){
					return "Assign";
				}
				return null;
			default: {
				return null;
			}
			}
		}
		return null;
	}

	@Override
	public Object createCopyWithEmptyList() {
		UserAccountSearchDataModel copy = new UserAccountSearchDataModel();
		return copy;
	}

	public enum Cols {

		name("table.header.name"), email("table.header.email"), groupname(
				"table.header.groupname"), templatename(
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
