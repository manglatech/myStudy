package org.olat.institute.feature.util;

public enum Features {
	
	NOT_DEFINED(0,"Not Defined"),
	CP_LEARNING_CONTENT(1,"CP Learning Content"),
	SCORM_LEARNING_CONTENT(2,"SCORM Learning Content"),
	COURSE_LEARNING_CONTENT(3,"Course Learning Content"),
	WIKI_LEARNING_CONTENT(4,"WIKI Learning Content"),
	PODCAST_LEARNING_CONTENT(5,"Podcast Learning Content"),
	BLOGS_LEARNING_CONTENT(6,"Blogs Learning Content"),
	PORTFOLIO_TEMPLATE_LEARNING_CONTENT(7,"Portfolio Template Learning Content"),
	TEST_LEARNING_CONTENT(8,"Test Learning Content"),
	QUASITIONARIES_LEARNING_CONTENT(9,"Quasitionaries Learning Content"),
	RESOURCE_FOLDER_LEARNING_CONTENT(10,"Resource Folder Learning Content"),
	GLOSSARY_LEARNING_CONTENT(11,"Glossary Learning Content"),
	SP_LEARNING_CONTENT(12,"Single Page Couse Content"),
	FORUM_COURSE_CONTENT(13,"Forum Course Content"),
	FILE_DIALOUGH_COURSE_CONTENT(14,"File Dialough Course Content"),
	SELFTEST_LEARNING_CONTENT(15,"Self Test Course Content"),
	ENROLLMENT_LEARNING_CONTENT(16,"Enrollment Course Content"),
	CALENDER_LEARNING_CONTENT(17,"Calender Module"),
	COURSE_FOLDER_LEARNING_CONTENT(18,"Folder In Course Content"),
	TASK_LEARNING_CONTENT(19,"Task Course Content"),
	EXTERNAL_PAGE_LEARNING_CONTENT(20,"External Page Course Content"),
	STRUCTURE_LEARNING_CONTENT(21,"Structure Course Content"),
	ASSESTMENT_CONTENT(22,"Assesment Module"),
	TOPIC_ASSIGNMENT_COURSE_CONTENT(23,"Topic Assignment Course Content"),
	EMAIL_COURSE_CONTENT(24,"Email In Course"),
	NOTIFICATIONS_EMAIL_COURSE_CONTENT(25,"Notifications Module"),
	LTI_COURSE_CONTENT(26,"LTI In Course"),
	PARTICIPANT_LIST_COURSE_CONTENT(27,"Participant List In Course"),
	LINK_LIST_COURSE_CONTENT(28,"Link List In Course"),
	BOOKMARK_MODULE(29,"Bookmark Module"),
	USER_PERSONAL_FOLDER(30,"User Personal Folder"),
	NOTES_MODULE(31,"Notes Module");
	
	
	private Features(Integer Id,String name){
		this.name = name;
		this.Id = Id;
	}
	
	private String name;
	private Integer Id;
	
	public String getName() {
		return name;
	}
	public Integer getId() {
		return Id;
	}
	
}
