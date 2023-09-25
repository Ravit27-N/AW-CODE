DO
$$
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Drop column "is_active" from "profile" table.
        ALTER TABLE IF EXISTS profile DROP COLUMN IF EXISTS is_active;
    END
$$ LANGUAGE plpgsql;