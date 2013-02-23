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

package org.olat.institute.feature;

import org.olat.core.configuration.AbstractOLATModule;
import org.olat.core.configuration.PersistedProperties;

/**
 * Initial Date: May 4, 2004
 * @author Mike Stock 
 * @author guido
 * Comment:
 */
public class FeatureModule extends AbstractOLATModule {
	
	
	public static Integer FEATURE_NOT_DEFINE = 0;
	public static Integer FEATURE_CP_CONTENT_ID=1;
	public static Integer FEATURE_SCORM_CONTENT_ID=2;
	public static Integer FEATURE_COURSE_ID=3;
	public static Integer FEATURE_WIKI_ID=4;
	public static Integer FEATURE_PODCAST_ID=5;
	public static Integer FEATURE_BLOG_ID=6;
	public static Integer FEATURE_PORTFOLIO_CONTENT_ID=7;
	public static Integer FEATURE_TEST_CONTENT=8;
	public static Integer FEATURE_QUASITIONARIES_ID=9;
	public static Integer FEATURE_RESOURCE_FOLDER_ID=10;
	public static Integer FEATURE_GLOSSARY_ID=11;
	public static Integer FEATURE_SINGLE_PAGE_ID=12;
	public static Integer FEATURE_FEATURE_ID=13;
	public static Integer FEATURE_FILE_DIALOUGH_ID=14;
	public static Integer FEATURE_SELFTEST_ID=15;
	public static Integer FEATURE_ENROLLMENT_ID=16;
	public static Integer FEATURE_CALENDAR_ID=17;
	public static Integer FEATURE_COURSE_FOLDER_ID=18;
	public static Integer FEATURE_TASK_ID=19;
	public static Integer FEATURE_EXTERNAL_PAGE_ID=20;
	public static Integer FEATURE_STRUCTURE_ID=21;
	public static Integer FEATURE_ASSESTMENT_ID=22;
	public static Integer FEATURE_TOPIC_ASSIGNMENT_ID=23;
	public static Integer FEATURE_EMAIL_COURSE_ID=24;
	public static Integer FEATURE_NOTIFICATION_ID=25;
	public static Integer FEATURE_LTI_COURSE_ID=26;
	public static Integer FEATURE_PARTICIPANT_LIST_ID=27;
	public static Integer FEATURE_LINK_LIST_ID=28;
	public static Integer FEATURE_BOOKMARK_ID=29;
	public static Integer FEATURE_USER_HOME_FOLDER_ID=30;
	public static Integer FEATURE_NOTES_ID=31;
	public static Integer FEATURE_OTHERUSER_SEARCH_ID=32;
	public static Integer FEATURE_HOMEPAGE_ID=33;
	public static Integer FEATURE_WEBDAV_ID=34;
	public static Integer FEATURE_CATALOG_ID=35;
	
	//NEW
	public static Integer FEATURE_ARCHIVE_TOOL_ID=36;
	public String CONFIG_FEATURE_ARCHIVE_TOOL_ID = "feature.archive.tool.id";
	
	public static Integer FEATURE_STATISTIC_ID=37;
	public String CONFIG_FEATURE_STATISTIC_ID = "feature.statistic.id";
	
	public static Integer FEATURE_LEARNING_RESOURCE_ID=38;
	public String CONFIG_FEATURE_LEARNING_RESOURCE_ID = "feature.learning.resource.id";

	public static Integer FEATURE_COURSE_PREVIEW_ID=39;
	public String CONFIG_FEATURE_COURSE_PREVIEW_ID = "feature.course.preview.id";

	public static Integer FEATURE_COURSE_LAYOUT_ID=40;
	public String CONFIG_FEATURE_COURSE_LAYOUT_ID = "feature.course.layout.id";

	public static Integer FEATURE_COURSE_EVIDENCE_ACH_ID=41;
	public String CONFIG_FEATURE_COURSE_EVIDENCE_ACH_ID = "feature.course.evidence.ach.id";

