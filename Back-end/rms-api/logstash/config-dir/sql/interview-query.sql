select
    i.id,
    i.title,
    i.description,
    i.date_time,
    itvs."name"::text as status,
    count(r) as reminder_count,
    case
        when i.candidate_id is null then '{}'
            else json_build_object('id', c.id, 'full_name', concat(c.salutation ,' ', c.firstname ,' ', c.lastname))::text
        end as candidate,
    i.created_at,
    greatest(i.updated_at, c.updated_at, rs.updated_at, r.updated_at) as updated_at,
    rs.interview_id is not null as has_result,
    (i.is_delete or c.is_deleted) as is_deleted
from interview i
         inner join candidate c on i.candidate_id = c.id
         inner join interview_status itvs on i.interview_status_id = itvs .id
         left join "result" rs on i.id = rs.interview_id
         left join Reminder r on i.id = r.interview_id
where greatest(i.updated_at, c.updated_at, rs.updated_at, r.updated_at) > :sql_last_value
group by i.id, rs.id, c.id, itvs.id, r.id