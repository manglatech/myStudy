/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <hr>
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * This file has been modified by the OpenOLAT community. Changes are licensed
 * under the Apache 2.0 license as the original file.
 */

package org.olat.finance.fee.ui;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.finance.fee.model.FeeMapping;

public class FeeMappingListTableDataModel extends DefaultTableDataModel<FeeMapping> {

	private Locale locale;

	/**
	 * @param objects
	 * @param locale
	 */
	public FeeMappingListTableDataModel(List<FeeMapping> objects, Locale locale) {
		super(objects);
		this.locale = locale;
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getValueAt(int,
	 *      int)
	 */
	public final Object getValueAt(int row, int col) {
		FeeMapping f = (FeeMapping) getObject(row);
		switch (col) {
		case 0:
			String name = StringEscapeUtils.escapeHtml(f.getFee().getName())
					.toString();
			return name;
		case 1:
			String desc = StringEscapeUtils.escapeHtml(
					f.getFee().getDescription()).toString();
			return desc;
		case 2:
			Integer price = f.getPrice();
			return price;
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
}
