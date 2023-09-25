select
	r.id,
	r.reminder_type_id as reminder_type,
	r.title,
	r.date_reminder,
	r.description,
	case
		when r.candidate_id is null then '{}'
		else json_build_object('id', r.candidate_id, 'full_name', concat(c.salutation, ' ', c.firstname, ' ', c.lastname))::text
	end candidate,
    case
        when r.interview_id is null then '{}'
        else json_build_object('id', r.interview_id, 'title', i.title)::text
    end interview,
	r.created_at,
    r.deleted,
    r.active,
    greatest(r.created_at, c.updated_at, i.updated_at) as updated_at
from reminder r
    inner join reminder_type rt on r.reminder_type_id = rt.id
    left join candidate c on r.candidate_id = c.id
    left join interview i on r.interview_id = i.id
where (r.updated_at > :sql_last_value or c.updated_at > :sql_last_value or i.updated_at > :sql_last_value)
  and r.is_send = false
  and
    case
        when r.interview_id is not null then (i.is_delete = false and (select count(i2.id) from interview i2 inner join candidate c2 on i2.candidate_id = c2.id and c2.is_deleted = false and i2.id = r.interview_id) > 0)
        when r.candidate_id is not null then c.is_deleted = false
        else true
    end