-- Run this script to remove the wrong data of "user_profiles" (assigned profile to user that cross client).
-- After finishing this script, you can run "#114665_3_view_wrong_data_that_assigned_profile_cross_client.sql" script again to validate the result.
DO
$$
    declare
        _client_id bigint;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Step 4: Delete the wrong data from "user_profiles" that based on the collection of the user ids and collection of the profile ids.
        FOR _client_id IN SELECT id FROM client
            LOOP
                DELETE FROM user_profiles WHERE user_id IN (SELECT * FROM get_user_ids(_client_id)) AND profile_id NOT IN (SELECT * FROM get_profile_ids(_client_id));
            END LOOP;
    END
$$ LANGUAGE plpgsql;

