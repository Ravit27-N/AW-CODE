-- Run this script to view the wrong data of "user_profiles" table that cross client.
DO
$$
    DECLARE
        -- no need to change.
        _client_id bigint;
        _user_profile record;
        _is_data_found bool := false;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Step 3: (Optional) Run this script to view the wrong data that will delete.
        FOR _client_id IN SELECT id FROM client order by id
            LOOP
                EXECUTE format(
                    'SELECT array_agg(up.user_id) as user_ids, array_agg(up.profile_id) as profile_ids FROM user_profiles up
                    WHERE up.user_id in (select * from get_user_ids($1)) AND up.profile_id not in (SELECT * FROM get_profile_ids($1));'
                    ) USING _client_id INTO _user_profile;

                IF(_user_profile.user_ids IS NOT NULL) THEN
                    _is_data_found = true;
                    RAISE NOTICE 'Client ID: %', _client_id;
                    RAISE NOTICE 'Value of wrong data:  { userIds: %, profileIds: % }', _user_profile.user_ids, _user_profile.profile_ids;
                END IF;
            END LOOP;

        IF(_is_data_found is false) THEN
            RAISE NOTICE 'No wrong data found!';
        END IF;
    END
$$ LANGUAGE plpgsql;