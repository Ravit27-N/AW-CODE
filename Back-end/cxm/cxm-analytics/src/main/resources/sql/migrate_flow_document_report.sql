DO
$$
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'database_schema_xxxx', false);

        INSERT INTO flow_document_report(id, flow_id, created_at, modified_at, date_status, status)
            SELECT id, flow_traceability_id, created_at, last_modified AS modified_at, date_status, status
            FROM flow_document
        ON CONFLICT DO NOTHING;

    end
$$ LANGUAGE plpgsql;
