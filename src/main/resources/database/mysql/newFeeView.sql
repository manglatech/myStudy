create or replace view o_user_account_summary_v as
select ofim.fk_identity_id, ofc.fee_category_id as fk_fee_category_id,  
sum(paid_amount) as user_paid_amount, sum(ofm.price) as total_amount, ofc.fk_institute_id as institute_id,
ofim.paid_status
      from o_fee_category ofc
      left join o_fee_mapping ofm on ofc.fee_category_id = ofm.fk_fee_category_id
      left join o_fee_identity_mapping ofim on ofim.fk_fee_category_id = ofc.fee_category_id
      left join o_fee_identity_paid ofip on ofip.fk_fee_identity_mapping_id = ofim.fee_identity_mapping_id
      group by ofim.fk_identity_id, ofc.fee_category_id
