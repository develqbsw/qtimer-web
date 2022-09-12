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
  when date_part('dow', tsr.c_time_from) in (6,7) or h.pk_id is not null or (rt.pk_id is not null and tsr.c_time_to - tsr.c_time_from > interval '4 hour')
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
