create or replace view o_user_account_summary_v as
SELECT obi.name as user_name, obi.id as identity_id, 
ups.fee_category_id as fee_category_id, 
"DO_IT" as template_name, ups.total_amount, ups.user_paid_amount, obi.fk_institute_id as institute_id
FROM o_gp_business og
left join o_bs_membership obm on obm.secgroup_id = og.fk_partipiciantgroup
left join o_bs_identity obi on obi.id = obm.identity_id
left join user_account_paid_summary_v ups on (ups.fk_identity_id = obi.id and ups.fk_group_id = og.fk_partipiciantgroup)

create or replace view o_user_account_summary_v as
select ofim.fk_identity_id, ofc.fee_category_id as fk_fee_category_id,  
sum(paid_amount) as user_paid_amount, sum(ofm.price) as total_amount, ofc.fk_institute_id as institute_id 
      from o_fee_category ofc
      left join o_fee_mapping ofm on ofc.fee_category_id = ofm.fk_fee_category_id
      left join o_fee_identity_mapping ofim on ofim.fk_fee_category_id = ofc.fee_category_id
      left join o_fee_identity_paid ofip on ofip.fk_fee_identity_mapping_id = ofim.fee_identity_mapping_id
      group by ofim.fk_identity_id, ofc.fee_category_id, ofc.fk_group_id

      select * from o_user_account_summary_v