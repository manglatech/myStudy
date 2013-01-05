package org.olat.admin.institute.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.institute.feature.model.InstituteFeature;

public class InstituteFeatureTableDataModel extends DefaultTableDataModel<InstituteFeature>{

	private Locale locale;
	
	public InstituteFeatureTableDataModel(List<InstituteFeature> objects, Locale locale) {
		super(objects);
		this.locale = locale;
	}
	public InstituteFeatureTableDataModel() {
		super(new ArrayList<InstituteFeature>());
	}
	
	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getValueAt(int,
	 *      int)
	 */
	public final Object getValueAt(int row, int col) {
		InstituteFeature i = (InstituteFeature) getObject(row);
		if(i == null)
			return "";
		
		switch (col) {
		case 0:
			return i.getKey();
		case 1:
			return i.getInstituteId();
		case 2:
			return i.getFeatureId();
		case 3:
			return i.getFeatureName();
		case 4:
			if(i.getStatus() == 0){
				return "Disabled";
			}else if(i.getStatus() == 1){
				return "Enabled";
			}
			return "";
		default:
			return "error";
		}
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 5;
	}
	
	@Override
	public Object createCopyWithEmptyList() {
		InstituteFeatureTableDataModel copy = new InstituteFeatureTableDataModel();
		return copy;
	}
	
}
