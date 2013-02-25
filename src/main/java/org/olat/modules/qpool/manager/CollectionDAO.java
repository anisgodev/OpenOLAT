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
package org.olat.modules.qpool.manager;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.PersistenceHelper;
import org.olat.core.commons.persistence.SortKey;
import org.olat.core.id.Identity;
import org.olat.modules.qpool.QuestionItem;
import org.olat.modules.qpool.QuestionItemCollection;
import org.olat.modules.qpool.model.CollectionToItem;
import org.olat.modules.qpool.model.QuestionItemCollectionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 22.02.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service("qcollectionDao")
public class CollectionDAO {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private QuestionItemDAO questionItemDao;
	
	public QuestionItemCollection createCollection(String name, Identity identity) {
		QuestionItemCollectionImpl collection = new QuestionItemCollectionImpl();
		collection.setCreationDate(new Date());
		collection.setLastModified(new Date());
		collection.setName(name);
		collection.setOwner(identity);
		dbInstance.getCurrentEntityManager().persist(collection);
		return collection;
	}
	
	public QuestionItemCollection loadCollectionById(Long key) {
		StringBuilder sb = new StringBuilder();
		sb.append("select coll from qcollection coll where coll.key=:key");
		List<QuestionItemCollection> items = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), QuestionItemCollection.class)
				.setParameter("key", key)
				.getResultList();
		if(items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}
	
	public void addItemToCollection(QuestionItem item, QuestionItemCollection collection) {
		QuestionItem lockedItem = questionItemDao.loadForUpdate(item.getKey());
		if(!isInCollection(collection, lockedItem)) {
			CollectionToItem coll2Item = new CollectionToItem();
			coll2Item.setCreationDate(new Date());
			coll2Item.setCollection(collection);
			coll2Item.setItem(lockedItem);
			dbInstance.getCurrentEntityManager().persist(coll2Item);
		}
		dbInstance.commit();
	}
	
	public boolean isInCollection(QuestionItemCollection collection, QuestionItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(coll2item) from qcollection2item coll2item where coll2item.collection.key=:collectionKey and coll2item.item.key=:itemKey");
		Number count = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Number.class)
				.setParameter("collectionKey", collection.getKey())
				.setParameter("itemKey", item.getKey())
				.getSingleResult().intValue();
		return count.intValue() > 0;
	}
	
	public int countItemsOfCollection(QuestionItemCollection collection) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(coll2item) from qcollection2item coll2item where coll2item.collection.key=:collectionKey");
		
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Number.class)
				.setParameter("collectionKey", collection.getKey())
				.getSingleResult().intValue();
	}
	
	public List<QuestionItem> getItemsOfCollection(QuestionItemCollection collection, int firstResult, int maxResults, SortKey... orderBy) {
		StringBuilder sb = new StringBuilder();
		sb.append("select coll2item.item from qcollection2item coll2item where coll2item.collection.key=:collectionKey");
		PersistenceHelper.appendGroupBy(sb, "coll2item.item", orderBy);
		
		TypedQuery<QuestionItem> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), QuestionItem.class)
				.setParameter("collectionKey", collection.getKey());
		if(firstResult >= 0) {
			query.setFirstResult(firstResult);
		}
		if(maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}
	
	public List<QuestionItemCollection> getCollections(Identity me) {
		StringBuilder sb = new StringBuilder();
		sb.append("select coll from qcollection coll where coll.owner.key=:identityKey");
		
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), QuestionItemCollection.class)
				.setParameter("identityKey", me.getKey())
				.getResultList();
	}
}
