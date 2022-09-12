-- View: public.v_week_sheet

-- DROP VIEW public.v_week_sheet;

CREATE OR REPLACE VIEW public.v_week_sheet AS 
 SELECT v_timestamps.pk_id,
    v_timestamps.fk_user AS c_user_id,
    v_timestamps.c_time_from,
    v_timestamps.c_time_to,
    to_timestamp((((date_part('day'::text, v_timestamps.c_time_from) || '.'::text) || date_part('month'::text, v_timestamps.c_time_from)) || '.'::text) || date_part('year'::text, v_timestamps.c_time_from), 'dd.MM.yyyy'::text) AS c_date,
    (v_timestamps.c_user_surname::text || ' '::text) || v_timestamps.c_user_name::text AS c_user,
    v_timestamps.c_project_name,
    v_timestamps.c_activity_name,
    v_timestamps.c_phase AS c_etapa_id,
    to_char(v_timestamps.c_time_to - v_timestamps.c_time_from + '1 minute','HH24:MI') AS c_duration,
    v_timestamps.c_note,
    v_timestamps.c_status,
    v_timestamps.c_status_description
   FROM v_timestamps
  WHERE v_timestamps.c_project_name IS NOT NULL
  ORDER BY ((v_timestamps.c_user_surname::text || ' '::text) || v_timestamps.c_user_name::text), (to_timestamp((((date_part('day'::text, v_timestamps.c_time_from) || '.'::text) || date_part('month'::text, v_timestamps.c_time_from)) || '.'::text) || date_part('year'::text, v_timestamps.c_time_from), 'dd.MM.yyyy'::text));

ALTER TABLE public.v_week_sheet
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE public.v_week_sheet TO qtimerdev_adm;
GRANT SELECT ON TABLE public.v_week_sheet TO qtimerdev_gw;
GRANT SELECT ON TABLE public.v_week_sheet TO qtimerdev_preview;

update t_request
   set c_date_last_gen_holiday = c_date_to
 where fk_request_type = 1
   and fk_status = 1
   and c_date_last_gen_holiday is null
   and c_date_to < to_date('310117', 'DDMMYY');
   
-- Function: public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone)

-- DROP FUNCTION public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION public.f_sed_get_working_time(
    infk_user_owner numeric,
    idtime_from timestamp without time zone,
    idtime_to timestamp without time zone)
  RETURNS character varying AS
$BODY$
DECLARE
 vtime varchar;
BEGIN

select 
to_char(c_sum_time,'hh24:mi')||'#'||
case when c_count_time=0 then '00:00' else to_char(date_trunc('minute',c_sum_time/c_count_time)+case when date_part('second',c_sum_time/c_count_time)<30 then interval '0 minute' else interval '1 minute' end ,'hh24:mi') end
into vtime
from(
select sum(date_trunc('minute',
                      coalesce(tsr.c_time_to, now()) - tsr.c_time_from) +
           interval '1 minute') c_sum_time,
       count(distinct case
               when date_part('dow', tsr.c_time_from) between 1 and 5 and
                    h.pk_id is null and
                    ((tsrr.c_time_to - tsrr.c_time_from) <= interval
                     '4 hour' or tsrr.c_time_from is null) then
                date_trunc('day', tsr.c_time_from)
               else
                null
             end) - 0.5 * count(distinct case
                                  when date_part('dow', tsr.c_time_from) between 1 and 5 and
                                       h.pk_id is null and
                                       (tsrr.c_time_to - tsrr.c_time_from) <= interval '4 hour' then
                                   date_trunc('day', tsr.c_time_from)
                                  else
                                   null
                                end) c_count_time
  from t_time_sheet_record tsr
  LEFT JOIN t_ct_activity a
    ON tsr.fk_activity = a.pk_id
   and a.fk_client = tsr.fk_client
  LEFT JOIN t_ct_holiday h
    ON date_trunc('day', tsr.c_time_from) = h.c_day
   and h.c_flag_valid = true
   and h.fk_client = tsr.fk_client
  LEFT JOIN t_time_sheet_record tsrr
    ON tsrr.fk_user_owner = tsr.fk_user_owner
   and tsrr.c_flag_valid = true
   and date_trunc('day', tsr.c_time_from) =
       date_trunc('day', tsrr.c_time_from)
   and exists (select *
          from t_ct_request_type rt
         where tsrr.fk_activity = rt.fk_activity)
where a.c_flag_sum=true
   and tsr.c_flag_valid=true
   and (tsr.c_time_to is not null or date_trunc('day',tsr.c_time_from)=current_date)
   and date_trunc('day',tsr.c_time_from)<=now()
   and tsr.fk_user_owner=infk_user_owner and date_trunc('day',tsr.c_time_from)<=idtime_to and coalesce(tsr.c_time_to,now())>=idtime_from
) t;
  if vtime is null then
    vtime := '00:00#00:00';
  end if;
  return vtime;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone)
  OWNER TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO public;
