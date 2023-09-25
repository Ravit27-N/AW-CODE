-- Set default value for field "model_name" of Flow traceability of "channel" "postal"

DO
$$
    BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    -- Set default model of old Postal FlowTraceability data
    UPDATE flow_traceability SET model_name = 'Not_defined' where CHANNEL = 'Postal' and model_name ISNULL;

    end
$$ LANGUAGE plpgsql;
