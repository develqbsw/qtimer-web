-- View: v_employees

DROP VIEW v_employees;

CREATE OR REPLACE VIEW v_employees AS 
select u.pk_id user_id,
       u.c_emp_code id,
       u.c_surname|| ' ' ||u.c_name meno_priezvisko,
       u.c_identification_number rodne_cislo,
       u.c_crn ico,
       u.c_vatin dic,
       to_char (u.c_birth_date, 'DD.MM.YYYY') datum_narodenia,
       et.c_description PP_typ,
       to_char (u.c_work_start_date, 'DD.MM.YYYY') prac_pomer_od,
       to_char (u.c_work_end_date, 'DD.MM.YYYY') Prac_pomer_do,
       u.c_position_name zaradenie,
       u.c_title titul,
       u.c_birth_place miesto_narodenia,
       u.c_resident_identity_card_number cislo_OP,
       u.c_contact_street tp_ulica,
       u.c_contact_street_number tp_cislo,
       u.c_contact_zip tp_psc,
       u.c_city tp_mesto,
       u.c_health_insurance_company zdravotna_poistovna,
       u.c_bank_account_number cislo_uctu,
       u.c_bank_institution banka,
	   u.fk_client,
	   u.c_flag_valid,
	   u.c_flag_list_criminal_records,
	   u.c_flag_recruit_medical_check,
	   u.c_flag_multisport_card
  from t_user u, t_ct_type_of_employment et
 where u.fk_type_of_employment = et.pk_id
 order by et.pk_id, u.c_surname, u.c_name, u.pk_id;

ALTER TABLE v_employees
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE v_employees TO qtimerdev_adm;
GRANT SELECT ON TABLE v_employees TO qtimerdev_gw;
GRANT SELECT ON TABLE v_employees TO qtimerdev_preview;