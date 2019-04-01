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
package org.olat.core.commons.editor.htmleditor;

import org.olat.core.commons.modules.bc.components.FolderComponent;
import org.olat.core.commons.services.vfs.VFSLeafEditorConfigs;

/**
 * 
 * Initial date: 31 Mar 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class HTMLEditorConfig implements VFSLeafEditorConfigs.Config {

	public static final String TYPE = "oo-html-editor";
	
	private FolderComponent folderComponent;

	private HTMLEditorConfig(Builder builder) {
		this.folderComponent = builder.folderComponent;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	public FolderComponent getFolderComponent() {
		return folderComponent;
	}

	public static Builder builder(FolderComponent folderComponent) {
		return new Builder(folderComponent);
	}

	public static final class Builder {
		private FolderComponent folderComponent;

		private Builder(FolderComponent folderComponent) {
			this.folderComponent = folderComponent;
		}

		public HTMLEditorConfig build() {
			return new HTMLEditorConfig(this);
		}
	}

}
