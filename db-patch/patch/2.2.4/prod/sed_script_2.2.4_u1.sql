update t_time_sheet_record
   set c_flag_home_office=false
 where c_flag_home_office is null;