	public String CONFIG_FEATURE_NOT_DEFINE = "feature.notdefine.id";
	public String CONFIG_FEATURE_CP_CONTENT_ID="feature.cp.content.id";
	private String CONFIG_FEATURE_SCORM_CONTENT_ID="feature.scorm.content.id";
	private String CONFIG_FEATURE_COURSE_ID="feature.course.id";
	private String CONFIG_FEATURE_WIKI_ID="feature.wiki.id";
	private String CONFIG_FEATURE_PODCAST_ID="feature.podcast.id";
	private String CONFIG_FEATURE_BLOG_ID="feature.blog.id";
	private String CONFIG_FEATURE_PORTFOLIO_CONTENT_ID="feature.portfolio.content.id";
	private String CONFIG_FEATURE_TEST_CONTENT="feature.test.content.id";
	private String CONFIG_FEATURE_QUASITIONARIES_ID="feature.quasitionaries.id";
	private String CONFIG_FEATURE_RESOURCE_FOLDER_ID="feature.resource.folder.id";
	private String CONFIG_FEATURE_GLOSSARY_ID="feature.glossary.id";
	private String CONFIG_FEATURE_SINGLE_PAGE_ID="feature.single.page.id";
	private String CONFIG_FEATURE_FEATURE_ID="feature.forum.id";
	private String CONFIG_FEATURE_FILE_DIALOUGH_ID="feature.file.dialough.id";
	private String CONFIG_FEATURE_SELFTEST_ID="feautre.selftest.id";
	private String CONFIG_FEATURE_ENROLLMENT_ID="feature.enrollment.id";
	private String CONFIG_FEATURE_CALENDAR_ID="features.calender.id";
	private String CONFIG_FEATURE_COURSE_FOLDER_ID="feature.course.folder.id";
	private String CONFIG_FEATURE_TASK_ID="feature.task.id";
	private String CONFIG_FEATURE_EXTERNAL_PAGE_ID="feature.external.page.id";
	private String CONFIG_FEATURE_STRUCTURE_ID="feature.structure.id";
	private String CONFIG_FEATURE_ASSESTMENT_ID="feature.assestment.id";
	private String CONFIG_FEATURE_TOPIC_ASSIGNMENT_ID="feature.topic.assignment.id";
	private String CONFIG_FEATURE_EMAIL_COURSE_ID="feature.email.course.id";
	private String CONFIG_FEATURE_NOTIFICATION_ID="features.notification.id";
	private String CONFIG_FEATURE_LTI_COURSE_ID="feature.lti.course.id";
	private String CONFIG_FEATURE_PARTICIPANT_LIST_ID="feature.participant.list.id";
	private String CONFIG_FEATURE_LINK_LIST_ID="feature.link.list.id";
	private String CONFIG_FEATURE_BOOKMARK_ID="features.bookmark.id";
	private String CONFIG_FEATURE_USER_HOME_FOLDER_ID="features.user.home.folder.id";
	private String CONFIG_FEATURE_NOTES_ID="features.notes.id";
	private String CONFIG_FEATURE_OTHERUSER_SEARCH_ID="features.otheruser.search.id";
	private String CONFIG_FEATURE_HOMEPAGE_ID="features.homepage.id";
	private String CONFIG_FEATURE_WEBDAV_ID="feature.webdav.id";
	private String CONFIG_FEATURE_CATALOG_ID="feature.catalog.id";
	
	
	private String CONFIG_ALL_REGISTER_USER_ACCESS="restricated.all.register.user.access.enabled";
	private String CONFIG_ONLY_OWNER_ACCESS="restricated.only.owner.access.enabled";
	private String CONFIG_OWNER_AND_OTHER_AUTHOR_ACCESS="restricated.owner.otherauthor.access.enabled";
	private String CONFIG_ALL_REGISTER_AND_GUEST_USER_ACCESS="restricated.all.register.guest.access.enabled";
	private String CONFIG_MEMBER_ONLY_ACCESS="restricated.member.only.access.enabled";
	
