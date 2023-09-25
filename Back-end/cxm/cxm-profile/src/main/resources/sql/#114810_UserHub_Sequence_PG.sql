DO
$$
DECLARE
last_id integer;
BEGIN

    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    -- Select max id user hub set to variable last_customer_id.
    SELECT MAX(ID) + 1 INTO last_id FROM user_hub;

    -- Alter sequence set Restart with last value customer id
    EXECUTE FORMAT('ALTER SEQUENCE user_hub_sequence RESTART %s ', last_id);

END $$;
