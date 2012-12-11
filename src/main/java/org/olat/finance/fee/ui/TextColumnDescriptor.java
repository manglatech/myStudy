package org.olat.finance.fee.ui;

import java.util.Locale;

import org.olat.core.gui.components.table.ColumnDescriptor;
import org.olat.core.gui.components.table.HrefGenerator;
import org.olat.core.gui.components.table.Table;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.logging.AssertException;

public class TextColumnDescriptor implements ColumnDescriptor {

	private Table table;
	private String headerKey;
	private int column;
	protected Locale locale;

	public TextColumnDescriptor(String headerKey, int column, Locale locale) {
		this.headerKey = headerKey;
		this.column = column;
		this.locale = locale;
	}

	public void renderValue(StringOutput sb, int row, Renderer renderer) {
		// add checkbox
		int currentPosInModel = table.getSortedRow(row);
		Long value = (Long) table.getTableDataModel().getValueAt(
				currentPosInModel, column);
		if (renderer == null) {
			// special case for table download
			sb.append("x");
		} else {

			String id = "tb_ms";

			// read write view
			sb.append("<input type=\"").append("input").append("\" id=\"");
			sb.append(id);
			sb.append("\" name=\"");
			sb.append(id);
			sb.append("\" size=\"");
			sb.append("10");
			sb.append("\" value=\"");
			sb.append(value);
			sb.append("\" ");
			sb.append(" />");

		}
	}

	public int compareTo(int rowa, int rowb) {
		Long rowaChecked = (Long) table.getTableDataModel().getValueAt(
				rowa, column);
		Long rowbChecked = (Long) table.getTableDataModel().getValueAt(
				rowb, column);
		
		if (rowaChecked == null || rowbChecked == null)
			return -1;
		else if (rowaChecked.longValue() == rowbChecked.longValue())
			return 1;
		return 0;
	}

	public boolean equals(Object object) {
		if (object instanceof TextColumnDescriptor)
			return true;
		return false;
	}

	public String getHeaderKey() {
		return this.headerKey;
	}

	public boolean translateHeaderKey() {
		return false;
	}

	public int getAlignment() {
		return ColumnDescriptor.ALIGNMENT_CENTER;
	}

	public String getAction(int row) {
		// no action
		return null;
	}

	public HrefGenerator getHrefGenerator() {
		// no HrefGenerator
		return null;
	}

	public String getPopUpWindowAttributes() {
		// no PopuWindow
		return null;
	}

	public boolean isPopUpWindowAction() {
		return false;
	}

	public boolean isSortingAllowed() {
		return true;
	}

	public void modelChanged() {
		// nothing to do here
	}

	public void otherColumnDescriptorSorted() {
		// nothing to do here
	}

	public void setHrefGenerator(HrefGenerator h) {
		throw new AssertException(
				"Not allowed to set HrefGenerator on MultiSelectColumn.");
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void sortingAboutToStart() {
		// nothing to do here
	}

	public String toString(int rowid) {
		return "long";
	}

}
