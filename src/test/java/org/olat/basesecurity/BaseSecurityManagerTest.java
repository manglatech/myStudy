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
package org.olat.basesecurity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.id.Roles;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.core.util.Encoder;
import org.olat.resource.OLATResource;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test the basic functions of the base security manager.
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class BaseSecurityManagerTest extends OlatTestCase {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	
	
	@Test
	public void testCreateIdentity() {
		String name = "createid-" + UUID.randomUUID().toString();
		User user = userManager.createUser("first" + name, "last" + name, name + "@frentix.com");
		Identity identity = securityManager.createAndPersistIdentityAndUser(name, user, BaseSecurityModule.getDefaultAuthProviderIdentifier(), name, Encoder.encrypt("secret"));
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(identity);
		Assert.assertNotNull(identity.getKey());
		Assert.assertEquals(name, identity.getName());
		Assert.assertNotNull(identity.getUser());
		Assert.assertEquals(user, identity.getUser());
		Assert.assertEquals("first" + name, identity.getUser().getProperty(UserConstants.FIRSTNAME, null));
		Assert.assertEquals("last" + name, identity.getUser().getProperty(UserConstants.LASTNAME, null));
		Assert.assertEquals(name + "@frentix.com", identity.getUser().getProperty(UserConstants.EMAIL, null));
	}
	
	/**
	 * This test is primarily made against Oracle
	 */
	@Test
	public void testCreateUpdateIdentity() {
		String name = "update-id-" + UUID.randomUUID().toString();
		User user = userManager.createUser("first" + name, "last" + name, name + "@frentix.com");
		user.setProperty(UserConstants.COUNTRY, "");
		user.setProperty(UserConstants.CITY, "Basel");
		Identity identity = securityManager.createAndPersistIdentityAndUser(name, user, BaseSecurityModule.getDefaultAuthProviderIdentifier(), name, Encoder.encrypt("secret"));
		dbInstance.commitAndCloseSession();
		
		//reload and update
		Identity identityPrime = securityManager.loadIdentityByKey(identity.getKey());
		identityPrime.getUser().setProperty(UserConstants.FIRSTNAME, "firstname");
		identityPrime.getUser().setProperty(UserConstants.COUNTRY, "CH");
		identityPrime.getUser().setProperty(UserConstants.CITY, "Lausanne");
		userManager.updateUserFromIdentity(identityPrime);
		dbInstance.commitAndCloseSession();
		
		//reload and check
		Identity identitySecond = securityManager.loadIdentityByKey(identity.getKey());
		Assert.assertEquals("firstname", identitySecond.getUser().getProperty(UserConstants.FIRSTNAME, null));
		Assert.assertEquals("last" + name, identitySecond.getUser().getProperty(UserConstants.LASTNAME, null));
		Assert.assertEquals(name + "@frentix.com", identitySecond.getUser().getProperty(UserConstants.EMAIL, null));
		Assert.assertEquals("CH", identitySecond.getUser().getProperty(UserConstants.COUNTRY, null));
		Assert.assertEquals("Lausanne", identitySecond.getUser().getProperty(UserConstants.CITY, null));
	}
	
	
	@Test
	public void testEquals() {
		String identityTest1Name = "eq-1-" + UUID.randomUUID().toString();
		Identity ident1 = JunitTestHelper.createAndPersistIdentityAsUser(identityTest1Name);
		Identity ident2 = JunitTestHelper.createAndPersistIdentityAsUser("eq-2-" + UUID.randomUUID().toString());
		
		assertFalse("Wrong equals implementation, different types are recognized as equals ",ident1.equals(new Integer(1)));
		assertFalse("Wrong equals implementation, different users are recognized as equals ",ident1.equals(ident2));
		assertFalse("Wrong equals implementation, null value is recognized as equals ",ident1.equals(null));
		assertTrue("Wrong equals implementation, same users are NOT recognized as equals ",ident1.equals(ident1));
		Identity ident1_2 = securityManager.findIdentityByName(identityTest1Name);
		assertTrue("Wrong equals implementation, same users are NOT recognized as equals ",ident1.equals(ident1_2));
	}
	
	@Test
	public void testHashCode() {
		String identityTest1Name = "hash-1-" + UUID.randomUUID().toString();
		Identity ident1 = JunitTestHelper.createAndPersistIdentityAsUser(identityTest1Name);
		Identity ident2 = JunitTestHelper.createAndPersistIdentityAsUser("hash-2-" + UUID.randomUUID().toString());
		
		assertTrue("Wrong hashCode implementation, same users have NOT same hash-code ",ident1.hashCode() == ident1.hashCode());
		assertFalse("Wrong hashCode implementation, different users have same hash-code",ident1.hashCode() == ident2.hashCode());
		Identity ident1_2 = securityManager.findIdentityByName(identityTest1Name);
		assertTrue("Wrong hashCode implementation, same users have NOT same hash-code ",ident1.hashCode() == ident1_2.hashCode());
	}

	@Test
	public void testFindIdentityByUser() {
		//create a user it
		String username = "find-me-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(username);
		Assert.assertNotNull(id);
		Assert.assertNotNull(id.getUser());
		dbInstance.commitAndCloseSession();
		
		//find it
		Identity foundId = securityManager.findIdentityByUser(id.getUser());
		Assert.assertNotNull(foundId);
		Assert.assertEquals(username, foundId.getName());
		Assert.assertEquals(id, foundId);
		Assert.assertEquals(id.getUser(), foundId.getUser());
	}
	
	@Test
	public void testFindIdentityByName() {
		//create a user it
		String name = "find-me-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(name);
		Assert.assertNotNull(id);
		Assert.assertEquals(name, id.getName());
		dbInstance.commitAndCloseSession();
		
		//find it
		Identity foundId = securityManager.findIdentityByName(name);
		Assert.assertNotNull(foundId);
		Assert.assertEquals(name, foundId.getName());
		Assert.assertEquals(id, foundId);
	}
	
	@Test
	public void testFindIdentityByNames() {
		//create a user it
		String name1 = "find-me-1-" + UUID.randomUUID().toString();
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser(name1);
		String name2 = "find-me-2-" + UUID.randomUUID().toString();
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser(name2);
		dbInstance.commitAndCloseSession();
		
		//find it
		List<String> names = new ArrayList<String>(2);
		names.add(name1);
		names.add(name2);
		List<Identity> foundIds = securityManager.findIdentitiesByName(names);
		Assert.assertNotNull(foundIds);
		Assert.assertEquals(2, foundIds.size());
		Assert.assertTrue(foundIds.contains(id1));
		Assert.assertTrue(foundIds.contains(id2));
	}
	
	@Test
	public void testGetSecurityGroupsForIdentity() {
		// create
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser( "find-sec-" + UUID.randomUUID().toString());
		SecurityGroup secGroup1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup2 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup3 = securityManager.createAndPersistSecurityGroup();
		securityManager.addIdentityToSecurityGroup(id, secGroup1);
		securityManager.addIdentityToSecurityGroup(id, secGroup2);
		dbInstance.commitAndCloseSession();
		
		//check
		List<SecurityGroup> secGroups = securityManager.getSecurityGroupsForIdentity(id);
		Assert.assertNotNull(secGroups);
		Assert.assertTrue(secGroups.contains(secGroup1));
		Assert.assertTrue(secGroups.contains(secGroup2));
		Assert.assertFalse(secGroups.contains(secGroup3));
	}
	
	@Test
	public void testCreateNamedGroup() {
		String name = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		SecurityGroup ng = securityManager.createAndPersistNamedSecurityGroup(name);
		dbInstance.commitAndCloseSession();
		
		SecurityGroup sgFound = securityManager.findSecurityGroupByName(name);
		Assert.assertNotNull(sgFound);
		Assert.assertEquals(sgFound.getKey(), ng.getKey());
	}
	
	@Test
	public void testRemoveFromSecurityGroup() {
		//create a security group with 2 identites
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser( "rm-1-sec-" + UUID.randomUUID().toString());
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser( "rm-2-sec-" + UUID.randomUUID().toString());
		SecurityGroup secGroup = securityManager.createAndPersistSecurityGroup();
		securityManager.addIdentityToSecurityGroup(id1, secGroup);
		securityManager.addIdentityToSecurityGroup(id2, secGroup);
		dbInstance.commitAndCloseSession();
		
		//remove the first one
		securityManager.removeIdentityFromSecurityGroup(id1, secGroup);
		dbInstance.commitAndCloseSession();
		
		int countMembers = securityManager.countIdentitiesOfSecurityGroup(secGroup);
		Assert.assertEquals(1, countMembers);
		List<Identity> members = securityManager.getIdentitiesOfSecurityGroup(secGroup);
		Assert.assertNotNull(members);
		Assert.assertEquals(1, members.size());
		Assert.assertEquals(id2, members.get(0));
	}
	
	/**
	 * 
	 */
	@Test
	public void testLoadIdentityByKeys() {
		//create a security group with 2 identites
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser( "load-1-sec-" + UUID.randomUUID().toString());
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser( "load-2-sec-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		
		List<Long> keys = new ArrayList<Long>(2);
		keys.add(id1.getKey());
		keys.add(id2.getKey());
		List<Identity> identities = securityManager.loadIdentityByKeys(keys);
		Assert.assertNotNull(identities);
		Assert.assertEquals(2, identities.size());
		Assert.assertTrue(identities.contains(id1));
		Assert.assertTrue(identities.contains(id2));
	}
	
	/**
	 * Update roles
	 */
	@Test
	public void testUpdateRoles_giveAllRights() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser( "roles-" + UUID.randomUUID().toString());
		Roles roles = securityManager.getRoles(id1);
		Assert.assertNotNull(roles);
		dbInstance.commitAndCloseSession();

		//update roles
		Roles modifiedRoles = new Roles(true, true, true, true, false, true, false);
		securityManager.updateRoles(id1, modifiedRoles);
		dbInstance.commitAndCloseSession();
		
		//check roles
		Roles reloadRoles = securityManager.getRoles(id1);
		Assert.assertNotNull(reloadRoles);
		Assert.assertTrue(reloadRoles.isAuthor());
		Assert.assertTrue(reloadRoles.isGroupManager());
		Assert.assertFalse(reloadRoles.isGuestOnly());
		Assert.assertTrue(reloadRoles.isInstitutionalResourceManager());
		Assert.assertFalse(reloadRoles.isInvitee());
		Assert.assertTrue(reloadRoles.isOLATAdmin());
		Assert.assertTrue(reloadRoles.isUserManager());
	}
	
	/**
	 * Update roles
	 */
	@Test
	public void testUpdateRoles_someRights() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser( "roles-" + UUID.randomUUID().toString());
		Roles roles = securityManager.getRoles(id1);
		Assert.assertNotNull(roles);
		dbInstance.commitAndCloseSession();

		//update roles
		Roles modifiedRoles = new Roles(false, true, false, true, false, false, false);
		securityManager.updateRoles(id1, modifiedRoles);
		dbInstance.commitAndCloseSession();
		
		//check roles
		Roles reloadRoles = securityManager.getRoles(id1);
		Assert.assertNotNull(reloadRoles);
		Assert.assertTrue(reloadRoles.isAuthor());
		Assert.assertFalse(reloadRoles.isGroupManager());
		Assert.assertFalse(reloadRoles.isGuestOnly());
		Assert.assertFalse(reloadRoles.isInstitutionalResourceManager());
		Assert.assertFalse(reloadRoles.isInvitee());
		Assert.assertFalse(reloadRoles.isOLATAdmin());
		Assert.assertTrue(reloadRoles.isUserManager());
	}
	
	/**
	 * Update roles, check that invitee don't become rights
	 */
	@Test
	public void testUpdateRoles_guest() {
		Identity invitee = JunitTestHelper.createAndPersistIdentityAsUser("invitee-" + UUID.randomUUID().toString());
		Roles roles = securityManager.getRoles(invitee);
		Assert.assertNotNull(roles);
		dbInstance.commitAndCloseSession();

		//update roles
		Roles modifiedRoles = new Roles(true, true, true, true, true, true, false);
		securityManager.updateRoles(invitee, modifiedRoles);
		dbInstance.commitAndCloseSession();

		//check roles
		Roles reloadRoles = securityManager.getRoles(invitee);
		Assert.assertNotNull(reloadRoles);
		Assert.assertFalse(reloadRoles.isAuthor());
		Assert.assertFalse(reloadRoles.isGroupManager());
		Assert.assertTrue(reloadRoles.isGuestOnly());
		Assert.assertFalse(reloadRoles.isInstitutionalResourceManager());
		Assert.assertFalse(reloadRoles.isInvitee());
		Assert.assertFalse(reloadRoles.isOLATAdmin());
		Assert.assertFalse(reloadRoles.isUserManager());
	}
	
	/**
	 * Test method @see org.olat.basesecurity.BaseSecurityManager.getIdentitiesByPowerSearch()
	 * with a list of identity keys as parameters.<br/>
	 * getIdentitiesByPowerSearch is a dynamic generated query and we need
	 * to test some aspects of it.
	 */
	@Test
	public void testGetIdentityByPowerSearch_IdentityKeys() {
		String login = "pow-1-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(login);
		dbInstance.commitAndCloseSession();
		
		SearchIdentityParams params = new SearchIdentityParams();
		params.setIdentityKeys(Collections.singletonList(id.getKey()));
		
		List<Identity> ids = securityManager.getIdentitiesByPowerSearch(params, 0, -1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(id, ids.get(0));
	}
	
	/**
	 * Check the method @see getIdentitiesByPowerSearch
	 * with a login as parameter.<br/>
	 * getIdentitiesByPowerSearch is a dynamic generated query and we need
	 * to test some aspects of it.
	 */
	@Test
	public void testGetIdentityByPowerSearch_Login() {
		String login = "pow-2-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(login);
		dbInstance.commitAndCloseSession();
		
		SearchIdentityParams params = new SearchIdentityParams();
		params.setLogin(login);
		
		List<Identity> ids = securityManager.getIdentitiesByPowerSearch(params, 0, -1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(id, ids.get(0));
	}
	
	/**
	 * Check the method @see getIdentitiesByPowerSearch
	 * with a user property as parameter.<br/>
	 * getIdentitiesByPowerSearch is a dynamic generated query and we need
	 * to test some aspects of it.
	 */
	@Test
	public void testGetIdentityByPowerSearch_UserProperty() {
		//create a user with a first name
		String login = "pow-3-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(login);
		String firstName = id.getUser().getProperty(UserConstants.FIRSTNAME, null);
		dbInstance.commitAndCloseSession();
		
		SearchIdentityParams params = new SearchIdentityParams();
		Map<String,String> props = new HashMap<String,String>();
		props.put(UserConstants.FIRSTNAME, firstName);
		params.setUserProperties(props);
		
		List<Identity> ids = securityManager.getIdentitiesByPowerSearch(params, 0, -1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(id, ids.get(0));
	}
	
	/**
	 * Check the method @see getIdentitiesByPowerSearch
	 * with a login and a list of identity keys as parameters.<br/>
	 * getIdentitiesByPowerSearch is a dynamic generated query and we need
	 * to test some aspects of it.
	 */
	@Test
	public void testGetIdentityByPowerSearch_LoginIdentityKeys() {
		String login = "pow-4-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(login);
		dbInstance.commitAndCloseSession();
		
		SearchIdentityParams params = new SearchIdentityParams();
		params.setLogin(login);
		params.setIdentityKeys(Collections.singletonList(id.getKey()));
		
		List<Identity> ids = securityManager.getIdentitiesByPowerSearch(params, 0, -1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(id, ids.get(0));
	}
	
	/**
	 * Test the method @see getIdentitiesByPowerSearch
	 * with 2 user properties and a list of identity keys as parameters.<br/>
	 * getIdentitiesByPowerSearch is a dynamic generated query and we need
	 * to test some aspects of it.
	 */
	@Test
	public void testGetIdentityByPowerSearch_LoginIdentityKeysProperty() {
		String login = "pow-5-" + UUID.randomUUID().toString();
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser(login);
		dbInstance.commitAndCloseSession();
		
		SearchIdentityParams params = new SearchIdentityParams();
		params.setLogin(login);
		Map<String,String> props = new HashMap<String,String>();
		props.put(UserConstants.FIRSTNAME, id.getUser().getProperty(UserConstants.FIRSTNAME, null));
		props.put(UserConstants.LASTNAME, id.getUser().getProperty(UserConstants.LASTNAME, null));
		params.setUserProperties(props);
		params.setIdentityKeys(Collections.singletonList(id.getKey()));
		
		List<Identity> ids = securityManager.getIdentitiesByPowerSearch(params, 0, -1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(id, ids.get(0));
	}
	
	@Test
	public void testGetIdentitiesByPowerSearchWithGroups() {
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser("user-1-" + UUID.randomUUID().toString());
		SecurityGroup usersGroup = securityManager.findSecurityGroupByName(Constants.GROUP_OLATUSERS);
		dbInstance.commitAndCloseSession();
		
		//test positive result
	  SecurityGroup[] groups = { usersGroup };
	  List<Identity> userList = securityManager.getVisibleIdentitiesByPowerSearch(id.getName(), null, true, groups, null, null, null, null,null);
		Assert.assertNotNull(userList);
	  Assert.assertEquals(1, userList.size());
		Assert.assertEquals(id, userList.get(0));
	  
		//test negatif -> with author security group
		SecurityGroup[] authors = { securityManager.findSecurityGroupByName(Constants.GROUP_AUTHORS) };
		List<Identity> authorList = securityManager.getVisibleIdentitiesByPowerSearch(id.getName(), null, true, authors, null, null, null, null,null);
		Assert.assertNotNull(authorList);
		Assert.assertTrue(authorList.isEmpty());
	}
	
	@Test
	public void testGetIdentitiesOfSecurityGroup() {
		//create 3 identities and 2 security groups
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-1-" + UUID.randomUUID().toString());
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-2-" + UUID.randomUUID().toString());
		Identity id3 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-3-" + UUID.randomUUID().toString());
		SecurityGroup secGroup = securityManager.createAndPersistSecurityGroup();
		securityManager.addIdentityToSecurityGroup(id1, secGroup);
		securityManager.addIdentityToSecurityGroup(id2, secGroup);
		securityManager.addIdentityToSecurityGroup(id3, secGroup);
		dbInstance.commitAndCloseSession();
		
		//retrieve them
		List<Identity> identities = securityManager.getIdentitiesOfSecurityGroup(secGroup, 0, -1);
		Assert.assertNotNull(identities);
		Assert.assertEquals(3, identities.size());
		Assert.assertTrue(identities.contains(id1));
		Assert.assertTrue(identities.contains(id2));
		Assert.assertTrue(identities.contains(id3));
	}
	
	@Test
	public void testGetIdentitiesOfSecurityGroups() {
		//create 3 identities and 2 security groups
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-1-" + UUID.randomUUID().toString());
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-2-" + UUID.randomUUID().toString());
		Identity id3 = JunitTestHelper.createAndPersistIdentityAsUser("user-sec-3-" + UUID.randomUUID().toString());
		SecurityGroup secGroup1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup2 = securityManager.createAndPersistSecurityGroup();
		securityManager.addIdentityToSecurityGroup(id1, secGroup1);
		securityManager.addIdentityToSecurityGroup(id2, secGroup1);
		securityManager.addIdentityToSecurityGroup(id2, secGroup2);
		securityManager.addIdentityToSecurityGroup(id3, secGroup2);
		dbInstance.commitAndCloseSession();
		
		//retrieve them
		List<SecurityGroup> secGroups = new ArrayList<SecurityGroup>();
		secGroups.add(secGroup1);
		secGroups.add(secGroup2);
		List<Identity> identities = securityManager.getIdentitiesOfSecurityGroups(secGroups);
		Assert.assertNotNull(identities);
		Assert.assertEquals(3, identities.size());
		Assert.assertTrue(identities.contains(id1));
		Assert.assertTrue(identities.contains(id2));
		Assert.assertTrue(identities.contains(id3));
	}
	

	@Test
	public void testGetPoliciesOfSecurityGroup() {
		SecurityGroup secGroup = securityManager.createAndPersistSecurityGroup();
		OLATResource resource = JunitTestHelper.createRandomResource();
		Policy policy_1 = securityManager.createAndPersistPolicy(secGroup, "test.right1", resource);
		Policy policy_2 = securityManager.createAndPersistPolicy(secGroup, "test.right2", resource);
		dbInstance.commitAndCloseSession();
		
		List<Policy> policies = securityManager.getPoliciesOfSecurityGroup(secGroup);
		Assert.assertNotNull(policies);
		Assert.assertEquals(2, policies.size());
		Assert.assertTrue(policies.contains(policy_1));
		Assert.assertTrue(policies.contains(policy_2));		
	}
	
	/**
	 * Test this method
	 * @see getPoliciesOfSecurityGroup(List<SecurityGroup> secGroups, resource1)
	 */
	@Test
	public void testGetPoliciesOfSecurityGroups() {
		//create 2 security groups and 2 resources
		SecurityGroup secGroup_1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup_2 = securityManager.createAndPersistSecurityGroup();
		OLATResource resource_1 = JunitTestHelper.createRandomResource();
		OLATResource resource_2 = JunitTestHelper.createRandomResource();
		Policy policy_1 = securityManager.createAndPersistPolicy(secGroup_1, "test.right1_1", resource_1);
		Policy policy_2 = securityManager.createAndPersistPolicy(secGroup_1, "test.right1_2", resource_2);
		Policy policy_3 = securityManager.createAndPersistPolicy(secGroup_2, "test.right2_1", resource_1);
		Policy policy_4 = securityManager.createAndPersistPolicy(secGroup_2, "test.right2_2", resource_2);
		dbInstance.commitAndCloseSession();
	
		//test group_1 and resource_1
		List<Policy> policies1_1 =  securityManager.getPoliciesOfSecurityGroup(Collections.singletonList(secGroup_1), resource_1);
		Assert.assertNotNull(policies1_1);
		Assert.assertEquals(1, policies1_1.size());
		Assert.assertEquals(policy_1, policies1_1.get(0));

		//test group_1 + resource_1 and 2
		List<Policy> policies1 =  securityManager.getPoliciesOfSecurityGroup(Collections.singletonList(secGroup_1), resource_1, resource_2);
		Assert.assertNotNull(policies1);
		Assert.assertEquals(2, policies1.size());
		Assert.assertTrue(policies1.contains(policy_1));
		Assert.assertTrue(policies1.contains(policy_2));
		
		//test group 2
		List<Policy> policies2 =  securityManager.getPoliciesOfSecurityGroup(Collections.singletonList(secGroup_2));
		Assert.assertNotNull(policies2);
		Assert.assertEquals(2, policies2.size());
		Assert.assertTrue(policies2.contains(policy_3));
		Assert.assertTrue(policies2.contains(policy_4));
		
		//test dummy
		List<Policy> policiesEmpty =  securityManager.getPoliciesOfSecurityGroup(Collections.<SecurityGroup>emptyList());
		Assert.assertNotNull(policiesEmpty);
		Assert.assertTrue(policiesEmpty.isEmpty());
	}
		
	/**
	 * Test the method
	 * @see public List<Policy> getPoliciesOfResource(OLATResource resource, SecurityGroup secGroup)
	 */
	@Test
	public void testGetPoliciesOfResource() {
		//create 3 rights
		SecurityGroup secGroup = securityManager.createAndPersistSecurityGroup();
		OLATResource resource = JunitTestHelper.createRandomResource();
		Policy policy_1 = securityManager.createAndPersistPolicy(secGroup, "test.right1", resource);
		Policy policy_2 = securityManager.createAndPersistPolicy(secGroup, "test.right2", resource);
		Policy policy_3 = securityManager.createAndPersistPolicy(secGroup, "test.right3", resource);
		dbInstance.commitAndCloseSession();
		
		//test the method
		List<Policy> policies = securityManager.getPoliciesOfResource(resource, secGroup);
		Assert.assertNotNull(policies);
		Assert.assertEquals(3, policies.size());
		Assert.assertTrue(policies.contains(policy_1));
		Assert.assertTrue(policies.contains(policy_2));
		Assert.assertTrue(policies.contains(policy_3));
	}
	
	@Test
	public void testGetPoliciesOfIdentity() {
		//create 3 security groups and 2 resources and an identity
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsUser("test-right-1-" + UUID.randomUUID().toString());
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsUser("test-right-2-" + UUID.randomUUID().toString());
		SecurityGroup secGroup_1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup_2 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup_3 = securityManager.createAndPersistSecurityGroup();
		securityManager.addIdentityToSecurityGroup(id1, secGroup_1);
		securityManager.addIdentityToSecurityGroup(id1, secGroup_2);
		securityManager.addIdentityToSecurityGroup(id2, secGroup_2);
		OLATResource resource_1 = JunitTestHelper.createRandomResource();
		OLATResource resource_2 = JunitTestHelper.createRandomResource();
		Policy policy_1_1 = securityManager.createAndPersistPolicy(secGroup_1, "test.right11", resource_1);
		Policy policy_1_2 = securityManager.createAndPersistPolicy(secGroup_1, "test.right12", resource_2);
		Policy policy_2_1 = securityManager.createAndPersistPolicy(secGroup_2, "test.right21", resource_1);
		Policy policy_3_1 = securityManager.createAndPersistPolicy(secGroup_3, "test.right31", resource_1);
		Policy policy_3_2 = securityManager.createAndPersistPolicy(secGroup_3, "test.right32", resource_2);
		dbInstance.commitAndCloseSession();
		
		//test the method for id1
		List<Policy> policiesId1 = securityManager.getPoliciesOfIdentity(id1);
		Assert.assertNotNull(policiesId1);
		Assert.assertTrue(3 <= policiesId1.size());// OpenOLAT add automatically some standard policy to every user
		Assert.assertTrue(policiesId1.contains(policy_1_1));
		Assert.assertTrue(policiesId1.contains(policy_1_2));
		Assert.assertTrue(policiesId1.contains(policy_2_1));
		Assert.assertFalse(policiesId1.contains(policy_3_1));
		Assert.assertFalse(policiesId1.contains(policy_3_2));
		
		//test the method for id2
		List<Policy> policiesId2 = securityManager.getPoliciesOfIdentity(id2);
		Assert.assertNotNull(policiesId2);
		Assert.assertTrue(1 <= policiesId2.size());// OpenOLAT add automatically some standard policy to every user
		Assert.assertFalse(policiesId2.contains(policy_1_1));
		Assert.assertFalse(policiesId2.contains(policy_1_2));
		Assert.assertTrue(policiesId2.contains(policy_2_1));
		Assert.assertFalse(policiesId2.contains(policy_3_1));
		Assert.assertFalse(policiesId2.contains(policy_3_2));
	}
	
	@Test
	public void testGetPoliciesOfIdentity_2() {
		Identity s1 = JunitTestHelper.createAndPersistIdentityAsUser("s1-" + UUID.randomUUID().toString());
		OLATResource olatres = JunitTestHelper.createRandomResource();
		SecurityGroup olatUsersGroup = securityManager.findSecurityGroupByName(Constants.GROUP_OLATUSERS);
		securityManager.createAndPersistPolicy(olatUsersGroup, Constants.PERMISSION_ACCESS, olatres);
		List<Policy> policies = securityManager.getPoliciesOfIdentity(s1);
		
		boolean foundPolicy = false;
		for (Iterator<Policy> iterator = policies.iterator(); iterator.hasNext();) {
			Policy policy = iterator.next();
			Long resourcableId = policy.getOlatResource().getResourceableId();
			if ((resourcableId != null) && (resourcableId.equals(olatres.getResourceableId()))) {
				assertEquals(olatUsersGroup.getKey(), policy.getSecurityGroup().getKey());
				assertEquals(Constants.PERMISSION_ACCESS, policy.getPermission());
				assertEquals(olatres.getResourceableId(), policy.getOlatResource().getResourceableId());
				foundPolicy = true;
			}
		}
		assertTrue("Does not found policy", foundPolicy);
	}
	
	/**
	 * @see public void deletePolicy(SecurityGroup secGroup, String permission, OLATResource resource);
	 */
	@Test
	public void testDeletePolicy() {
		//create 3 rights
		SecurityGroup secGroup1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup2 = securityManager.createAndPersistSecurityGroup();
		OLATResource resource1 = JunitTestHelper.createRandomResource();
		OLATResource resource2 = JunitTestHelper.createRandomResource();
		Policy policy_1_1 = securityManager.createAndPersistPolicy(secGroup1, "test.r1-1_1", resource1);
		Policy policy_1_1b = securityManager.createAndPersistPolicy(secGroup1, "test.r2-1_1", resource1);
		Policy policy_1_2 = securityManager.createAndPersistPolicy(secGroup1, "test.r3_1-2", resource2);
		Policy policy_2_2 = securityManager.createAndPersistPolicy(secGroup2, "test.r3_2-2", resource2);
		dbInstance.commitAndCloseSession();
		
		//delete policy 1_1b
		securityManager.deletePolicy(secGroup1, "test.r2-1_1", resource1);
		
		//test the method
		List<Policy> policies_1_1 = securityManager.getPoliciesOfResource(resource1, secGroup1);
		Assert.assertNotNull(policies_1_1);
		Assert.assertEquals(1, policies_1_1.size());
		Assert.assertTrue(policies_1_1.contains(policy_1_1));
		Assert.assertFalse(policies_1_1.contains(policy_1_1b));
		
		//too much deleted in resource 1?
		List<Policy> policies_x_1 = securityManager.getPoliciesOfResource(resource1, null);
		Assert.assertNotNull(policies_x_1);
		Assert.assertEquals(1, policies_x_1.size());
		Assert.assertTrue(policies_x_1.contains(policy_1_1));
		Assert.assertFalse(policies_x_1.contains(policy_1_1b));
		
		//too much deleted in resource 2?
		List<Policy> policies_x_2 = securityManager.getPoliciesOfResource(resource2, null);
		Assert.assertNotNull(policies_x_2);
		Assert.assertEquals(2, policies_x_2.size());
		Assert.assertTrue(policies_x_2.contains(policy_1_2));
		Assert.assertTrue(policies_x_2.contains(policy_2_2));
	}
	
	/**
	 * @see public boolean deletePolicies(Collection<SecurityGroup> secGroups, Collection<OLATResource> resources)
	 */
	@Test
	public void testDeletePolicies() {
		//prepare enough rights
		SecurityGroup secGroup1 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup2 = securityManager.createAndPersistSecurityGroup();
		SecurityGroup secGroup3 = securityManager.createAndPersistSecurityGroup();
		OLATResource resource1 = JunitTestHelper.createRandomResource();
		OLATResource resource2 = JunitTestHelper.createRandomResource();
		OLATResource resource3 = JunitTestHelper.createRandomResource();
		Policy policy_1_1 = securityManager.createAndPersistPolicy(secGroup1, "test.r1-1_1", resource1);
		Policy policy_1_b = securityManager.createAndPersistPolicy(secGroup1, "test.r2-1_1", resource1);
		Policy policy_1_2 = securityManager.createAndPersistPolicy(secGroup1, "test.r3_1-2", resource2);
		Policy policy_1_3 = securityManager.createAndPersistPolicy(secGroup1, "test.r4_1-3", resource3);
		Policy policy_2_2 = securityManager.createAndPersistPolicy(secGroup2, "test.r5_2-2", resource2);
		Policy policy_3_1 = securityManager.createAndPersistPolicy(secGroup3, "test.r6_3-1", resource1);
		Policy policy_3_2 = securityManager.createAndPersistPolicy(secGroup3, "test.r7_3-2", resource2);
		Policy policy_3_3 = securityManager.createAndPersistPolicy(secGroup3, "test.r8_3-3", resource3);
		dbInstance.commitAndCloseSession();
		
		//delete group 1 and 2, delete resource 1 and 3
		List<SecurityGroup> secGroups = new ArrayList<SecurityGroup>(2);
		secGroups.add(secGroup1);
		secGroups.add(secGroup2);
		List<OLATResource> resources = new ArrayList<OLATResource>(2);
		resources.add(resource1);
		resources.add(resource3);
		boolean deleted = securityManager.deletePolicies(secGroups, resources);
		Assert.assertTrue(deleted);
		
		//check resource 1 -> only policy_3_1 survives
		List<Policy> policies_1 = securityManager.getPoliciesOfResource(resource1, null);
		Assert.assertNotNull(policies_1);
		Assert.assertEquals(1, policies_1.size());
		Assert.assertTrue(policies_1.contains(policy_3_1));
		Assert.assertFalse(policies_1.contains(policy_1_1));
		Assert.assertFalse(policies_1.contains(policy_1_b));
		//check resource 2 -> all its policies survive
		List<Policy> policies_2 = securityManager.getPoliciesOfResource(resource2, null);
		Assert.assertNotNull(policies_2);
		Assert.assertEquals(3, policies_2.size());
		Assert.assertTrue(policies_2.contains(policy_1_2));
		Assert.assertTrue(policies_2.contains(policy_2_2));
		Assert.assertTrue(policies_2.contains(policy_3_2));
		//check resource 3
		List<Policy> policies_3 = securityManager.getPoliciesOfResource(resource3, null);
		Assert.assertNotNull(policies_3);
		Assert.assertEquals(1, policies_3.size());
		Assert.assertTrue(policies_3.contains(policy_3_3));
		Assert.assertFalse(policies_3.contains(policy_1_3));
	}
}
