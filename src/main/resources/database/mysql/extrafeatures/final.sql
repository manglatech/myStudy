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

create or replace view o_gp_business_v  as (
   select
      gp.group_id as group_id,
      gp.groupname as groupname,
      gp.lastmodified as lastmodified,
      gp.creationdate as creationdate,
      gp.lastusage as lastusage,
      gp.descr as descr,
      gp.minparticipants as minparticipants,
      gp.maxparticipants as maxparticipants,
      gp.waitinglist_enabled as waitinglist_enabled,
      gp.autocloseranks_enabled as autocloseranks_enabled,
      (select count(part.id) from o_bs_membership as part where part.secgroup_id = gp.fk_partipiciantgroup) as num_of_participants,
      (select count(own.id) from o_bs_membership as own where own.secgroup_id = gp.fk_ownergroup) as num_of_owners,
      (case when gp.waitinglist_enabled = 1
         then 
           (select count(waiting.id) from o_bs_membership as waiting where waiting.secgroup_id = gp.fk_partipiciantgroup)
         else
           0
      end) as num_waiting,
      (select count(offer.offer_id) from o_ac_offer as offer 
         where offer.fk_resource_id = gp.fk_resource
         and offer.is_valid=1
         and (offer.validfrom is null or offer.validfrom <= current_timestamp())
         and (offer.validto is null or offer.validto >= current_timestamp())
      ) as num_of_valid_offers,
      (select count(offer.offer_id) from o_ac_offer as offer 
         where offer.fk_resource_id = gp.fk_resource
         and offer.is_valid=1
      ) as num_of_offers,
      (select count(relation.fk_resource) from o_gp_business_to_resource as relation 
         where relation.fk_group = gp.group_id
      ) as num_of_relations,
      gp.fk_resource as fk_resource,
      gp.fk_ownergroup as fk_ownergroup,
      gp.fk_partipiciantgroup as fk_partipiciantgroup,
      gp.fk_waitinggroup as fk_waitinggroup,
      gp.fk_institute_id as fk_institute_id
   from o_gp_business as gp
);

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
 (1,'2',1,'CP Learning content',0),
 (2,'2',2,'SCORM LC',0),
 (3,'2',3,'Course LC',1),
 (4,'2',4,'Wiki LC',0),
 (5,'2',5,'Podcast LC',1),
 (6,'2',6,'Blogs LC',0),
 (7,'2',7,'Portfolio LC',0),
 (8,'2',8,'Test LC',1),
 (9,'2',9,'Quasitionaries LC',0),
 (10,'2',10,'Resource Folder LC',1),
 (11,'2',11,'Glossary LC',0),
 (12,'2',12,'Single Page CC',1),
 (13,'2',13,'Forum CC',1),
 (14,'2',14,'File Dialough CC',0),
 (15,'2',15,'Self Test CC',1),
 (16,'2',16,'Enrollment CC',0),
 (17,'2',17,'Calendar Module',1),
 (18,'2',18,'Folder CC',1),
 (19,'2',19,'Task CC',1),
 (20,'2',20,'External Page CC',1),
 (21,'2',21,'Structure CC',1),
 (22,'2',22,'Assesment Module',0),
 (23,'2',23,'Topic Assignment CC',1),
 (24,'2',24,'Email In CC',1),
 (25,'2',25,'Notifications In CC',0),
 (26,'2',26,'LTI CC',0),
 (27,'2',27,'Participant List CC',0),
 (28,'2',28,'Link List CC',0),
 (29,'2',29,'Bookmark Module',0),
 (30,'2',30,'User Personal Folder',1),
 (31,'2',31,'Notes Module',1),
 (32,'2',32,'User Search Module',1),
 (33,'2',33,'HomePage',1),
 (34,'2',34,'WebDAV Module',0),
 (35,'2',35,'CATALOG Module',0);

create or replace view o_user_account_summary_v as
select ofim.fk_identity_id, ofc.fee_category_id as fk_fee_category_id,  
sum(paid_amount) as user_paid_amount, sum(ofm.price) as total_amount, ofc.fk_institute_id as institute_id,
ofim.paid_status
      from o_fee_category ofc
      left join o_fee_mapping ofm on ofc.fee_category_id = ofm.fk_fee_category_id
      left join o_fee_identity_mapping ofim on ofim.fk_fee_category_id = ofc.fee_category_id
      left join o_fee_identity_paid ofip on ofip.fk_fee_identity_mapping_id = ofim.fee_identity_mapping_id
      group by ofim.fk_identity_id, ofc.fee_category_id;
 
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
      

