DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Insert data to flow_document_report_history
        INSERT into flow_document_report_history
        (id, created_at, modified_at, date_status, status, flow_document_report_id)
        select fdh.id, fd.created_at, fd.last_modified, fd.date_status, fdh.event, fdr.id
        from flow_document_history as fdh
                 join flow_document fd on fd.id = fdh.flow_document_id
                 join flow_document_report fdr on fd.id = fdr.id On conflict do nothing;

end
$$