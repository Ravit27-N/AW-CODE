DO
$$
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
    	PERFORM pg_catalog.set_config('search_path', 'database_schema_xxxx', false);

	    INSERT INTO flow_traceability_report(id, owner_id, created_at, created_by , modified_at, deposit_date, deposit_mode, channel, sub_channel)
	    	SELECT	id, owner_id, created_at, created_by , last_modified AS modified_at, deposit_date, deposit_mode, channel, sub_channel
	        FROM flow_traceability
	    ON CONFLICT DO NOTHING;

    end
$$ LANGUAGE plpgsql;
