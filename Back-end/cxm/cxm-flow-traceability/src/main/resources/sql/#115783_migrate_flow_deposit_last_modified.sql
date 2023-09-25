-- SQL script: Update last_modified for flow_deposit
DO
$$
BEGIN

        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Update last_modified for flow_deposit base on column flow_traceability date_status
        -- If flow id equal to flow_traceability id set last_modified with flow_traceability date_status and flow_deposit is null.
        UPDATE flow_deposit fd
        SET last_modified = (
            SELECT ft.date_status
            FROM flow_traceability ft
            WHERE fd.id = ft.id
        )
        WHERE fd.last_modified IS NULL;

end;
$$ LANGUAGE plpgsql;
