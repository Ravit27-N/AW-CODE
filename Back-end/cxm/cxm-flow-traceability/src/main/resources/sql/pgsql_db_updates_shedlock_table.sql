DO
$$
    BEGIN

    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    DROP TABLE IF EXISTS "shedlock";
    CREATE TABLE IF NOT EXISTS "shedlock" (
        name VARCHAR(255) NOT NULL, 
        lock_until TIMESTAMP NOT NULL,
        locked_at TIMESTAMP NOT NULL, 
        locked_by VARCHAR(255) NOT NULL, 
        PRIMARY KEY (name)
    );

    end
$$ LANGUAGE plpgsql;