	public static boolean ALL_REGISTER_USER_ACCESS = true;;
	public static boolean ONLY_OWNER_ACCESS= true;
	public static boolean OWNER_AND_OTHER_AUTHOR_ACCESS= true;
	public static boolean ALL_REGISTER_AND_GUEST_USER_ACCESS= true;
	public static boolean MEMBER_ONLY_ACCESS= true;
	
	private String CONFIG_IMPORT_RESOURCES_ENABLED ="import.resources.enabled";
	public static boolean IMPORT_RESOURCES_ENABLED = true;
	private String CONFIG_EXPORT_RESOURCES_ENABLED ="export.resources.enabled";
	public static boolean EXPORT_RESOURCES_ENABLED = true;

	private String CONFIG_LIMIT_SEARCH_ON_RESOURCE_SEARCH_FORM_ENABLED ="limit.search.on.resource.search.form.enabled";
	public static boolean LIMIT_SEARCH_ON_RESOURCE_SEARCH_FORM_ENABLED = true;
	
	private String CONFIG_ALL_COURSE_LINK_ENABLED ="all.course.link.enabled";
	public static boolean ALL_COURSE_LINK_ENABLED = true;
	
	private String CONFIG_BOOKING_IN_COURSE_DETAILS_ENABLED ="booking.in.course.details.page.enabled";
	public static boolean BOOKING_IN_COURSE_DETAILS_ENABLED = true;
	
	
	//TODO: Make it Property Driven : It was quick fix. Disabled controller in BusinessGroupContext.xml file. So Not making Prop drivern right now.
	public static boolean OPEN_BUSINESS_GROUP_ENABLED = false;
	
	private FeatureModule() {
	}

	@Override
	public void init() {
	}

