DO
$$
    DECLARE
    -- do not change.
    _privileges record;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Find privilege key of profile details that have visibility or modification is true.
        FOR _privileges IN (
            with visibility as (
                select ('{'||index -1||',visibility}')::TEXT[] as path, id  from profile_details pd, jsonb_array_elements(pd.privileges) with ordinality arr(privilege, index)
                where (privilege ->> 'visibility')::bool = true
            ), modification as (
                select('{'||index -1||',modification}')::TEXT[] as path, id from profile_details pd, jsonb_array_elements(pd.privileges) with ordinality arr(privilege, index)
                where (privilege ->> 'modification')::bool = true
            )
            select * from modification union all select * from visibility
        )
        LOOP
            -- Update profile details that have visibility or modification to false.
            update profile_details pd set privileges = jsonb_set(privileges, _privileges.path, 'false', false);
        END LOOP;
    end
$$ LANGUAGE plpgsql;
