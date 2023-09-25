-- Set default client for old data

DO
$$
    DECLARE
    -- Define the client id before running the script
    default_client_id BIGINT := 0;
    BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    -- Set default for old profile that has not assigned the client to it
    UPDATE profile SET client_id = default_client_id where client_id isnull;

    end
$$ LANGUAGE plpgsql;
