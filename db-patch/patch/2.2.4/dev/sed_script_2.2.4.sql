-- Table: t_request_notification

-- DROP TABLE t_request_notification;

CREATE TABLE t_request_notification
(
  fk_user_request numeric(10,0) NOT NULL,
  fk_user_notify numeric(10,0) NOT NULL,
  CONSTRAINT fk_notification__userrequest FOREIGN KEY (fk_user_request)
      REFERENCES t_user (pk_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_notification__usernotify FOREIGN KEY (fk_user_notify)
      REFERENCES t_user (pk_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_request_notification
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE t_request_notification TO qtimerdev_adm;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE t_request_notification TO qtimerdev_gw;
GRANT SELECT ON TABLE t_request_notification TO qtimerdev_preview;



-- Table: t_ct_home_office_permission

-- DROP TABLE t_ct_home_office_permission;

CREATE TABLE t_ct_home_office_permission
(
  pk_id numeric(10,0) NOT NULL,
  c_msg_code character varying(50) NOT NULL,
  c_description character varying(1000),
  CONSTRAINT pk_c_ct_home_office_permission PRIMARY KEY (pk_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_ct_home_office_permission
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE t_ct_home_office_permission TO qtimerdev_adm;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE t_ct_home_office_permission TO qtimerdev_gw;
GRANT SELECT ON TABLE t_ct_home_office_permission TO qtimerdev_preview;


INSERT INTO t_ct_home_office_permission
  (pk_id, c_msg_code, c_description)
VALUES
  (1, 'PERMISSIONTYPE_NOTALLOWED', 'Nepovolená');
INSERT INTO t_ct_home_office_permission
  (pk_id, c_msg_code, c_description)
VALUES
  (2, 'PERMISSIONTYPE_REQUESTREQUIRED', 'Iba so žiadosťou');
INSERT INTO t_ct_home_office_permission
  (pk_id, c_msg_code, c_description)
VALUES
  (3, 'PERMISSIONTYPE_ANYTIME', 'Kedykoľvek');

alter table t_user add column fk_home_office_permission numeric(10,0);
alter table t_user_hist add column fk_home_office_permission numeric(10,0);

update t_user set fk_home_office_permission = 2;

alter table t_user add constraint fk_user__home_office_permission foreign key (fk_home_office_permission) references t_ct_home_office_permission (pk_id) match simple on update no action on delete no action;
    
alter table t_time_sheet_record add column c_flag_home_office boolean;
alter table t_time_sheet_record_hist add column c_flag_home_office boolean;
alter table t_tmp_time_sheet_record add column c_flag_home_office boolean;

-- Function: public.f_archiver()

-- DROP FUNCTION public.f_archiver();

CREATE OR REPLACE FUNCTION public.f_archiver()
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
        c_flag_last, c_flag_last_working, c_flag_last_nonworking,fk_status, c_flag_home_office)
     VALUES
       (NEW.pk_id, NEW.fk_client, NEW.fk_user_owner, NEW.fk_user_createdby, NEW.fk_user_changedby, 
        NEW.c_datetime_changed, NEW.c_time_from, NEW.c_time_to, NEW.fk_activity, NEW.fk_project, 
        NEW.c_flag_valid, substr(TG_OP,1,1),nextval('S_TIME_SHEET_RECORD_HIST'), NEW.c_note, NEW.c_flag_outside, NEW.c_phase, NEW.fk_reason,
        NEW.c_flag_last, NEW.c_flag_last_working, NEW.c_flag_last_nonworking, NEW.fk_status, NEW.c_flag_home_office);
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
        c_bank_institution, c_birth_place, c_position_name, c_flag_absent_check,
    c_flag_list_criminal_records, c_flag_recruit_medical_check, c_flag_multisport_card, fk_home_office_permission)
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
        NEW.c_bank_institution, NEW.c_birth_place, NEW.c_position_name, NEW.c_flag_absent_check,
    NEW.c_flag_list_criminal_records, NEW.c_flag_recruit_medical_check, NEW.c_flag_multisport_card, NEW.fk_home_office_permission);
   ELSE
   ------------------------------------
     RAISE EXCEPTION '[SED_ERROR] Undefined trigger action for table "%" ', TG_RELNAME;
   END IF;
   
  RETURN NULL;
end;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_archiver()
  OWNER TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION public.f_archiver() TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION public.f_archiver() TO public;
GRANT EXECUTE ON FUNCTION public.f_archiver() TO qtimerdev_gw;

-- View: public.v_timestamps

-- DROP VIEW public.v_timestamps;

CREATE OR REPLACE VIEW public.v_timestamps AS 
 SELECT ts.pk_id,
    u.pk_id AS fk_user,
    u.fk_client,
    u.c_name AS c_user_name,
    u.c_surname AS c_user_surname,
    ts.c_time_from,
    ts.c_time_to,
    date_part('day'::text, ts.c_time_from) AS c_date_day,
    date_part('month'::text, ts.c_time_from) AS c_date_month,
    date_part('year'::text, ts.c_time_from) AS c_date_year,
    ts.c_note,
    ts.c_phase,
    ts.c_flag_outside,
    a.pk_id AS fk_activity,
    a.c_name AS c_activity_name,
    a.c_flag_working,
    p.c_name AS c_project_name,
    p.pk_id AS fk_project,
    uch.c_name AS c_changedby_name,
    uch.c_surname AS c_changedby_surname,
    ts.fk_reason,
    rr.c_name AS c_reason_name,
    ts.fk_status AS c_status,
    s.c_description AS c_status_description,
    (date_part('epoch'::text, date_trunc('milliseconds'::text, ts.c_time_to - ts.c_time_from)) + 0.001::double precision) / 60::double precision AS c_duration_minutes,
    a.c_flag_sum,
    a.c_flag_export,
    ts.c_flag_home_office
   FROM t_ct_tsr_status s,
    t_user u,
    t_ct_activity a,
    t_user uch,
    t_time_sheet_record ts
     LEFT JOIN t_ct_project p ON ts.fk_project = p.pk_id
     LEFT JOIN t_request_reason rr ON ts.fk_reason = rr.pk_id
  WHERE ts.fk_user_owner = u.pk_id AND ts.fk_client = u.fk_client AND ts.fk_activity = a.pk_id AND ts.fk_user_changedby = uch.pk_id AND ts.c_flag_valid = true AND ts.fk_status = s.pk_id;

ALTER TABLE public.v_timestamps
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE public.v_timestamps TO qtimerdev_adm;
GRANT SELECT ON TABLE public.v_timestamps TO qtimerdev_gw;
GRANT SELECT ON TABLE public.v_timestamps TO qtimerdev_preview;

-- Function: f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone)

-- DROP FUNCTION f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION f_sed_get_working_time(
    infk_user_owner numeric,
    idtime_from timestamp without time zone,
    idtime_to timestamp without time zone)
  RETURNS character varying AS
$BODY$
DECLARE
 vtime varchar;
BEGIN

select to_char(c_sum_time,'hh24:mi')||'#'||case when c_count_time=0 then '00:00' else to_char(date_trunc('minute',c_sum_time/c_count_time)+case when date_part('second',c_sum_time/c_count_time)<30 then interval '0 minute' else interval '1 minute' end ,'hh24:mi') end
into vtime
from(
select sum (s.c_time) c_sum_time, count(distinct c_date_day) -count(distinct c_date_minus_full_day) -0.5*count(distinct c_date_minus_half_day) c_count_time from(
select 
case
  when a.c_flag_sum then
    date_trunc('minute',coalesce(tsr.c_time_to, now()) - tsr.c_time_from) + interval '1 minute'
  else
    null
  end c_time,

  date_trunc('day',tsr.c_time_from) c_date_day,
case
  when date_part('dow', tsr.c_time_from) in (6,0) or h.pk_id is not null or (rt.pk_id is not null and tsr.c_time_to - tsr.c_time_from > interval '4 hour')
  then
    date_trunc('day',tsr.c_time_from)
  else
    null
  end c_date_minus_full_day,

case
  when rt.pk_id is not null and tsr.c_time_to - tsr.c_time_from <= interval '4 hour' then
    date_trunc('day',tsr.c_time_from)
  else
    null
  end c_date_minus_half_day

  from t_time_sheet_record tsr

LEFT JOIN t_ct_activity a
    ON tsr.fk_activity = a.pk_id
   and a.fk_client = tsr.fk_client
LEFT JOIN t_ct_holiday h
    ON date_trunc('day', tsr.c_time_from) = h.c_day
   and h.c_flag_valid = true
   and h.fk_client = tsr.fk_client
LEFT JOIN t_ct_request_type rt
    ON tsr.fk_activity = rt.fk_activity

 where tsr.c_flag_valid=true
   and (tsr.c_time_to is not null or date_trunc('day',tsr.c_time_from)=current_date)
   and date_trunc('day',tsr.c_time_from)<=now()
   and tsr.fk_user_owner=infk_user_owner and date_trunc('day',tsr.c_time_from)<=idtime_to and coalesce(tsr.c_time_to,now())>=idtime_from
) s) t;
  if vtime is null then
    vtime := '00:00#00:00';
  end if;
  return vtime;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone)
  OWNER TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO qtimerdev_adm;
GRANT EXECUTE ON FUNCTION f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO public;
GRANT EXECUTE ON FUNCTION f_sed_get_working_time(numeric, timestamp without time zone, timestamp without time zone) TO qtimerdev_gw;