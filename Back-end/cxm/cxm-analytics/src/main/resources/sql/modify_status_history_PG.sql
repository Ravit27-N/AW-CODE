DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM
pg_catalog.set_config('search_path', 'schema_xxxx', false);
-- update column status on flow_document_report_history if null
update flow_document_report_history as fdrh
set status = (select fdh.event
              from flow_document_history as fdh
              where fdrh.id = fdh.id)
where fdrh.status is null;

alter table flow_document_report_history
    alter column status set not null;

end
$$;