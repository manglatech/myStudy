create table if not exists o_fee (
   fee_id bigint not null,
   name varchar(50) not null,
   description varchar(250) not null,
   fk_institute_id varchar(50) not null,
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (fee_id)
);
create table if not exists o_fee_category (
   fee_category_id bigint not null,
   name varchar(50) not null,
   description varchar(250) not null,
   fk_institute_id varchar(50) not null,
   due_date datetime null,
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (fee_category_id)
);
create table if not exists o_fee_mapping(
   fee_mapping_id bigint not null,
   fk_fee_id bigint not null,
   fk_fee_category_id bigint not null,
   price int null,	
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (fee_mapping_id)
);
alter table o_fee_mapping add constraint idx_o_fee_mapping1 foreign key (fk_fee_id) references o_fee (fee_id);
alter table o_fee_mapping add constraint idx_o_fee_mapping2 foreign key (fk_fee_category_id) references o_fee_category (fee_category_id);

create table if not exists o_fee_identity_mapping(
   fee_identity_mapping_id bigint not null,
   fk_fee_category_id bigint not null,
   fk_identity_id bigint not null,
   paid_status int(1) null,
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (fee_identity_mapping_id)
);
alter table o_fee_identity_mapping add constraint idx_o_fee_identity_mapping1 foreign key (fk_fee_category_id) references o_fee_category (fee_category_id);
alter table o_fee_identity_mapping add constraint idx_o_fee_identity_mapping2 foreign key (fk_identity_id) references o_bs_identity (id);

create table if not exists o_fee_identity_paid(
   fee_identity_paid_id bigint not null,
   fk_fee_identity_mapping_id bigint not null,
   paid_amount int null,
   paid_flag char(1) null,
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (fee_identity_paid_id)
);
alter table o_fee_identity_paid add constraint idx_o_fee_identity_paid1 foreign key (fk_fee_identity_mapping_id) references o_fee_identity_mapping (fee_identity_mapping_id);

alter table o_bs_identity add column fk_institute_id varchar(20) default "0";
create index fk_institute_id_idx on o_bs_identity (fk_institute_id);

alter table o_repositoryentry add column fk_institute_id varchar(20) default "0";
create index re_fk_institute_id_idx on o_repositoryentry (fk_institute_id);

alter table o_gp_business add column fk_institute_id varchar(20) default "0";
create index gp_fk_institute_id_idx on o_gp_business (fk_institute_id);

create table if not exists o_institute (
   institute_internal_id bigint not null,
   institute_id varchar(20) not null,
   name varchar(20) not null,
   number_of_users int not null,
   version mediumint unsigned not null,
   lastmodified datetime,
   creationdate datetime,
   primary key (institute_internal_id)
);      

insert into o_institute values (1,"0","Super Admin",100,1,null,'2013-01-05');
   

create table if not exists o_institute_features_control (
   institute_features_control_id bigint not null,
   institute_id varchar(20) not null,
   feature_id bigint not null,
   feature_name varchar(20) not null,
   status int not null default 0,
   primary key (institute_features_control_id)
);

INSERT INTO `openolat`.`o_institute_features_control` 
VALUES  
 (1,'0',1,'CP Learning content',0),
 (2,'0',2,'SCORM LC',0),
 (3,'0',3,'Course LC',1),
 (4,'0',4,'Wiki LC',0),
 (5,'0',5,'Podcast LC',1),
 (6,'0',6,'Blogs LC',0),
 (7,'0',7,'Portfolio LC',0),
 (8,'0',8,'Test LC',1),
 (9,'0',9,'Quasitionaries LC',0),
 (10,'0',10,'Resource Folder LC',1),
 (11,'0',11,'Glossary LC',0),
 (12,'0',12,'Single Page CC',1),
 (13,'0',13,'Forum CC',1),
 (14,'0',14,'File Dialough CC',0),
 (15,'0',15,'Self Test CC',1),
 (16,'0',16,'Enrollment CC',0),
 (17,'0',17,'Calendar Module',1),
 (18,'0',18,'Folder CC',1),
 (19,'0',19,'Task CC',1),
 (20,'0',20,'External Page CC',1),
 (21,'0',21,'Structure CC',1),
 (22,'0',22,'Assesment Module',0),
 (23,'0',23,'Topic Assignment CC',1),
 (24,'0',24,'Email In CC',1),
 (25,'0',25,'Notifications In CC',0),
 (26,'0',26,'LTI CC',0),
 (27,'0',27,'Participant List CC',0),
 (28,'0',28,'Link List CC',0),
 (29,'0',29,'Bookmark Module',0),
 (30,'0',30,'User Personal Folder',1),
 (31,'0',31,'Notes Module',1),
 (32,'0',32,'User Search Module',1),
 (33,'0',33,'HomePage',1),
 (34,'0',34,'WebDAV Module',0),
 (35,'0',35,'CATALOG Module',0);
 
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (36,'0',36,'Archive Module',0);
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (37,'0',37,'Statistic Module',0);
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (38,'0',38,'Learning Resource',0);
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (39,'0',39,'Course Preview',0);
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (40,'0',40,'Course Layout',0);
 INSERT INTO `openolat`.`o_institute_features_control` VALUES (41,'0',41,'Evidence Ach',0);
 
create or replace view o_user_account_summary_v as
select ofim.fk_identity_id, ofc.fee_category_id as fk_fee_category_id, ofc.fk_institute_id as institute_id,ofim.paid_status,
(select sum(ofip.paid_amount) from o_fee_identity_paid ofip where ofip.fk_fee_identity_mapping_id = ofim.fee_identity_mapping_id group by ofip.fk_fee_identity_mapping_id )as user_paid_amount, 
(select sum(ofm.price) from o_fee_mapping ofm where ofc.fee_category_id = ofm.fk_fee_category_id group by ofc.fee_category_id ) as total_amount,
ofc.due_date
from o_fee_category ofc, o_fee_identity_mapping ofim
where ofim.fk_fee_category_id = ofc.fee_category_id
and ofim.fk_identity_id is not null;

