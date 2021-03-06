package org.olat.finance.user.payment.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.user.payment.model.UserPaymentInfo;

public class UserAccountListTDM extends DefaultTableDataModel<UserPaymentInfo> {

	private Locale locale;

	public UserAccountListTDM(List<UserPaymentInfo> objects, Locale locale) {
		super(objects);
		this.locale = locale;
	}

	public UserAccountListTDM() {
		super(new ArrayList<UserPaymentInfo>());
	}

	public final Object getValueAt(int row, int col) {
		UserPaymentInfo f = (UserPaymentInfo) getObject(row);
		switch (col) {
		case 0:
			FeeCategory category = f.getFeeIdentityMapping().getFeeCategory();
			return category.getName();
		case 1:
			return f.getPaidAmount();
		case 2:
			return f.getCreationDate();
		default:
			return "error";
		}
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}

	public Object createCopyWithEmptyList() {
		UserAccountListTDM copy = new UserAccountListTDM();
		return copy;
	}
}
