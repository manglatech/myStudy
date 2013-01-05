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
package org.olat.group.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.Constants;
import org.olat.basesecurity.IdentityImpl;
import org.olat.basesecurity.SecurityGroup;
import org.olat.basesecurity.SecurityGroupMembershipImpl;
import org.olat.collaboration.CollaborationTools;
import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.services.mark.impl.MarkImpl;
import org.olat.core.id.Identity;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupImpl;
import org.olat.group.BusinessGroupOrder;
import org.olat.group.BusinessGroupShort;
import org.olat.group.BusinessGroupView;
import org.olat.group.model.BGRepositoryEntryRelation;
import org.olat.group.model.BGResourceRelation;
import org.olat.group.model.BusinessGroupMembershipViewImpl;
import org.olat.group.model.BusinessGroupShortImpl;
import org.olat.group.model.BusinessGroupViewImpl;
import org.olat.group.model.SearchBusinessGroupParams;
import org.olat.properties.Property;
import org.olat.repository.model.RepositoryEntryMembership;
import org.olat.resource.OLATResource;
import org.olat.resource.OLATResourceManager;
import org.olat.resource.accesscontrol.model.OfferImpl;
import org.olat.user.UserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
@Service("businessGroupDao")
public class BusinessGroupDAO {
	
	private OLog log = Tracing.createLoggerFor(BusinessGroupDAO.class);
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private OLATResourceManager olatResourceManager;
	@Autowired
	private BusinessGroupPropertyDAO businessGroupPropertyManager;
	
	public BusinessGroup createAndPersist(Identity creator, String name, String description,
			Integer minParticipants, Integer maxParticipants, boolean waitingListEnabled, boolean autoCloseRanksEnabled,
			boolean showOwners, boolean showParticipants, boolean showWaitingList) {

		BusinessGroupImpl businessgroup = null;
		//security groups
		SecurityGroup ownerGroup = securityManager.createAndPersistSecurityGroup();
		SecurityGroup participantGroup = securityManager.createAndPersistSecurityGroup();
		SecurityGroup waitingGroup = securityManager.createAndPersistSecurityGroup();
		
		businessgroup = new BusinessGroupImpl(name, description, ownerGroup, participantGroup, waitingGroup);
		if(minParticipants != null && minParticipants.intValue() >= 0) {
			businessgroup.setMinParticipants(minParticipants);
		}
		if(maxParticipants != null && maxParticipants.intValue() > 0) {
			businessgroup.setMaxParticipants(maxParticipants);
		}
		
		businessgroup.setWaitingListEnabled(waitingListEnabled);
		businessgroup.setAutoCloseRanksEnabled(autoCloseRanksEnabled);
		if(creator != null)
			businessgroup.setInstituteId(creator.getInstituteId());
		
		EntityManager em = dbInstance.getCurrentEntityManager();
		em.persist(businessgroup);
		
		if(log.isDebug()){
			log.debug("created Buddy Group named " + name + " for Identity " + creator);
		}
		/*
		 * policies: - ownerGroup can do everything on this businessgroup -> is an
		 * admin, can invite people to owner.- & partipiciantgroup -
		 * partipiciantGroup can read this businessgroup
		 */
		OLATResource businessgroupOlatResource =  olatResourceManager.createOLATResourceInstance(businessgroup);
		olatResourceManager.saveOLATResource(businessgroupOlatResource);
		businessgroup.setResource(businessgroupOlatResource);
		em.merge(businessgroup);

		//		securityManager.createAndPersistPolicy(ownerGroup, Constants.PERMISSION_ACCESS, businessgroup);
		securityManager.createAndPersistPolicyWithResource(ownerGroup, Constants.PERMISSION_ACCESS, businessgroupOlatResource);
		securityManager.createAndPersistPolicyWithResource(participantGroup, Constants.PERMISSION_READ, businessgroupOlatResource);
		// membership: add identity
		if (creator != null) {
			securityManager.addIdentityToSecurityGroup(creator, ownerGroup);
		}

		// per default all collaboration-tools are disabled

		// group members visibility
		businessGroupPropertyManager.createAndPersistDisplayMembers(businessgroup, showOwners, showParticipants, showWaitingList);
		return businessgroup;
	}
	
