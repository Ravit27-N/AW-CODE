DO
$$
    BEGIN

    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    ALTER TABLE flow_document
    ADD COLUMN IF NOT EXISTS export_status VARCHAR(150) DEFAULT 'TO_EXPORT' NOT NULL;

    end
$$ LANGUAGE plpgsql;