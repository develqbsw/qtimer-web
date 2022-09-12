-- View: v_employees_status

 DROP VIEW v_employees_status;

CREATE OR REPLACE VIEW v_employees_status AS 
 SELECT tmp.pk_id,
    tmp.c_client_id,
    tmp.c_emp_code,
    tmp.c_surname,
    tmp.c_name,
    tmp.c_start,
    tmp.c_stop,
    tmp.tmpday,
    tmp.c_status,
    tmp.c_absence_info,
    tmp.c_outside,
    tmp.c_home_office,
    tmp.c_flag_workplace,
    tmp.c_zone_id,
    tmp.c_office_number,
    tmp.c_contact_email,
    tmp.c_contact_phone,
    tmp.fk_user_photo
   FROM ( SELECT doch.pk_id,
            doch.c_client_id,
            doch.c_emp_code,
            doch.c_surname,
            doch.c_name,
            doch.c_start,
            doch.c_stop,
            doch.tmpday,
            doch.c_status,
                CASE
                    WHEN requests.type IS NOT NULL THEN (((((requests.stav::text || ','::text) || requests.type::text) || ','::text) || requests."from") || ','::text) || requests."to"
                    ELSE
                    CASE
                        WHEN breaks.activity_id = '-1'::integer::numeric THEN 'WORK_BREAK'::text
                        WHEN breaks.activity_id = '-2'::integer::numeric THEN 'LUNCH'::text
                        WHEN breaks.activity_id = '-7'::integer::numeric THEN 'PARAGRAPH'::text
                        ELSE NULL::text
                    END
                END AS c_absence_info,
            doch.outside AS c_outside,
            doch.home_office AS c_home_office,
                CASE
                    WHEN (doch.ts_outside is false and doch.ts_home_office is false) AND (doch.c_status = 'IN_WORK'::text OR doch.c_status = 'OUT_OF_WORK'::text AND (breaks.activity_id in (-1,-2,-7))) THEN true
                    ELSE false
                END AS c_flag_workplace,
            doch.fk_zone AS c_zone_id,
            doch.c_office_number,
            doch.c_contact_email,
            doch.c_contact_phone,
            doch.fk_user_photo
           FROM ( SELECT u.pk_id,
                    u.fk_client AS c_client_id,
                    u.c_emp_code,
                    u.c_surname,
                    u.c_name,
                    ts.c_start,
                    ts.c_stop,
                    u.datum::timestamp without time zone AS tmpday,
                        CASE
                            WHEN ts.stav_v_praci = 'IN_WORK'::text THEN 'IN_WORK'::text
                            WHEN ts.stav_v_praci = 'OUT_OF_WORK'::text THEN 'OUT_OF_WORK'::text
                            ELSE 'NOT_IN_WORK'::text
                        END AS c_status,
                        CASE
                            WHEN ts.stav_v_praci = 'IN_WORK'::text THEN
                            CASE
                                WHEN ts.outside = true THEN 'YES'::text
                                WHEN ts.outside = false THEN 'NO'::text
                                ELSE 'NO'::text
                            END
                            ELSE NULL::text
                        END AS outside,
                        CASE
                            WHEN ts.stav_v_praci = 'IN_WORK'::text THEN
                            CASE
                                WHEN ts.home_office = true THEN 'YES'::text
                                WHEN ts.home_office = false THEN 'NO'::text
                                ELSE 'NO'::text
                            END
                            ELSE NULL::text
                        END AS home_office,
                    ts.outside AS ts_outside,
                    ts.home_office AS ts_home_office,
                    u.fk_zone,
                    u.c_office_number,
                    u.c_contact_email,
                    u.c_contact_phone,
                    u.fk_user_photo
                   FROM ( SELECT ( SELECT 'now'::text::date AS date) AS datum,
                            u1.pk_id,
                            u1.fk_user_type,
                            u1.c_login,
                            u1.c_password,
                            u1.c_name,
                            u1.c_surname,
                            u1.c_flag_valid,
                            u1.fk_client,
                            u1.c_emp_code,
                            u1.c_contact_email,
                            u1.c_contact_phone,
                            u1.c_contact_mobile,
                            u1.c_contact_street,
                            u1.c_contact_street_number,
                            u1.c_contact_zip,
                            u1.c_contact_country,
                            u1.c_note,
                            u1.c_flag_main,
                            u1.fk_user_changedby,
                            u1.c_datetime_changed,
                            u1.c_city,
                            u1.c_autologin_token,
                            u1.c_login_long,
                            u1.c_pin_code,
                            u1.c_pin_code_salt,
                            u1.c_password_salt,
                            u1.c_flag_edit_time,
                            u1.c_flag_system_email,
                            u1.fk_zone,
                            u1.c_office_number,
                            u1.fk_user_photo
                           FROM t_user u1
                          WHERE u1.fk_user_type = 3::numeric) u
                     LEFT JOIN ( SELECT table_start."user",
                            table_start.c_time_from AS c_start,
                            table_stop.c_time_to AS c_stop,
                            table_start.for_day,
                                CASE
                                    WHEN table_stop.c_time_to IS NULL THEN 'IN_WORK'::text
                                    ELSE 'OUT_OF_WORK'::text
                                END AS stav_v_praci,
                            table_stop.c_flag_outside AS outside,
                            table_stop.c_flag_home_office AS home_office
                           FROM ( SELECT tsr.fk_user_owner AS "user",
                                    min(tsr.c_time_from) AS c_time_from,
                                    max(tsr.c_time_from) AS max_start,
                                    date_trunc('day'::text, tsr.c_time_from) AS for_day
                                   FROM t_time_sheet_record tsr,
                                    t_ct_activity a
                                  WHERE tsr.fk_activity = a.pk_id AND tsr.c_flag_valid = true AND a.c_flag_valid = true AND a.c_flag_working = true AND tsr.c_time_from >= date_trunc('day'::text, now()) AND tsr.c_time_from <= (date_trunc('day'::text, now() + '1 day'::interval) - '00:00:00.000001'::interval)
                                  GROUP BY tsr.fk_user_owner, (date_trunc('day'::text, tsr.c_time_from))) table_start,
                            t_time_sheet_record table_stop
                          WHERE table_start.max_start = table_stop.c_time_from AND table_stop.c_flag_valid = true AND table_stop.fk_user_owner = table_start."user") ts ON u.pk_id = ts."user" AND u.datum = ts.for_day
                  WHERE u.c_flag_valid = true AND u.fk_user_type = 3::numeric AND u.c_flag_main = false) doch
             LEFT JOIN ( SELECT (( SELECT 'now'::text::date AS date))::timestamp without time zone AS datum,
                    r.c_date_from,
                    r.c_date_to,
                    r.fk_user_owner,
                    r.fk_status,
                    rs.c_msg_code AS stav,
                    r.fk_request_type,
                    rt.c_code AS type,
                    r.c_date_from AS "from",
                    r.c_date_to AS "to",
                    r.pk_id,
                    r.fk_client
                   FROM t_request r,
                    t_ct_request_type rt,
                    t_ct_request_status rs
                  WHERE rt.pk_id = r.fk_request_type AND rs.pk_id = r.fk_status AND (r.fk_status = ANY (ARRAY[3::numeric])) AND (( SELECT 'now'::text::date AS date)) >= r.c_date_from AND (( SELECT 'now'::text::date AS date)) <= r.c_date_to) requests ON doch.pk_id = requests.fk_user_owner AND doch.tmpday = requests.datum
             LEFT JOIN ( SELECT tsr.fk_user_owner AS "user",
                    date_trunc('day'::text, tsr.c_time_from) AS for_day,
                    a2.pk_id AS activity_id
                   FROM t_time_sheet_record tsr,
                    t_ct_activity a2
                  WHERE tsr.fk_activity = a2.pk_id AND tsr.c_flag_valid = true AND tsr.c_time_to IS NULL AND a2.c_flag_valid = true AND a2.c_flag_working = false AND (tsr.c_time_from IN ( SELECT max(tmp_1.c_time_from) AS max
                           FROM t_time_sheet_record tmp_1,
                            t_ct_activity a3
                          WHERE tmp_1.fk_activity = a3.pk_id AND tmp_1.c_flag_valid = true AND tmp_1.c_time_to IS NULL AND a3.c_flag_valid = true AND a3.c_flag_working = false AND tmp_1.fk_user_owner = tsr.fk_user_owner AND tmp_1.c_time_from >= date_trunc('day'::text, now()) AND tmp_1.c_time_from <= (date_trunc('day'::text, now() + '1 day'::interval) - '00:00:00.000001'::interval)))) breaks ON doch.pk_id = breaks."user" AND doch.tmpday = breaks.for_day) tmp;

ALTER TABLE v_employees_status
  OWNER TO qtimerdev_adm;
GRANT ALL ON TABLE v_employees_status TO qtimerdev_adm;
GRANT SELECT ON TABLE v_employees_status TO qtimerdev_gw;
GRANT SELECT ON TABLE v_employees_status TO qtimerdev_preview;
