INSERT INTO t_request_reason(
            pk_id, fk_client, fk_request_type, c_code, c_name, c_flag_valid, 
            fk_user_changedby, c_datetime_changed, c_flag_system)
    VALUES (nextval('S_REQUEST_REASON'), 107, 7, 'HO_EMPLOYEE_SPECI', 'Výnimočný z podnetu zamestnanca', TRUE, 
            395, current_timestamp, TRUE);
INSERT INTO t_request_reason(
            pk_id, fk_client, fk_request_type, c_code, c_name, c_flag_valid, 
            fk_user_changedby, c_datetime_changed, c_flag_system)
    VALUES (nextval('S_REQUEST_REASON'), 107, 7, 'HO_EMPLOYEE_PERIOD', 'Pravidelný z podnetu zamestnanca', TRUE, 
            395, current_timestamp, TRUE);
INSERT INTO t_request_reason(
            pk_id, fk_client, fk_request_type, c_code, c_name, c_flag_valid, 
            fk_user_changedby, c_datetime_changed, c_flag_system)
    VALUES (nextval('S_REQUEST_REASON'), 107, 7, 'HO_SUPERVISOR', 'Z podnetu nadriadeného', TRUE, 
            395, current_timestamp, TRUE);
    update t_request
       set fk_reason = (select pk_id from t_request_reason where c_code = 'HO_EMPLOYEE_SPECI') 
     where fk_request_type = 7;