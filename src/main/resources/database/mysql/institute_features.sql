create table if not exists o_institute_features_control (
   institute_features_control_id bigint not null,
   institute_id varchar(20) not null,
   feature_id bigint not null,
   feature_name varchar(20) not null,
   primary key (institute_features_control_id)
);