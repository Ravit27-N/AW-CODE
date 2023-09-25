DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM
pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- copy created_at value from flow_traceability set to created_at of flow_traceability_report if it null
update flow_traceability_report as ftr
set created_at = coalesce((select coalesce(min(ft.created_at), min(fh.created_at))
                  from flow_traceability as ft
                           join flow_history fh on ft.id = fh.flow_traceability_id
                  where ft.id = ftr.id), ftr.deposit_date)
where ftr.created_at is null;

-- copy created_at value from flow_document set to created_at of flow_document_report if it null
update flow_document_report as fdr
set created_at = coalesce((select coalesce(min(fd.created_at), min(fdh.created_at)) as created_at
                  from flow_document as fd
                           join flow_document_history fdh on fd.id = fdh.flow_document_id
                  where fdr.id = fd.id), fdr.date_status)
where fdr.created_at is null;

-- copy created_at value from flow_document_history set to created_at of flow_document_report_history if it null
update flow_document_report_history as fdrh
set created_at = coalesce((select fdh.created_at
                  from flow_document_history as fdh
                  where fdrh.id = fdh.id), fdrh.date_status)
where fdrh.created_at is null;


--     Apply not null constraints to columns created_at
alter table flow_traceability_report
    alter column created_at set not null;
alter table flow_document_report
    alter column created_at set not null;
alter table flow_document_report_history
    alter column created_at set not null;
end
$$