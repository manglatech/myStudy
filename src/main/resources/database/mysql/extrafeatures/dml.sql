update o_bs_identity set fk_institute_id = "0"

-- Create Institute and User From GUI.
-- Change Institue Id for Users:
select * from o_bs_identity
update o_bs_identity set fk_institute_id = "1" where name = 'institute1_admin'

update o_bs_identity set fk_institute_id = "2" where name = 'institute2_admin'

select * from o_gp_business_v

select * from o_institute_features_control