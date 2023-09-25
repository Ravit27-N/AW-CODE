-- Step 1: Create a function to return collection ids of the user by client id.
CREATE OR REPLACE FUNCTION get_user_ids(_client_id bigint)
    RETURNS TABLE (id bigint)
    LANGUAGE plpgsql AS
$func$
BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    RETURN QUERY SELECT ue.id::bigint FROM user_entity ue INNER JOIN service s ON s.id = ue.service_id INNER JOIN division d ON d.id = s.division_id
                 WHERE ue.is_active = true AND d.client_id = _client_id;
END
$func$;

-- Step 2: Create a function to return collection ids of the profile by client id.
CREATE OR REPLACE FUNCTION get_profile_ids(_client_id bigint)
    RETURNS TABLE (id bigint)
    LANGUAGE plpgsql AS
$func$
BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    RETURN QUERY SELECT p.id::bigint FROM profile p INNER JOIN client c ON c.id = p.client_id WHERE c.id = _client_id;
END
$func$;