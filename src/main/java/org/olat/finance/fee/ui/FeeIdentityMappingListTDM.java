package org.olat.finance.fee.ui;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.core.id.UserConstants;
import org.olat.finance.fee.model.FeeIdentityMapping;
import org.olat.finance.user.payment.model.UserPaymentInfo;

public class FeeIdentityMappingListTDM extends
		DefaultTableDataModel<FeeIdentityMapping> {
	
	private Locale locale;

	/**
	 * @param objects
	 * @param locale
	 */
	public FeeIdentityMappingListTDM(List<FeeIdentityMapping> objects,
			Locale locale) {
		super(objects);
		this.locale = locale;
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getValueAt(int,
	 *      int)
	 */
	public final Object getValueAt(int row, int col) {
		FeeIdentityMapping f = (FeeIdentityMapping) getObject(row);
		Set<UserPaymentInfo> payments = null;
		if(f != null){
			payments = f.getUserPayments();
		}
		
		switch (col) {
		case 0:
			String name = StringEscapeUtils.escapeHtml(f.getIdentity().getName())
					.toString();
			return name;
		case 1:
			String email = StringEscapeUtils.escapeHtml(
					f.getIdentity().getUser().getProperty(UserConstants.EMAIL,null));
			return email;
		case 2:
			if(payments != null){
				return payments.size();
			}
			return 0;
		case 3:
			Integer paymnetAmt = 0;
			if(payments != null){
				for(UserPaymentInfo info : payments){
					paymnetAmt += info.getPaidAmount();
				}
			}
			return paymnetAmt;	
		default:
			return "error";
		}
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 4;
	}
}