	public BusinessGroup load(Long id) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		BusinessGroup group = em.find(BusinessGroupImpl.class, id);
		return group;
	}
	
	public List<BusinessGroupShort> loadShort(Collection<Long> ids) {
		if(ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select bgi from ").append(BusinessGroupShortImpl.class.getName()).append(" bgi ")
		  .append(" where bgi.key in (:ids)");

		List<BusinessGroupShort> groups = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), BusinessGroupShort.class)
				.setParameter("ids", ids)
				.setHint("org.hibernate.cacheable", Boolean.TRUE)
				.getResultList();
		return groups;
	}
	
	public List<BusinessGroup> load(Collection<Long> ids) {
		if(ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select bgi from ").append(BusinessGroupImpl.class.getName()).append(" bgi ")
			.append(" inner join fetch bgi.ownerGroup ownerGroup")
			.append(" inner join fetch bgi.partipiciantGroup participantGroup")
			.append(" inner join fetch bgi.waitingGroup waitingGroup")
			.append(" inner join fetch bgi.resource resource")
		  .append(" where bgi.key in (:ids)");

		List<BusinessGroup> groups = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), BusinessGroup.class)
				.setParameter("ids", ids)
				.getResultList();
		return groups;
	}
	
	public List<BusinessGroup> loadAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("select bgi from ").append(BusinessGroupImpl.class.getName()).append(" bgi ")
			.append(" inner join fetch bgi.ownerGroup ownerGroup")
			.append(" inner join fetch bgi.partipiciantGroup participantGroup")
			.append(" inner join fetch bgi.waitingGroup waitingGroup")
			.append(" inner join fetch bgi.resource resource");

		List<BusinessGroup> groups = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), BusinessGroup.class)
				.getResultList();
		return groups;
	}
	
	public BusinessGroup loadForUpdate(Long id) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		//BusinessGroup group = em.find(BusinessGroupImpl.class, id, LockModeType.PESSIMISTIC_WRITE);
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("select bgi from ").append(BusinessGroupImpl.class.getName()).append(" bgi ")
			.append(" inner join fetch bgi.ownerGroup ownerGroup")
			.append(" inner join fetch bgi.partipiciantGroup participantGroup")
			.append(" inner join fetch bgi.waitingGroup waitingGroup")
			.append(" inner join fetch bgi.resource resource")
			.append(" where bgi.key=:key");
		
		return em.createQuery(sb.toString(), BusinessGroup.class)
				.setParameter("key", id).setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getSingleResult();
	}
	
	public BusinessGroup merge(BusinessGroup group) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		BusinessGroup mergedGroup = em.merge(group);
		return mergedGroup;
	}
	
	/**
	 * The method don't reload/reattach the object, make sure that you have
	 * reloaded the business group before trying to delete it.
	 * 
	 * @param group
	 */
	public void delete(BusinessGroup group) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		em.remove(group);
	}
	
	/**
	 * Work with the hibernate session
	 * @param group
	 * @return
	 */
	public BusinessGroup update(BusinessGroup group) {
		dbInstance.updateObject(group);
		return group;
	}
	
	public int countMembershipInfoInBusinessGroups(Identity identity, List<Long> groupKeys) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("select count(membership) from ").append(BusinessGroupMembershipViewImpl.class.getName()).append(" as membership ")
		  .append(" where membership.identityKey=:identId ");
		if(groupKeys != null && !groupKeys.isEmpty()) {
		  sb.append(" and (membership.ownerGroupKey in (:groupKeys) or membership.participantGroupKey in (:groupKeys) or membership.waitingGroupKey in (:groupKeys))");
		}
		
		TypedQuery<Number> query = dbInstance.getCurrentEntityManager().createQuery(sb.toString(), Number.class)
				.setParameter("identId", identity.getKey());
		if(groupKeys != null && !groupKeys.isEmpty()) {
			query.setParameter("groupKeys", groupKeys);
		}
		
		Number res = query.getSingleResult();
		return res.intValue();
	}

	public List<BusinessGroupMembershipViewImpl> getMembershipInfoInBusinessGroups(Collection<BusinessGroup> groups, List<Identity> identities) {
		List<Long> groupKeys = new ArrayList<Long>();
		for(BusinessGroup group:groups) {
			groupKeys.add(group.getKey());
		}
		Identity[] identityArr = identities.toArray(new Identity[identities.size()]);
		return getMembershipInfoInBusinessGroups(groupKeys, identityArr);
	}
	
	public List<BusinessGroupMembershipViewImpl> getMembershipInfoInBusinessGroups(Collection<Long> groupKeys, Identity... identity) {	
		StringBuilder sb = new StringBuilder(); 
		sb.append("select membership from ").append(BusinessGroupMembershipViewImpl.class.getName()).append(" as membership ");
		boolean and = false;
		if(identity != null && identity.length > 0) {
			and = and(sb, and);
		  sb.append("membership.identityKey in (:identIds) ");
		}
		if(groupKeys != null && !groupKeys.isEmpty()) {
			and = and(sb, and);
		  sb.append("(membership.ownerGroupKey in (:groupKeys) or membership.participantGroupKey in (:groupKeys) or membership.waitingGroupKey in (:groupKeys))");
		}
		
		TypedQuery<BusinessGroupMembershipViewImpl> query = dbInstance.getCurrentEntityManager().createQuery(sb.toString(), BusinessGroupMembershipViewImpl.class);
		if(identity != null && identity.length > 0) {
			List<Long> ids = new ArrayList<Long>(identity.length);
			for(Identity id:identity) {
				ids.add(id.getKey());
			}
			query.setParameter("identIds", ids);
		}	
		if(groupKeys != null && !groupKeys.isEmpty()) {
			query.setParameter("groupKeys", groupKeys);
		}
		
		List<BusinessGroupMembershipViewImpl> res = query.getResultList();
		return res;
	}

	public List<Long> isIdentityInBusinessGroups(Identity identity, boolean owner, boolean attendee, boolean waiting, List<BusinessGroup> groups) {
		if(groups == null || groups.isEmpty() || (!owner && !attendee && !waiting)) {
			return Collections.emptyList();
		}
		
		StringBuilder sb = new StringBuilder(); 
		sb.append("select bgi.key from ").append(BusinessGroupImpl.class.getName()).append(" as bgi ");
		if(!owner && !attendee && waiting) {
			sb.append(" inner join bgi.waitingGroup waitingGroup");
		} else if(owner && !attendee && !waiting) {
			sb.append(" inner join bgi.ownerGroup ownerGroup");
		} else if(!owner && attendee && !waiting) {
			sb.append(" inner join bgi.partipiciantGroup participantGroup");
		} else {
			sb.append(" left join bgi.ownerGroup ownerGroup");
			sb.append(" left join bgi.partipiciantGroup participantGroup");
			sb.append(" left join bgi.waitingGroup waitingGroup");
		}
		sb.append(" where bgi.key in (:groupKeys) and (");

		boolean or = false;
		if(owner) {
			or = or(sb, or);
			sb.append("ownerGroup.key in (select ownerMemberShip.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" ownerMemberShip ")
				.append(" where ownerMemberShip.identity.key=:identId ")
				.append(")");
		}
		if(attendee) {
			or = or(sb, or);
			sb.append(" participantGroup.key in (select partMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as partMembership ")
				.append("  where partMembership.identity.key=:identId")
				.append(" )");
		}
		if(waiting) {
			or = or(sb, or);
			sb.append(" waitingGroup.key in (select waitMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as waitMembership ")
				.append("  where waitMembership.identity.key=:identId")
				.append(" )");
		}
		sb.append(")");

		List<Long> groupKeys = new ArrayList<Long>();
		for(BusinessGroup group:groups) {
			groupKeys.add(group.getKey());
		}
		List<Long> res = dbInstance.getCurrentEntityManager().createQuery(sb.toString(), Long.class)
				.setParameter("groupKeys", groupKeys)
				.setParameter("identId", identity.getKey())
				.setHint("org.hibernate.cacheable", Boolean.TRUE)
				.getResultList();
		return res;
	}
	
	
	public BusinessGroup findBusinessGroup(SecurityGroup secGroup) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("select bgi from ").append(BusinessGroupImpl.class.getName()).append(" as bgi ")
			.append(" left join fetch bgi.ownerGroup ownerGroup")
			.append(" left join fetch bgi.partipiciantGroup participantGroup")
			.append(" left join fetch bgi.waitingGroup waitingGroup")
			.append(" inner join fetch bgi.resource resource")
			.append(" where (bgi.partipiciantGroup=:secGroup or bgi.ownerGroup=:secGroup or bgi.waitingGroup=:secGroup)");

		List<BusinessGroup> res = dbInstance.getCurrentEntityManager().createQuery(sb.toString(), BusinessGroup.class)
				.setParameter("secGroup", secGroup)
				.getResultList();

		if(res.isEmpty()) return null;
		return res.get(0);
	}
	
	public List<BusinessGroup> findBusinessGroupsWithWaitingListAttendedBy(Identity identity, OLATResource resource) {
		StringBuilder sb = new StringBuilder();
		if(resource == null) {
			sb.append("select bgs from ").append(BusinessGroupImpl.class.getName()).append(" as bgs ")
		    .append(" inner join fetch bgs.waitingGroup waitingList ")
			  .append(" inner join fetch bgs.ownerGroup ownerGroup ")
			  .append(" inner join fetch bgs.partipiciantGroup participantGroup ")
				.append(" inner join fetch bgs.resource bgResource")
		    .append(" where ");
		} else {
			sb.append("select bgs from ").append(BGResourceRelation.class.getName()).append(" as rel ")
			  .append(" inner join rel.group bgs ")
			  .append(" inner join fetch bgs.waitingGroup waitingList ")
			  .append(" inner join fetch bgs.ownerGroup ownerGroup ")
			  .append(" inner join fetch bgs.partipiciantGroup participantGroup ")
				.append(" inner join fetch bgs.resource bgResource")
		    .append(" where rel.resource.key=:resourceKey and ");
		}
		sb.append(" waitingList in (select memberShip.securityGroup from ").append(SecurityGroupMembershipImpl.class.getName()).append(" memberShip ")
      .append("   where memberShip.identity.key=:identityKey ")
      .append(" )");
		
		TypedQuery<BusinessGroup> query = dbInstance.getCurrentEntityManager().createQuery(sb.toString(), BusinessGroup.class)
				.setParameter("identityKey", identity.getKey());
		if(resource != null) {
			query.setParameter("resourceKey", resource.getKey());
		}
		List<BusinessGroup> groups = query.getResultList();
		return groups;
	}
	
	public int countBusinessGroups(SearchBusinessGroupParams params, OLATResource resource) {
		TypedQuery<Number> query = createFindDBQuery(params, resource, Number.class)
				.setHint("org.hibernate.cacheable", Boolean.TRUE);

		Number count = query.getSingleResult();
		return count.intValue();
	}
	
	public List<BusinessGroup> findBusinessGroups(SearchBusinessGroupParams params, OLATResource resource,
			int firstResult, int maxResults, BusinessGroupOrder... ordering) {
		TypedQuery<BusinessGroup> query = createFindDBQuery(params, resource, BusinessGroup.class, ordering);
		query.setFirstResult(firstResult);
		if(maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		List<BusinessGroup> groups = query.getResultList();
		return groups;
	}
	
	private <T> TypedQuery<T> createFindDBQuery(SearchBusinessGroupParams params, OLATResource resource, Class<T> resultClass, BusinessGroupOrder... ordering) {
		
		StringBuilder query = new StringBuilder();
		if(BusinessGroup.class.equals(resultClass)) {
			query.append("select distinct(bgi) from ");
		} else {
			query.append("select count(bgi.key) from ");
		}
		query.append(org.olat.group.BusinessGroupImpl.class.getName()).append(" as bgi ");

		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			//implicit joins
			query.append(", ").append(SecurityGroupMembershipImpl.class.getName()).append(" as sgmi ")
				   .append(", ").append(IdentityImpl.class.getName()).append(" identity")
				   .append(", ").append(UserImpl.class.getName()).append(" user ");
		}
		//inner joins
		if(BusinessGroup.class.equals(resultClass)) {
			query.append("inner join fetch bgi.ownerGroup ownerGroup ")
			     .append("inner join fetch bgi.partipiciantGroup participantGroup ")
			     .append("inner join fetch bgi.waitingGroup waitingGroup ")
			     .append("inner join fetch bgi.resource bgResource ");
		} else {
			query.append("inner join bgi.ownerGroup ownerGroup ")
			     .append("inner join bgi.partipiciantGroup participantGroup ")
			     .append("inner join bgi.waitingGroup waitingGroup ")
	         .append("inner join bgi.resource bgResource ");
		}

		boolean where = false;
		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			where = true;
			query.append(" where ownerGroup = sgmi.securityGroup")
				   .append(" and sgmi.identity = identity ")
				   .append(" and identity.user = user and ")
			//query the name in login, firstName and lastName
				   .append("(");
			searchLikeUserProperty(query, "firstName", "owner");
			query.append(" or ");
			searchLikeUserProperty(query, "lastName", "owner");
			query.append(" or ");
			searchLikeAttribute(query, "identity", "name", "owner");
			query.append(")");
		}
		
		if(params.getGroupKeys() != null && !params.getGroupKeys().isEmpty()) {
			where = where(query, where);
			query.append("bgi.key in (:groupKeys)");
		}
		if(params.getIdentity() != null){
			where = where(query, where);
			query.append("bgi.instituteId =:instituteId");
		}
		if(resource != null) {
			where = where(query, where);
			query.append("bgi.key in (")
			     .append("  select relation.group.key from ").append(BGResourceRelation.class.getName()).append(" relation where relation.resource.key=:resourceKey")
			     .append(")");
		}
		
		if(params.getResources() != null) {
			where = where(query, where);
			query.append(" bgi.key ").append(params.getResources().booleanValue() ? "" : "not").append(" in (")
           .append("   select bgRel.group.key from ").append(BGResourceRelation.class.getName()).append(" bgRel ")
			     .append(" )");
		}
		
		if(StringHelper.containsNonWhitespace(params.getCourseTitle())) {
			where = where(query, where);
			query.append(" bgi.key in (")
           .append("   select bgRel.groupKey from ").append(BGRepositoryEntryRelation.class.getName()).append(" bgRel ")
           .append("     where ");
			searchLikeAttribute(query, "bgRel", "repositoryEntryDisplayName", "displayName");
			query.append(" )");
		}
		
		if(params.isOwner() || params.isAttendee() || params.isWaiting()) {
			where = where(query, where);
			boolean subOr = false;
			query.append('(');
			if(params.isOwner()) {
				subOr = or(query, subOr);
				query.append("ownerGroup.key in (select ownerMemberShip.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" ownerMemberShip ")
				     .append(" where ownerMemberShip.identity.key=:identId ")
				     .append(")");
			}
			if(params.isAttendee()) {
				subOr = or(query, subOr);
				query.append(" participantGroup.key in (select partMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as partMembership ")
				     .append("  where partMembership.identity.key=:identId")
				     .append(" )");
			}
			if(params.isWaiting()) {
				subOr = or(query, subOr);
				query.append(" waitingGroup.key in (select waitMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as waitMembership ")
				     .append("  where waitMembership.identity.key=:identId")
				     .append(" )");
			}
			query.append(')');
		}
		
		if(params.getPublicGroups() != null) {
			where = where(query, where);
			if(params.getPublicGroups().booleanValue()) {
		    query.append(" bgResource.key in (")
		         .append("   select offer.resource.key from ").append(OfferImpl.class.getName()).append(" offer ")
		         .append("     where offer.valid=true")
		         .append("     and (offer.validFrom is null or offer.validFrom<=:atDate)")
						 .append("     and (offer.validTo is null or offer.validTo>=:atDate)")
						 .append(" )");
			} else {
		    query.append(" bgResource.key not in (")
             .append("   select offer.resource.key from ").append(OfferImpl.class.getName()).append(" offer ")
             .append("     where offer.valid=true")
				     .append(" )");
			}
		}
		
		if(params.getMarked() != null) {
			where = where(query, where);
			query.append(" bgi.key ").append(params.getMarked().booleanValue() ? "" : "not").append(" in (")
           .append("   select mark.resId from ").append(MarkImpl.class.getName()).append(" mark ")
           .append("     where mark.resName='BusinessGroup' and mark.creator.key=:identId")
			     .append(" )");
		}
		
		if(StringHelper.containsNonWhitespace(params.getNameOrDesc())) {
			where = where(query, where);
			query.append("(");
			searchLikeAttribute(query, "bgi", "name", "search");
			query.append(" or ");
			searchLikeAttribute(query, "bgi", "description", "search");
			query.append(")");
		} else {
			if(StringHelper.containsNonWhitespace(params.getExactName())) {
				where = where(query, where);
				query.append("bgi.name=:exactName");
			}
			if(StringHelper.containsNonWhitespace(params.getName())) {
				where = where(query, where);
				searchLikeAttribute(query, "bgi", "name", "name");
			}
			if(StringHelper.containsNonWhitespace(params.getDescription())) {
				where = where(query, where);
				searchLikeAttribute(query, "bgi", "description", "description");
			}
		}
		
		if(params.getTools() != null && !params.getTools().isEmpty()) {
			where = where(query, where);
			query.append("bgi.key in (select prop.resourceTypeId from ").append(Property.class.getName()).append(" prop")
				.append(" where prop.category='").append(CollaborationTools.PROP_CAT_BG_COLLABTOOLS).append("'")
				.append(" and prop.name in (:tools) and prop.stringValue='true' and prop.resourceTypeName='BusinessGroup')");
		}
		//order by (not for count)
		if(BusinessGroup.class.equals(resultClass)) {
			query.append(" order by ");
			if(ordering != null && ordering.length > 0) {
				for(BusinessGroupOrder o:ordering) {
					switch(o) {
						case nameAsc: query.append("bgi.name,");break;
						case nameDesc: query.append("bgi.name desc,");break;
						case creationDateAsc: query.append("bgi.creationDate,");break;
						case creationDateDesc: query.append("bgi.creationDate desc,");break;
					}
				}
			} else {
				query.append("bgi.name,");
			}
			query.append("bgi.key");
		}

		TypedQuery<T> dbq = dbInstance.getCurrentEntityManager().createQuery(query.toString(), resultClass);
		//add parameters
		if(params.isOwner() || params.isAttendee() || params.isWaiting() || params.getMarked() != null) {
			dbq.setParameter("identId", params.getIdentity().getKey());
		}
		if(params.getIdentity() != null){
			dbq.setParameter("instituteId", params.getIdentity().getInstituteId());
		}
		if(params.getPublicGroups() != null && params.getPublicGroups().booleanValue()) {
			dbq.setParameter("atDate", new Date(), TemporalType.TIMESTAMP);
		}
		if(params.getGroupKeys() != null && !params.getGroupKeys().isEmpty()) {
			dbq.setParameter("groupKeys", params.getGroupKeys());
		}
		
		if (resource != null) {
			dbq.setParameter("resourceKey", resource.getKey());
		}
		if(params.getTools() != null && !params.getTools().isEmpty()) {
			dbq.setParameter("tools", params.getTools());
		}
		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			dbq.setParameter("owner", makeFuzzyQueryString(params.getOwnerName()));
		}
		if(StringHelper.containsNonWhitespace(params.getNameOrDesc())) {
			dbq.setParameter("search", makeFuzzyQueryString(params.getNameOrDesc()));
		} else {
			if(StringHelper.containsNonWhitespace(params.getExactName())) {
				dbq.setParameter("exactName", params.getExactName());
			}
			if(StringHelper.containsNonWhitespace(params.getName())) {
				dbq.setParameter("name", makeFuzzyQueryString(params.getName()));
			}
			if(StringHelper.containsNonWhitespace(params.getDescription())) {
				dbq.setParameter("description", makeFuzzyQueryString(params.getDescription()));
			}
		}
		if(StringHelper.containsNonWhitespace(params.getCourseTitle())) {
			dbq.setParameter("displayName", makeFuzzyQueryString(params.getCourseTitle()));
		}
		return dbq;
	}
	
	public List<BusinessGroupView> findBusinessGroupWithAuthorConnection(Identity author) {
		StringBuilder sb = new StringBuilder();
		sb.append("select bgi from ").append(BusinessGroupViewImpl.class.getName()).append(" as bgi ")
		  .append("where bgi.key in (")
		  .append("  select rel.group.key from ").append(BGResourceRelation.class.getName()).append(" as rel ")
		  .append("  where rel.resource.key in (")
		  .append("    select membership.ownerResourceKey from ").append(RepositoryEntryMembership.class.getName()).append(" as membership")
		  .append("    where membership.identityKey=:authorKey and membership.ownerRepoKey is not null")
		  .append("  )")
		  .append(")");

		List<BusinessGroupView> groups = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), BusinessGroupView.class)
				.setParameter("authorKey", author.getKey())
				.getResultList();
		return groups;
	}

	public int countBusinessGroupViews(SearchBusinessGroupParams params, OLATResource resource) {
		TypedQuery<Number> query = createFindViewDBQuery(params, resource, Number.class)
				.setHint("org.hibernate.cacheable", Boolean.TRUE);

		Number count = query.getSingleResult();
		return count.intValue();
	}
	
	public List<BusinessGroupView> findBusinessGroupViews(SearchBusinessGroupParams params, OLATResource resource,
			int firstResult, int maxResults, BusinessGroupOrder... ordering) {
		TypedQuery<BusinessGroupView> query = createFindViewDBQuery(params, resource, BusinessGroupView.class, ordering);
		query.setFirstResult(firstResult);
		if(maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		List<BusinessGroupView> groups = query.getResultList();
		return groups;
	}
	
	private <T> TypedQuery<T> createFindViewDBQuery(SearchBusinessGroupParams params, OLATResource resource, Class<T> resultClass, BusinessGroupOrder... ordering) {
		
		StringBuilder query = new StringBuilder();
		if(BusinessGroupView.class.equals(resultClass)) {
			query.append("select distinct(bgi) from ");
		} else {
			query.append("select count(bgi.key) from ");
		}
		query.append(BusinessGroupViewImpl.class.getName()).append(" as bgi ");

		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			//implicit joins
			query.append(", ").append(SecurityGroupMembershipImpl.class.getName()).append(" as sgmi ")
				   .append(", ").append(IdentityImpl.class.getName()).append(" identity")
				   .append(", ").append(UserImpl.class.getName()).append(" user ");
		}
		//inner joins
		if(BusinessGroupView.class.equals(resultClass)) {
			query.append("inner join fetch bgi.ownerGroup ownerGroup ")
			     .append("inner join fetch bgi.partipiciantGroup participantGroup ")
			     .append("inner join fetch bgi.waitingGroup waitingGroup ")
			     .append("inner join fetch bgi.resource bgResource ");
		} else {
			query.append("inner join bgi.ownerGroup ownerGroup ")
			     .append("inner join bgi.partipiciantGroup participantGroup ")
			     .append("inner join bgi.waitingGroup waitingGroup ")
	         .append("inner join bgi.resource bgResource ");
		}

		boolean where = false;
		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			where = true;
			query.append(" where ownerGroup = sgmi.securityGroup")
				   .append(" and sgmi.identity = identity ")
				   .append(" and identity.user = user and ")
			//query the name in login, firstName and lastName
				   .append("(");
			searchLikeUserProperty(query, "firstName", "owner");
			query.append(" or ");
			searchLikeUserProperty(query, "lastName", "owner");
			query.append(" or ");
			searchLikeAttribute(query, "identity", "name", "owner");
			query.append(")");
		}
		
		if(params.getGroupKeys() != null && !params.getGroupKeys().isEmpty()) {
			where = where(query, where);
			query.append("bgi.key in (:groupKeys)");
		}
		if(params.getIdentity() != null){
			where = where(query, where);
			query.append("bgi.instituteId =:instituteId");
		}
		if(resource != null) {
			where = where(query, where);
			query.append("bgi.key in (")
			     .append("  select relation.group.key from ").append(BGResourceRelation.class.getName()).append(" relation where relation.resource.key=:resourceKey")
			     .append(")");
		}
		
		if(params.getResources() != null) {
			where = where(query, where);
			query.append(" bgi.numOfRelations").append(params.getResources().booleanValue() ? ">0" : "<=0");
		}
		
		if(StringHelper.containsNonWhitespace(params.getCourseTitle())) {
			where = where(query, where);
			query.append(" bgi.key in (")
           .append("   select bgRel.groupKey from ").append(BGRepositoryEntryRelation.class.getName()).append(" bgRel ")
           .append("     where ");
			searchLikeAttribute(query, "bgRel", "repositoryEntryDisplayName", "displayName");
			query.append(" )");
		}
		
		if(params.isOwner() || params.isAttendee() || params.isWaiting()) {
			where = where(query, where);
			boolean subOr = false;
			query.append('(');
			if(params.isOwner()) {
				subOr = or(query, subOr);
				query.append("ownerGroup.key in (select ownerMemberShip.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" ownerMemberShip ")
				     .append(" where ownerMemberShip.identity.key=:identId ")
				     .append(")");
			}
			if(params.isAttendee()) {
				subOr = or(query, subOr);
				query.append(" participantGroup.key in (select partMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as partMembership ")
				     .append("  where partMembership.identity.key=:identId")
				     .append(" )");
			}
			if(params.isWaiting()) {
				subOr = or(query, subOr);
				query.append(" waitingGroup.key in (select waitMembership.securityGroup.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as waitMembership ")
				     .append("  where waitMembership.identity.key=:identId")
				     .append(" )");
			}
			query.append(')');
		}
		
		if(params.getPublicGroups() != null) {
			where = where(query, where);
			if(params.getPublicGroups().booleanValue()) {
		    query.append(" bgi.numOfValidOffers>0");
			} else {
		    query.append(" bgi.numOfOffers<=0");
			}
		}
		
		if(params.getMarked() != null) {
			where = where(query, where);
			query.append(" bgi.key ").append(params.getMarked().booleanValue() ? "" : "not").append(" in (")
           .append("   select mark.resId from ").append(MarkImpl.class.getName()).append(" mark ")
           .append("     where mark.resName='BusinessGroup' and mark.creator.key=:identId")
			     .append(" )");
		}
		
		if(params.isHeadless()) {
			where = where(query, where);
			query.append(" bgi.numOfRelations=0 and bgi.numOfOwners=0 and bgi.numOfParticipants=0");
		}
		
		if(params.getNumOfMembers() > -1) {
			where = where(query, where);
			if(params.isNumOfMembersBigger()) {
				query.append(" (bgi.numOfOwners + bgi.numOfParticipants)>=").append(params.getNumOfMembers());
			} else {
				query.append(" (bgi.numOfOwners + bgi.numOfParticipants)<=").append(params.getNumOfMembers());
			}	
		}
		
		if(StringHelper.containsNonWhitespace(params.getNameOrDesc())) {
			where = where(query, where);
			query.append("(");
			searchLikeAttribute(query, "bgi", "name", "search");
			query.append(" or ");
			searchLikeAttribute(query, "bgi", "description", "search");
			query.append(")");
		} else {
			if(StringHelper.containsNonWhitespace(params.getExactName())) {
				where = where(query, where);
				query.append("bgi.name=:exactName");
			}
			if(StringHelper.containsNonWhitespace(params.getName())) {
				where = where(query, where);
				searchLikeAttribute(query, "bgi", "name", "name");
			}
			if(StringHelper.containsNonWhitespace(params.getDescription())) {
				where = where(query, where);
				searchLikeAttribute(query, "bgi", "description", "description");
			}
		}
		
		if(params.getTools() != null && !params.getTools().isEmpty()) {
			where = where(query, where);
			query.append("bgi.key in (select prop.resourceTypeId from ").append(Property.class.getName()).append(" prop")
				.append(" where prop.category='").append(CollaborationTools.PROP_CAT_BG_COLLABTOOLS).append("'")
				.append(" and prop.name in (:tools) and prop.stringValue='true' and prop.resourceTypeName='BusinessGroup')");
		}
		//order by (not for count)
		if(BusinessGroupView.class.equals(resultClass)) {
			query.append(" order by ");
			if(ordering != null && ordering.length > 0) {
				for(BusinessGroupOrder o:ordering) {
					switch(o) {
						case nameAsc: query.append("bgi.name,");break;
						case nameDesc: query.append("bgi.name desc,");break;
						case creationDateAsc: query.append("bgi.creationDate,");break;
						case creationDateDesc: query.append("bgi.creationDate desc,");break;
					}
				}
			} else {
				query.append("bgi.name,");
			}
			query.append("bgi.key");
		}

		TypedQuery<T> dbq = dbInstance.getCurrentEntityManager().createQuery(query.toString(), resultClass);
		//add parameters
		if(params.isOwner() || params.isAttendee() || params.isWaiting() || params.getMarked() != null) {
			dbq.setParameter("identId", params.getIdentity().getKey());
		}
		if(params.getIdentity() != null){
			dbq.setParameter("instituteId", params.getIdentity().getInstituteId());
		}
		if(params.getGroupKeys() != null && !params.getGroupKeys().isEmpty()) {
			dbq.setParameter("groupKeys", params.getGroupKeys());
		}
		if (resource != null) {
			dbq.setParameter("resourceKey", resource.getKey());
		}
		if(params.getTools() != null && !params.getTools().isEmpty()) {
			dbq.setParameter("tools", params.getTools());
		}
		if(StringHelper.containsNonWhitespace(params.getOwnerName())) {
			dbq.setParameter("owner", makeFuzzyQueryString(params.getOwnerName()));
		}
		if(StringHelper.containsNonWhitespace(params.getNameOrDesc())) {
			dbq.setParameter("search", makeFuzzyQueryString(params.getNameOrDesc()));
		} else {
			if(StringHelper.containsNonWhitespace(params.getExactName())) {
				dbq.setParameter("exactName", params.getExactName());
			}
			if(StringHelper.containsNonWhitespace(params.getName())) {
				dbq.setParameter("name", makeFuzzyQueryString(params.getName()));
			}
			if(StringHelper.containsNonWhitespace(params.getDescription())) {
				dbq.setParameter("description", makeFuzzyQueryString(params.getDescription()));
			}
		}
		if(StringHelper.containsNonWhitespace(params.getCourseTitle())) {
			dbq.setParameter("displayName", makeFuzzyQueryString(params.getCourseTitle()));
		}
		return dbq;
	}
	
	public int countContacts(Identity identity) {
		List<Long> result = createContactsQuery(identity, Long.class).getResultList();
		result.remove(identity.getKey());//not always a contact of myself with this query
		return result.size();
	}

	public List<Identity> findContacts(Identity identity, int firstResult, int maxResults) {
		TypedQuery<Identity> query = createContactsQuery(identity, Identity.class);
		query.setFirstResult(firstResult);
		if(maxResults > 0) {
			query.setMaxResults(maxResults + 1);
		}
		List<Identity> contacts = query.getResultList();
		if(!contacts.remove(identity) && maxResults > 0 && contacts.size() > maxResults) {
			contacts.remove(contacts.size() - 1);
		}
		return contacts;
	}
	
	private <T> TypedQuery<T> createContactsQuery(Identity identity, Class<T> resultClass) {
		StringBuilder query = new StringBuilder();
		if(Identity.class.equals(resultClass)) {
			query.append("select distinct identity from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as sgmi ");
		} else {
			query.append("select distinct identity.key from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as sgmi ");
		}
		query.append(" inner join sgmi.identity as identity ")
		     .append(" inner join sgmi.securityGroup as secGroup ")
		     .append(" where ")
		     .append("  secGroup in (")
		     .append("    select bg1.ownerGroup from ").append(BusinessGroupImpl.class.getName()).append(" as bg1,").append(Property.class.getName()).append(" as prop where prop.grp=bg1 and prop.name='displayMembers' and prop.longValue in (1,3,5,7)")
		     .append("      and bg1.ownerGroup in (select ownerSgmi.securityGroup from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as ownerSgmi where ownerSgmi.identity.key=:identKey)")
		     .append("  ) or")
		     .append("  secGroup in (")
		     .append("    select bg3.ownerGroup from ").append(BusinessGroupImpl.class.getName()).append(" as bg3,").append(Property.class.getName()).append(" as prop where prop.grp=bg3 and prop.name='displayMembers' and prop.longValue in (1,3,5,7)")
		     .append("      and bg3.partipiciantGroup in (select partSgmi.securityGroup from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as partSgmi where partSgmi.identity.key=:identKey)")
		     .append("  ) or")
		     .append("  secGroup in (")
		     .append("    select bg2.partipiciantGroup from ").append(BusinessGroupImpl.class.getName()).append(" as bg2,").append(Property.class.getName()).append(" as prop where prop.grp=bg2 and prop.name='displayMembers' and prop.longValue in (2,3,6,7)")
		     .append("      and bg2.partipiciantGroup in (select partSgmi.securityGroup from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as partSgmi where partSgmi.identity.key=:identKey)")
		     .append("  ) or")
		     .append("  secGroup in (")
		     .append("    select bg4.partipiciantGroup from ").append(BusinessGroupImpl.class.getName()).append(" as bg4,").append(Property.class.getName()).append(" as prop where prop.grp=bg4 and prop.name='displayMembers' and prop.longValue in (2,3,6,7)")
		     .append("      and bg4.ownerGroup in (select ownerSgmi.securityGroup from ").append(SecurityGroupMembershipImpl.class.getName()).append(" as ownerSgmi where ownerSgmi.identity.key=:identKey)")
		     .append("  )");
		if(Identity.class.equals(resultClass)) {
			query.append("order by identity.name");
		}

		TypedQuery<T> db = dbInstance.getCurrentEntityManager().createQuery(query.toString(), resultClass);
		db.setParameter("identKey", identity.getKey());
		return db;
	}
	
	private StringBuilder searchLikeUserProperty(StringBuilder sb, String key, String var) {
		if(dbInstance.getDbVendor().equals("mysql")) {
			sb.append(" user.properties['").append(key).append("'] like :").append(var);
		} else {
			sb.append(" lower(user.properties['").append(key).append("']) like :").append(var);
			if(dbInstance.getDbVendor().equals("oracle")) {
	 	 		sb.append(" escape '\\'");
	 	 	}
		}
		return sb;
	}
	
	private StringBuilder searchLikeAttribute(StringBuilder sb, String objName, String attribute, String var) {
		if(dbInstance.getDbVendor().equals("mysql")) {
			sb.append(" ").append(objName).append(".").append(attribute).append(" like :").append(var);
		} else {
			sb.append(" lower(").append(objName).append(".").append(attribute).append(") like :").append(var);
			if(dbInstance.getDbVendor().equals("oracle")) {
	 	 		sb.append(" escape '\\'");
	 	 	}
		}
		return sb;
	}
	
	private String makeFuzzyQueryString(String string) {
		// By default only fuzzyfy at the end. Usually it makes no sense to do a
		// fuzzy search with % at the beginning, but it makes the query very very
		// slow since it can not use any index and must perform a fulltext search.
		// User can always use * to make it a really fuzzy search query
		string = string.replace('*', '%');
		string = string + "%";
		// with 'LIKE' the character '_' is a wildcard which matches exactly one character.
		// To test for literal instances of '_', we have to escape it.
		string = string.replace("_", "\\_");
		return string.toLowerCase();
	}
	
	private final boolean where(StringBuilder sb, boolean where) {
		if(where) {
			sb.append(" and ");
		} else {
			sb.append(" where ");
		}
		return true;
	}
	
	private final boolean and(StringBuilder sb, boolean and) {
		if(and) sb.append(" and ");
		else sb.append(" where ");
		return true;
	}
	
	private final boolean or(StringBuilder sb, boolean or) {
		if(or) sb.append(" or ");
		else sb.append(" ");
		return true;
	}
}
