DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        ALTER TABLE resource_library DROP CONSTRAINT IF EXISTS ukfkr4kjr4aolodcjacexnhd6gb;
END
$$ LANGUAGE plpgsql;
