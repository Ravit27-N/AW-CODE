select
    ca.id,
    ca.salutation,
    cs.id as status_id,
    ca.firstname as first_name,
    ca.lastname as last_name,
    ca.gpa,
    ca.gender,
    ca.date_of_birth,
    ca.year_of_experience,
    ca.email,
    ca.photo_url as photo_url,
    ca.telephone,
    ca.active,
    ca.priority,
    ca.created_at as created_at,
    greatest(ca.updated_at, cs.updated_at, i.updated_at, r.updated_at, r2.updated_at, cu.updated_at) as updated_at,
    ca.description,
    ca.is_deleted,
    (select count(candidate_id) from interview where candidate_id = ca.id and is_delete = false) as interview_count,
    (select count(candidate_id) from reminder where candidate_id = ca.id and deleted = false) as reminder_count,
    json_build_object('id', cs.id, 'title', cs.title, 'active', cs.active)::text as candidate_status,
    case
        when i.candidate_id is null then '{}'
        else json_build_object('id', i.id, 'title', i.title, 'last_interview',
                               to_char(i.date_time, 'dd-MM-yyyy HH24:MI:SS'),
                               'result', case when r2.interview_id is null then '{}'
                                              else json_build_object('id', r2.id, 'score', r2.score, 'average', round(cast(r2.average as numeric), 2), 'oral', oral, 'english', english, 'logical', logical, 'flexibility', flexibility) end
            )::text
        end interviews,
    case
        when cu.candidate_id is null then '[]'
        else json_build_array(json_build_object('id', u.id, 'name', u.name))::text
        end universities
from candidate ca
         left join candidate_status cs on ca.status_id = cs.id
         left join interview i on ca.id = i.candidate_id and i.id = (select id from interview where candidate_id = ca.id order by date_time desc limit 1) and is_delete = false
         left join reminder r on ca.id = r.candidate_id and deleted = false
         left join result r2 on i.id = r2.interview_id
         left join candidate_university cu on ca.id = cu.candidate_id
         left join (select id, name from university) u on cu.university_id = u.id
where ca.updated_at > :sql_last_value or cs.updated_at > :sql_last_value or i.updated_at > :sql_last_value or r.updated_at > :sql_last_value or r2.updated_at > :sql_last_value or cu.updated_at > :sql_last_value
group by ca.id, cs.id, i.id, i.candidate_id, r.candidate_id, r2.id, cu.candidate_id, u.id, u.name, r.updated_at, cu.updated_at