	@Override
	protected void initDefaultProperties() {
		
		FEATURE_NOT_DEFINE = getIntConfigParameter(CONFIG_FEATURE_NOT_DEFINE,FEATURE_NOT_DEFINE);
		FEATURE_CP_CONTENT_ID = getIntConfigParameter(CONFIG_FEATURE_CP_CONTENT_ID,FEATURE_CP_CONTENT_ID);
		FEATURE_SCORM_CONTENT_ID=getIntConfigParameter(CONFIG_FEATURE_SCORM_CONTENT_ID,FEATURE_SCORM_CONTENT_ID) ;
		FEATURE_COURSE_ID=getIntConfigParameter(CONFIG_FEATURE_COURSE_ID,FEATURE_COURSE_ID);
		FEATURE_WIKI_ID=getIntConfigParameter(CONFIG_FEATURE_WIKI_ID,FEATURE_WIKI_ID);
		FEATURE_PODCAST_ID=getIntConfigParameter(CONFIG_FEATURE_PODCAST_ID,FEATURE_PODCAST_ID);
		FEATURE_BLOG_ID=getIntConfigParameter(CONFIG_FEATURE_BLOG_ID,FEATURE_BLOG_ID);
		FEATURE_PORTFOLIO_CONTENT_ID=getIntConfigParameter(CONFIG_FEATURE_PORTFOLIO_CONTENT_ID,FEATURE_PORTFOLIO_CONTENT_ID);
		FEATURE_TEST_CONTENT=getIntConfigParameter(CONFIG_FEATURE_TEST_CONTENT,FEATURE_TEST_CONTENT);
		FEATURE_QUASITIONARIES_ID=getIntConfigParameter(CONFIG_FEATURE_QUASITIONARIES_ID,FEATURE_QUASITIONARIES_ID);
		FEATURE_RESOURCE_FOLDER_ID=getIntConfigParameter(CONFIG_FEATURE_RESOURCE_FOLDER_ID,FEATURE_RESOURCE_FOLDER_ID);
		FEATURE_GLOSSARY_ID=getIntConfigParameter(CONFIG_FEATURE_GLOSSARY_ID,FEATURE_GLOSSARY_ID);
		FEATURE_SINGLE_PAGE_ID=getIntConfigParameter(CONFIG_FEATURE_SINGLE_PAGE_ID,FEATURE_SINGLE_PAGE_ID);
		FEATURE_FEATURE_ID=getIntConfigParameter(CONFIG_FEATURE_FEATURE_ID,FEATURE_FEATURE_ID);
		FEATURE_FILE_DIALOUGH_ID=getIntConfigParameter(CONFIG_FEATURE_FILE_DIALOUGH_ID,FEATURE_FILE_DIALOUGH_ID);
		FEATURE_SELFTEST_ID=getIntConfigParameter(CONFIG_FEATURE_SELFTEST_ID,FEATURE_SELFTEST_ID);
		FEATURE_ENROLLMENT_ID=getIntConfigParameter(CONFIG_FEATURE_ENROLLMENT_ID,FEATURE_ENROLLMENT_ID);
		FEATURE_CALENDAR_ID=getIntConfigParameter(CONFIG_FEATURE_CALENDAR_ID,FEATURE_CALENDAR_ID);
		FEATURE_COURSE_FOLDER_ID=getIntConfigParameter(CONFIG_FEATURE_COURSE_FOLDER_ID,FEATURE_COURSE_FOLDER_ID);
		FEATURE_TASK_ID=getIntConfigParameter(CONFIG_FEATURE_TASK_ID,FEATURE_TASK_ID);
		FEATURE_EXTERNAL_PAGE_ID=getIntConfigParameter(CONFIG_FEATURE_EXTERNAL_PAGE_ID,FEATURE_EXTERNAL_PAGE_ID);
		FEATURE_STRUCTURE_ID=getIntConfigParameter(CONFIG_FEATURE_STRUCTURE_ID,FEATURE_STRUCTURE_ID);
		FEATURE_ASSESTMENT_ID=getIntConfigParameter(CONFIG_FEATURE_ASSESTMENT_ID,FEATURE_ASSESTMENT_ID);
		FEATURE_TOPIC_ASSIGNMENT_ID=getIntConfigParameter(CONFIG_FEATURE_TOPIC_ASSIGNMENT_ID,FEATURE_TOPIC_ASSIGNMENT_ID);
		FEATURE_EMAIL_COURSE_ID=getIntConfigParameter(CONFIG_FEATURE_EMAIL_COURSE_ID,FEATURE_EMAIL_COURSE_ID);
		FEATURE_NOTIFICATION_ID=getIntConfigParameter(CONFIG_FEATURE_NOTIFICATION_ID,FEATURE_NOTIFICATION_ID);
		FEATURE_LTI_COURSE_ID=getIntConfigParameter(CONFIG_FEATURE_LTI_COURSE_ID,FEATURE_LTI_COURSE_ID);
		FEATURE_PARTICIPANT_LIST_ID=getIntConfigParameter(CONFIG_FEATURE_PARTICIPANT_LIST_ID,FEATURE_PARTICIPANT_LIST_ID);
		FEATURE_LINK_LIST_ID=getIntConfigParameter(CONFIG_FEATURE_LINK_LIST_ID,FEATURE_LINK_LIST_ID);
		FEATURE_BOOKMARK_ID=getIntConfigParameter(CONFIG_FEATURE_BOOKMARK_ID,FEATURE_BOOKMARK_ID);
		FEATURE_USER_HOME_FOLDER_ID=getIntConfigParameter(CONFIG_FEATURE_USER_HOME_FOLDER_ID,FEATURE_USER_HOME_FOLDER_ID);
		FEATURE_NOTES_ID=getIntConfigParameter(CONFIG_FEATURE_NOTES_ID,FEATURE_NOTES_ID);
		FEATURE_OTHERUSER_SEARCH_ID=getIntConfigParameter(CONFIG_FEATURE_OTHERUSER_SEARCH_ID,FEATURE_OTHERUSER_SEARCH_ID);
		FEATURE_HOMEPAGE_ID=getIntConfigParameter(CONFIG_FEATURE_HOMEPAGE_ID,FEATURE_HOMEPAGE_ID);
		FEATURE_WEBDAV_ID=getIntConfigParameter(CONFIG_FEATURE_WEBDAV_ID,FEATURE_WEBDAV_ID);
		FEATURE_CATALOG_ID=getIntConfigParameter(CONFIG_FEATURE_CATALOG_ID,FEATURE_CATALOG_ID);
		FEATURE_ARCHIVE_TOOL_ID=getIntConfigParameter(CONFIG_FEATURE_ARCHIVE_TOOL_ID,FEATURE_ARCHIVE_TOOL_ID);
		FEATURE_STATISTIC_ID = getIntConfigParameter(CONFIG_FEATURE_STATISTIC_ID,FEATURE_STATISTIC_ID);
		FEATURE_LEARNING_RESOURCE_ID = getIntConfigParameter(CONFIG_FEATURE_LEARNING_RESOURCE_ID, FEATURE_LEARNING_RESOURCE_ID);
		FEATURE_COURSE_PREVIEW_ID = getIntConfigParameter(CONFIG_FEATURE_COURSE_PREVIEW_ID, FEATURE_COURSE_PREVIEW_ID);
		FEATURE_COURSE_LAYOUT_ID = getIntConfigParameter(CONFIG_FEATURE_COURSE_LAYOUT_ID, FEATURE_COURSE_LAYOUT_ID);
		FEATURE_COURSE_EVIDENCE_ACH_ID = getIntConfigParameter(CONFIG_FEATURE_COURSE_EVIDENCE_ACH_ID, FEATURE_COURSE_EVIDENCE_ACH_ID);
		
		ALL_REGISTER_USER_ACCESS = getBooleanConfigParameter(CONFIG_ALL_REGISTER_USER_ACCESS,ALL_REGISTER_USER_ACCESS);
		ONLY_OWNER_ACCESS= getBooleanConfigParameter(CONFIG_ONLY_OWNER_ACCESS,ONLY_OWNER_ACCESS);
		OWNER_AND_OTHER_AUTHOR_ACCESS = getBooleanConfigParameter(CONFIG_OWNER_AND_OTHER_AUTHOR_ACCESS,OWNER_AND_OTHER_AUTHOR_ACCESS);
		ALL_REGISTER_AND_GUEST_USER_ACCESS = getBooleanConfigParameter(CONFIG_ALL_REGISTER_AND_GUEST_USER_ACCESS, ALL_REGISTER_AND_GUEST_USER_ACCESS);
		MEMBER_ONLY_ACCESS = getBooleanConfigParameter(CONFIG_MEMBER_ONLY_ACCESS,MEMBER_ONLY_ACCESS);
		
		IMPORT_RESOURCES_ENABLED = getBooleanConfigParameter(CONFIG_IMPORT_RESOURCES_ENABLED,IMPORT_RESOURCES_ENABLED);
		EXPORT_RESOURCES_ENABLED = getBooleanConfigParameter(CONFIG_EXPORT_RESOURCES_ENABLED, EXPORT_RESOURCES_ENABLED);
		
		LIMIT_SEARCH_ON_RESOURCE_SEARCH_FORM_ENABLED = getBooleanConfigParameter(CONFIG_LIMIT_SEARCH_ON_RESOURCE_SEARCH_FORM_ENABLED, LIMIT_SEARCH_ON_RESOURCE_SEARCH_FORM_ENABLED);
		ALL_COURSE_LINK_ENABLED = getBooleanConfigParameter(CONFIG_ALL_COURSE_LINK_ENABLED, ALL_COURSE_LINK_ENABLED);
		
		BOOKING_IN_COURSE_DETAILS_ENABLED = getBooleanConfigParameter(CONFIG_BOOKING_IN_COURSE_DETAILS_ENABLED, BOOKING_IN_COURSE_DETAILS_ENABLED);
		
	}

	@Override
	protected void initFromChangedProperties() {
	}

	@Override
	public void setPersistedProperties(PersistedProperties persistedProperties) {
		this.moduleConfigProperties = persistedProperties;
	}
}