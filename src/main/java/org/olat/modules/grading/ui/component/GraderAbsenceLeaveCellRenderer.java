/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.grading.ui.component;

import java.util.List;

import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableComponent;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Formatter;
import org.olat.modules.grading.ui.AssignedReferenceEntryRow;
import org.olat.modules.grading.ui.GraderRow;
import org.olat.user.AbsenceLeave;

/**
 * 
 * Initial date: 14 févr. 2020<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class GraderAbsenceLeaveCellRenderer implements FlexiCellRenderer {
	
	private final Formatter formatter;
	
	public GraderAbsenceLeaveCellRenderer(Translator translator) {
		formatter = Formatter.getInstance(translator.getLocale());
	}

	@Override
	public void render(Renderer renderer, StringOutput target, Object cellValue, int row, FlexiTableComponent source,
			URLBuilder ubu, Translator trans) {
		if(cellValue instanceof GraderRow) {
			render(target, ((GraderRow)cellValue).getAbsenceLeaves());
		} else if(cellValue instanceof AssignedReferenceEntryRow) {
			render(target, ((AssignedReferenceEntryRow)cellValue).getAbsenceLeaves());
		}
	}
	
	private void render(StringOutput target, List<AbsenceLeave> absenceLeaves) {
		for(AbsenceLeave absenceLeave:absenceLeaves) {
			if(absenceLeave.getAbsentFrom() != null && absenceLeave.getAbsentTo() != null) {
				target.append(formatter.formatDate(absenceLeave.getAbsentFrom()))
				      .append(" - ")
				      .append(formatter.formatDate(absenceLeave.getAbsentTo()));
			}
		}
	}
}
