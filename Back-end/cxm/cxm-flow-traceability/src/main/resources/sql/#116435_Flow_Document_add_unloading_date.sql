-- SQL script: Update unloading_date for flow_document
DO
$$
BEGIN

        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Update set unloading_date to flow_document
        -- where status 'in progress'
        UPDATE flow_document fd
        SET unloading_date = (SELECT max(fdh.date_time)
                              FROM flow_document_history fdh
                              WHERE fd.id = fdh.flow_document_id
                                AND lower(fdh.event) = 'in progress')
        where channel = 'Postal' AND unloading_date IS NULL;

end;
$$ LANGUAGE plpgsql;