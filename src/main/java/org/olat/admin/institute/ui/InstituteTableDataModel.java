package org.olat.admin.institute.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.institute.model.Institute;

public class InstituteTableDataModel extends DefaultTableDataModel<Institute>{

	private Locale locale;
	
	public InstituteTableDataModel(List<Institute> objects, Locale locale) {
		super(objects);
		this.locale = locale;
	}
	public InstituteTableDataModel() {
		super(new ArrayList<Institute>());
	}
	
	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getValueAt(int,
	 *      int)
	 */
	public final Object getValueAt(int row, int col) {
		Institute i = (Institute) getObject(row);
		if(i == null)
			return "";
		
		switch (col) {
		case 0:
			return i.getKey();
		case 1:
			return i.getInstituteId();
		case 2:
			return i.getName();
		case 3:
			return "Update";
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
	
	@Override
	public Object createCopyWithEmptyList() {
		InstituteTableDataModel copy = new InstituteTableDataModel();
		return copy;
	}
	
}