GRANT EXECUTE ON FUNCTION public.f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO qtimerdev_gw;

-- Table: t_ct_type_of_employment

-- DROP TABLE t_ct_type_of_employment;

CREATE TABLE t_ct_type_of_employment
(
  pk_id numeric(10,0) NOT NULL,
  c_msg_code character varying(50) NOT NULL,
  c_description character varying(1000),
  CONSTRAINT pk_c_ct_type_of_employment PRIMARY KEY (pk_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_ct_type_of_employment
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE t_ct_type_of_employment TO qtimerdev_adm;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE t_ct_type_of_employment TO qtimerdev_gw;
GRANT SELECT ON TABLE t_ct_type_of_employment TO qtimerdev_preview;

alter table t_user add column c_identification_number character varying(20);
alter table t_user_hist add column c_identification_number character varying(20);
alter table t_user add column c_crn character varying(20);
alter table t_user_hist add column c_crn character varying(20);
alter table t_user add column c_vatin character varying(20);
alter table t_user_hist add column c_vatin character varying(20);
alter table t_user add column fk_type_of_employment numeric(10,0);
alter table t_user_hist add column fk_type_of_employment numeric(10,0);
alter table t_user add column c_work_end_date timestamp without time zone;
alter table t_user_hist add column c_work_end_date timestamp without time zone;
alter table t_user add column c_title character varying(20);
alter table t_user_hist add column c_title character varying(20);
alter table t_user add column c_resident_identity_card_number character varying(20);
alter table t_user_hist add column c_resident_identity_card_number character varying(20);
alter table t_user add column c_health_insurance_company character varying(50);
alter table t_user_hist add column c_health_insurance_company character varying(50);
alter table t_user add column c_bank_account_number character varying(34);
alter table t_user_hist add column c_bank_account_number character varying(34);
alter table t_user add column c_bank_institution character varying(50);
alter table t_user_hist add column c_bank_institution character varying(50);
alter table t_user add column c_birth_place character varying(50);
alter table t_user_hist add column c_birth_place character varying(50);
alter table t_user add column c_flag_absent_check boolean;
alter table t_user_hist add column c_flag_absent_check boolean;
alter table t_user add column c_position_name character varying(100);
alter table t_user_hist add column c_position_name character varying(100);
alter table t_user add constraint fk_user__type_of_employment foreign key (fk_type_of_employment) references t_ct_type_of_employment (pk_id) match simple on update no action on delete no action;


INSERT INTO t_ct_type_of_employment
  (pk_id, c_msg_code, c_description)
VALUES
  (1, 'EMPLOYMENTTYPE_EMPLOYEE', 'Zamestnanec');
INSERT INTO t_ct_type_of_employment
  (pk_id, c_msg_code, c_description)
VALUES
  (2, 'EMPLOYMENTTYPE_WORKER', 'Pracovník');
INSERT INTO t_ct_type_of_employment
  (pk_id, c_msg_code, c_description)
VALUES
  (3, 'EMPLOYMENTTYPE_PART_TIME', 'Brigádnik');
INSERT INTO t_ct_type_of_employment
  (pk_id, c_msg_code, c_description)
VALUES
  (4, 'EMPLOYMENTTYPE_INACTIVE', 'Neuvedený');

-- Function: f_archiver()

-- DROP FUNCTION f_archiver();

CREATE OR REPLACE FUNCTION f_archiver()
  RETURNS trigger AS
$BODY$begin
   ------------------------------------
   IF TG_OP NOT IN ('INSERT','UPDATE') THEN
      RAISE EXCEPTION '[SED_ERROR] Operation "%" is not allowed on table "%"', TG_OP, TG_RELNAME;
   END IF;
   ------------------------------------
   IF    TG_RELNAME='t_client' THEN
     INSERT INTO t_client_hist 
       (pk_id, fk_legal_form, c_flag_valid, c_client_name, c_client_name_short, 
        c_identification_number, c_tax_number, c_tax_vat_number, c_contact_street, 
        c_contact_street_number, c_contact_zip, c_contact_country, fk_user_changedby, 
        c_datetime_changed, c_stmt_type, hist_id, c_flag_project_required, 
        c_flag_activity_required, c_time_auto_email, c_time_auto_gen_ts, 
        c_shift_day_auto_gen_ts, c_city, c_language, c_interval_stop_work_rec,
    c_flag_generate_messages, c_bonus_vacation)
     VALUES (NEW.pk_id, NEW.fk_legal_form, NEW.c_flag_valid, NEW.c_client_name, NEW.c_client_name_short, 
        NEW.c_identification_number, NEW.c_tax_number, NEW.c_tax_vat_number, NEW.c_contact_street, 
        NEW.c_contact_street_number, NEW.c_contact_zip, NEW.c_contact_country, NEW.fk_user_changedby, 
        NEW.c_datetime_changed, substr(TG_OP,1,1), nextval('S_CLIENT_HIST'), NEW.c_flag_project_required, 
        NEW.c_flag_activity_required, NEW.c_time_auto_email, NEW.c_time_auto_gen_ts, 
        NEW.c_shift_day_auto_gen_ts, NEW.c_city, NEW.c_language, NEW.c_interval_stop_work_rec,
    NEW.c_flag_generate_messages, NEW.c_bonus_vacation);
   ------------------------------------
   ELSIF TG_RELNAME='t_ct_activity' THEN
     INSERT INTO t_ct_activity_hist
       (pk_id, fk_client, c_client_order, c_flag_valid, c_flag_working, c_flag_export, c_time_max, c_time_min, c_hours_max, c_flag_sum,
        fk_user_changedby, c_datetime_changed, c_name, c_note, c_stmt_type, hist_id, c_flag_changeable, c_flag_default)
     VALUES 
       (NEW.pk_id, NEW.fk_client, NEW.c_client_order, NEW.c_flag_valid, NEW.c_flag_working, NEW.c_flag_export, NEW.c_time_max, NEW.c_time_min, NEW.c_hours_max, NEW.c_flag_sum,
        NEW.fk_user_changedby, NEW.c_datetime_changed, NEW.c_name, NEW.c_note, substr(TG_OP,1,1),nextval('S_CT_ACTIVITY_HIST'), 
  NEW.c_flag_changeable, NEW.c_flag_default);
   ------------------------------------
   ELSIF TG_RELNAME='t_ct_project' THEN
     INSERT INTO t_ct_project_hist
       (pk_id, fk_client, c_client_order, c_flag_valid, fk_user_changedby, 
        c_datetime_changed, c_name, c_note, c_group, c_id, c_stmt_type, hist_id, c_flag_default)
     VALUES 
       (NEW.pk_id, NEW.fk_client, NEW.c_client_order, NEW.c_flag_valid, NEW.fk_user_changedby, 
        NEW.c_datetime_changed, NEW.c_name, NEW.c_note, NEW.c_group, NEW.c_id, substr(TG_OP,1,1),nextval('S_CT_PROJECT_HIST'), NEW.c_flag_default);
   ------------------------------------
   ELSIF TG_RELNAME='t_organization_tree' THEN
     INSERT INTO t_organization_tree_hist
       (pk_id, fk_client, fk_user_owner, c_position_name, fk_possition_superior, 
        fk_user_changedby, c_datetime_changed, c_stmt_type, hist_id)
    VALUES
       (NEW.pk_id, NEW.fk_client, NEW.fk_user_owner, NEW.c_position_name, NEW.fk_possition_superior, 
        NEW.fk_user_changedby, NEW.c_datetime_changed, substr(TG_OP,1,1),nextval('S_ORGANIZATION_TREE_HIST'));
   ------------------------------------
   ELSIF TG_RELNAME='t_request' THEN
     INSERT INTO t_request_hist
       (pk_id, fk_request_type, fk_status, c_date_from, c_date_to, c_hours_for_datefrom, 
        c_hours_for_dateto, c_number_of_working_days, c_place_description, 
        fk_client, fk_user_owner, fk_user_createdby, fk_user_changedby, c_datetime_changed, 
        c_note, c_stmt_type, hist_id, c_create_date, c_responsalis_name, fk_reason, c_code, 
        c_date_last_gen_holiday)
     VALUES
       (NEW.pk_id, NEW.fk_request_type, NEW.fk_status, NEW.c_date_from, NEW.c_date_to, NEW.c_hours_for_datefrom, 
        NEW.c_hours_for_dateto, NEW.c_number_of_working_days, NEW.c_place_description, 
        NEW.fk_client, NEW.fk_user_owner, NEW.fk_user_createdby, NEW.fk_user_changedby, NEW.c_datetime_changed, 
        NEW.c_note, substr(TG_OP,1,1),nextval('S_REQUEST_HIST'), NEW.c_create_date, NEW.c_responsalis_name, NEW.fk_reason, NEW.c_code, 
        NEW.c_date_last_gen_holiday);
   ------------------------------------
   ELSIF TG_RELNAME='t_time_sheet_record' THEN
     INSERT INTO t_time_sheet_record_hist
       (pk_id, fk_client, fk_user_owner, fk_user_createdby, fk_user_changedby, 
        c_datetime_changed, c_time_from, c_time_to, fk_activity, fk_project, 
        c_flag_valid, c_stmt_type, hist_id, c_note, c_flag_outside, c_phase, fk_reason,
        c_flag_last, c_flag_last_working, c_flag_last_nonworking,fk_status)
     VALUES
       (NEW.pk_id, NEW.fk_client, NEW.fk_user_owner, NEW.fk_user_createdby, NEW.fk_user_changedby, 
        NEW.c_datetime_changed, NEW.c_time_from, NEW.c_time_to, NEW.fk_activity, NEW.fk_project, 
        NEW.c_flag_valid, substr(TG_OP,1,1),nextval('S_TIME_SHEET_RECORD_HIST'), NEW.c_note, NEW.c_flag_outside, NEW.c_phase, NEW.fk_reason,
        NEW.c_flag_last, NEW.c_flag_last_working, NEW.c_flag_last_nonworking, NEW.fk_status);
   ------------------------------------
   ELSIF TG_RELNAME='t_user' THEN
     INSERT INTO t_user_hist
       (pk_id, fk_user_type, c_login, c_password, c_name, c_surname, 
        c_flag_valid, fk_client, c_emp_code, c_contact_email, c_contact_phone, 
        c_contact_mobile, c_contact_street, c_contact_street_number, 
        c_contact_zip, c_contact_country, c_note, c_flag_main, 
        fk_user_changedby, c_datetime_changed, c_stmt_type, hist_id, 
        c_pin_code, c_login_long, c_flag_edit_time, c_flag_system_email, 
        c_flag_alertness_work, c_city, c_autologin_token, c_pin_code_salt, 
        c_password_salt, c_language, c_jira_access_token,
        fk_zone, c_office_number, c_card_code, c_card_code_salt,c_table_rows,
        fk_user_photo, c_birth_date, c_work_start_date, c_vacation, c_vacation_next_year,
        c_identification_number, c_crn, c_vatin, fk_type_of_employment, c_work_end_date, 
        c_title, c_resident_identity_card_number, c_health_insurance_company, c_bank_account_number, 
        c_bank_institution, c_birth_place, c_position_name, c_flag_absent_check)
    VALUES
       (NEW.pk_id, NEW.fk_user_type, NEW.c_login, NEW.c_password, NEW.c_name, NEW.c_surname, 
        NEW.c_flag_valid, NEW.fk_client, NEW.c_emp_code, NEW.c_contact_email, NEW.c_contact_phone, 
        NEW.c_contact_mobile, NEW.c_contact_street, NEW.c_contact_street_number, 
        NEW.c_contact_zip, NEW.c_contact_country, NEW.c_note, NEW.c_flag_main, 
        NEW.fk_user_changedby, NEW.c_datetime_changed, substr(TG_OP,1,1), nextval('S_USER_HIST'), 
        NEW.c_pin_code, NEW.c_login_long, NEW.c_flag_edit_time, NEW.c_flag_system_email, 
        NEW.c_flag_alertness_work, NEW.c_city, NEW.c_autologin_token, NEW.c_pin_code_salt, 
        NEW.c_password_salt, NEW.c_language, NEW.c_jira_access_token,
        NEW.fk_zone, NEW.c_office_number, NEW.c_card_code, NEW.c_card_code_salt,NEW.c_table_rows,
        NEW.fk_user_photo, NEW.c_birth_date, NEW.c_work_start_date, NEW.c_vacation, NEW.c_vacation_next_year,
        NEW.c_identification_number, NEW.c_crn, NEW.c_vatin, NEW.fk_type_of_employment, NEW.c_work_end_date,
        NEW.c_title, NEW.c_resident_identity_card_number, NEW.c_health_insurance_company, NEW.c_bank_account_number,
        NEW.c_bank_institution, NEW.c_birth_place, NEW.c_position_name, NEW.c_flag_absent_check);
   ELSE
   ------------------------------------
     RAISE EXCEPTION '[SED_ERROR] Undefined trigger action for table "%" ', TG_RELNAME;
   END IF;
   
  RETURN NULL;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION f_archiver()
  OWNER TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION f_archiver() TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION f_archiver() TO public;
GRANT EXECUTE ON FUNCTION f_archiver() TO qtimerdev_gw;


UPDATE t_user u
   SET c_position_name =
       (SELECT o.c_position_name
          FROM t_organization_tree o
         where o.fk_user_owner = u.pk_id);

UPDATE t_user
   SET c_flag_absent_check = true;

alter table t_user ALTER COLUMN c_flag_absent_check SET NOT NULL;

-- View: v_organization_tree

DROP VIEW v_organization_tree;

CREATE OR REPLACE VIEW v_organization_tree AS 
 SELECT t.pk_id,
    t.fk_possition_superior,
    u.c_login_long,
    u.c_name,
    u.c_surname,
    u.c_flag_valid,
    u.pk_id AS user_pk_id,
    t.fk_client
   FROM t_organization_tree t,
    t_user u
  WHERE t.fk_user_owner = u.pk_id AND t.fk_client = u.fk_client AND u.fk_user_type = 3::numeric;

ALTER TABLE v_organization_tree
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE v_organization_tree TO qtimerdev_adm;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_organization_tree TO qtimerdev_gw;
GRANT SELECT ON TABLE v_organization_tree TO qtimerdev_preview;

-- View: v_employees

-- DROP VIEW v_employees;

CREATE OR REPLACE VIEW v_employees AS 
 select u.c_emp_code id,
       u.c_name|| ' ' ||u.c_surname meno_priezvisko,
       u.c_identification_number rodne_cislo,
       u.c_crn ico,
       u.c_vatin dic,
       to_char (u.c_birth_date, 'DD.MM.YYYY') datum_narodenia,
       et.c_description PP_typ,
       to_char (u.c_work_start_date, 'DD.MM.YYYY') prac_pomer_od,
       to_char (u.c_work_end_date, 'DD.MM.YYYY') prac_pomer_do,
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
	   u.c_flag_valid
  from t_user u, t_ct_type_of_employment et
 where u.fk_type_of_employment = et.pk_id
 order by et.pk_id, u.c_surname, u.c_name, u.pk_id;

ALTER TABLE v_employees
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE v_employees TO qtimerdev_adm;
GRANT SELECT ON TABLE v_employees TO qtimerdev_gw;
GRANT SELECT ON TABLE v_employees TO qtimerdev_preview;