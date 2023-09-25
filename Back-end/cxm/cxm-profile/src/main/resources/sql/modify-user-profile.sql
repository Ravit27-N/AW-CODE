
DO
$$
    DECLARE
        -- no need to change.
        _profile_id   bigint;
        _user_profile record;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        FOR _profile_id IN SELECT id FROM profile where owner_id is null
            LOOP
                EXECUTE format(
                        'SELECT up.user_id as user_id, ue.created_by as username FROM user_profiles up inner join user_entity ue on up.user_id = ue.id where up.profile_id = $1  ORDER BY up.created_at limit 1;'
                    ) USING _profile_id INTO _user_profile;

        -- Update created_by and owner_id of "profile" table
        UPDATE profile SET created_by = _user_profile.username, owner_id = _user_profile.user_id WHERE owner_id is null and id = _profile_id;
        END LOOP;
    END
$$ LANGUAGE plpgsql;


