drop table o_fee;
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

drop table o_fee_category;
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

drop table o_fee_mapping;
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


drop table o_fee_identity_mapping;
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


drop table o_fee_identity_paid;
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

