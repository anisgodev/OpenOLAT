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
* <p>
*/

package org.olat.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.olat.course.nodes.co.COEditController.CONFIG_KEY_EMAILTOADRESSES;
import static org.olat.course.nodes.co.COEditController.CONFIG_KEY_EMAILTOCOACHES;
import static org.olat.course.nodes.co.COEditController.CONFIG_KEY_EMAILTOPARTICIPANTS;
import static org.olat.course.nodes.co.COEditController.CONFIG_KEY_MBODY_DEFAULT;
import static org.olat.course.nodes.co.COEditController.CONFIG_KEY_MSUBJECT_DEFAULT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.junit.Before;
import org.junit.Test;
import org.olat.basesecurity.BaseSecurityManager;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.gui.components.tree.TreeNode;
import org.olat.core.id.Identity;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.nodes.CourseNode;
import org.olat.course.tree.CourseEditorTreeNode;
import org.olat.modules.ModuleConfiguration;
import org.olat.restapi.repository.course.CoursesWebService;
import org.olat.restapi.support.vo.CourseNodeVO;
import org.olat.test.OlatJerseyTestCase;

/**
 * 
 * Description:<br>
 * Test the creation and management of contact building block
 * 
 * <P>
 * Initial Date:  6 mai 2010 <br>
 * @author srosse, stephane.rosse@frentix.com
 */
public class CoursesContactElementTest extends OlatJerseyTestCase {
	
	private Identity admin;
	private ICourse course1;
	private String rootNodeId;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		admin = BaseSecurityManager.getInstance().findIdentityByName("administrator");
		course1 = CoursesWebService.createEmptyCourse(admin, "course-rest-contacts", "Course to test the contacts elements", null);
		DBFactory.getInstance().intermediateCommit();
		
		rootNodeId = course1.getEditorTreeModel().getRootNode().getIdent();
	}
	
	@Test
	public void testBareBoneConfig() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//create an contact node
		URI newContactUri = getElementsUri(course1).path("contact")
			.queryParam("parentNodeId", rootNodeId)
			.queryParam("position", "0").queryParam("shortTitle", "Contact-0")
			.queryParam("longTitle", "Contact-long-0")
			.queryParam("objectives", "Contact-objectives-0").build();
		HttpPut method = conn.createPut(newContactUri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		InputStream body = response.getEntity().getContent();
		
		
		assertEquals(200, response.getStatusLine().getStatusCode());
		CourseNodeVO contactNode = parse(body, CourseNodeVO.class);
		assertNotNull(contactNode);
		assertNotNull(contactNode.getId());
		assertEquals(contactNode.getShortTitle(), "Contact-0");
		assertEquals(contactNode.getLongTitle(), "Contact-long-0");
		assertEquals(contactNode.getLearningObjectives(), "Contact-objectives-0");
		assertEquals(contactNode.getParentId(), rootNodeId);
	}
	
	@Test
	public void testFullConfig() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//create an contact node
		URI newContactUri = getElementsUri(course1).path("contact")
			.queryParam("parentNodeId", rootNodeId)
			.queryParam("position", "0").queryParam("shortTitle", "Contact-1")
			.queryParam("longTitle", "Contact-long-1")
			.queryParam("objectives", "Contact-objectives-1")
			.queryParam("coaches", "true")
			.queryParam("participants", "true")
			.queryParam("groups", "")
			.queryParam("areas", "")
			.queryParam("to", "test@frentix.com;test2@frentix.com")
			.queryParam("defaultSubject", "Hello by contact 1")
			.queryParam("defaultBody", "Hello by contact 1 body")
			.build();
		HttpPut method = conn.createPut(newContactUri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		InputStream body = response.getEntity().getContent();
		
		
		//check the return values
		assertEquals(200, response.getStatusLine().getStatusCode());
		CourseNodeVO contactNode = parse(body, CourseNodeVO.class);
		assertNotNull(contactNode);
		assertNotNull(contactNode.getId());
		
		//check the persisted value
		ICourse course = CourseFactory.loadCourse(course1.getResourceableId());
		TreeNode node = course.getEditorTreeModel().getNodeById(contactNode.getId());
		assertNotNull(node);
		CourseEditorTreeNode editorCourseNode = (CourseEditorTreeNode)node;
		CourseNode courseNode = editorCourseNode.getCourseNode();
		ModuleConfiguration config = courseNode.getModuleConfiguration();
		assertNotNull(config);
		
		assertEquals(config.getBooleanEntry(CONFIG_KEY_EMAILTOCOACHES), true);
		assertEquals(config.getBooleanEntry(CONFIG_KEY_EMAILTOPARTICIPANTS), true);
		
		List<String> tos = (List<String>)config.get(CONFIG_KEY_EMAILTOADRESSES);
		assertNotNull(tos);
		assertEquals(2, tos.size());

		assertEquals(config.get(CONFIG_KEY_MSUBJECT_DEFAULT), "Hello by contact 1");
		assertEquals(config.get(CONFIG_KEY_MBODY_DEFAULT), "Hello by contact 1 body");
	}
	
	private UriBuilder getElementsUri(ICourse course) {
		return UriBuilder.fromUri(getContextURI()).path("repo").path("courses").path(course.getResourceableId().toString()).path("elements");
	}

}
