create or replace view o_user_account_summary_v as
select ofim.fk_identity_id, ofc.fee_category_id as fk_fee_category_id, ofc.fk_institute_id as institute_id,ofim.paid_status,
(select sum(ofip.paid_amount) from o_fee_identity_paid ofip where ofip.fk_fee_identity_mapping_id = ofim.fee_identity_mapping_id group by ofip.fk_fee_identity_mapping_id )as user_paid_amount, 
(select sum(ofm.price) from o_fee_mapping ofm where ofc.fee_category_id = ofm.fk_fee_category_id group by ofc.fee_category_id ) as total_amount,
ofc.due_date
from o_fee_category ofc, o_fee_identity_mapping ofim
where ofim.fk_fee_category_id = ofc.fee_category_id
and ofim.fk_identity_id is not null

-- UnPaid
select * from o_user_account_summary_v 
where (user_paid_amount is null or user_paid_amount = 0)
and paid_status = 0

-- Partial Paid
select * from o_user_account_summary_v 
where (total_amount > user_paid_amount)
and paid_status = 0

-- Paid
select * from o_user_account_summary_v 
where (total_amount is not null  && user_paid_amount  = total_amount)
and paid_status = 0

-- Mark As Paid
select * from o_user_account_summary_v 
where paid_status = 5

-- Over Paid
select * from o_user_account_summary_v 
where (user_paid_amount > total_amount)
and paid_status = 0

alter table o_fee_category add column due_date datetime;



