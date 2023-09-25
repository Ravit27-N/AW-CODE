select
    c.id,
    c.firstname as first_name,
    c.lastname as last_name,
    c.salutation,
    c.telephone,
    c.gpa,
    c.date_of_birth,
    c.year_of_experience,
    c.gender,
    c.active,
    c.created_at,
    greatest(c.updated_at, i.updated_at, r.updated_at, cu.updated_at, u.updated_at) as updated_at,
    c.photo_url,
    c.email,
    c.priority,
    case
        when i.candidate_id is null then '{}'
        else json_build_object('id', i.id, 'title', i.title, 'description', i.description, 'date_time', i.date_time, 'result', case when r.interview_id is null then '{}' else json_build_object('id', r.id, 'score', r.score, 'average', round(cast(r.average as numeric), 2), 'oral', oral, 'english', english, 'logical', logical, 'flexibility', flexibility) end )::text
        end interviews,
    case
        when cu.candidate_id is null then '[]'
        else json_agg(distinct jsonb_build_object('id', u.id, 'name', u.name))::text
        end universities
from candidate c
         inner join interview i on c.id = i.candidate_id and i.id = (select id from interview where candidate_id = c.id order by date_time desc limit 1) and is_delete = false
         left join result r on i.id = r.interview_id
         left join candidate_university cu on c.id = cu.candidate_id
         left join university u on cu.university_id = u.id
where c.is_deleted = false and
    (c.updated_at > :sql_last_value or i.updated_at > :sql_last_value or r.updated_at > :sql_last_value or cu.updated_at > :sql_last_value or u.updated_at > :sql_last_value)
group by
    c.id, i.id, i.candidate_id, r.interview_id, r.id, cu.candidate_id, cu.updated_at, u.updated_at