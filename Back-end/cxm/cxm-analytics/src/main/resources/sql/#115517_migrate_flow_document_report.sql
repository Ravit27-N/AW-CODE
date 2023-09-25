DO
$$
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM
            pg_catalog.set_config('search_path', 'schema_xxxx', false);

--         Update records of flow_document_report by data from flow
        UPDATE flow_document_report fdp
        SET total_page = sub_query.page_number,
            id_doc      = sub_query.id_doc,
            recipient   = sub_query.recipient,
            num_reco    = sub_query.num_reco
        FROM (
                 SELECT fd.id, fd.page_number, fd.id_doc, fd.recipient, fdn.num_reco
                 FROM flow_document fd
                          inner join flow_document_details fdd
                                     on fd.id = fdd.id
                          left join flow_document_notification fdn
                                    on fd.id = fdn.document_id
             )
                 AS sub_query
        WHERE fdp.id = sub_query.id;

--         Update value of columns(filter1, filler2, filler3, filler4, filler5)
        UPDATE flow_document_report fdp
        SET filler1 = sub_query.filler1,
            filler2 = sub_query.filler2,
            filler3 = sub_query.filler3,
            filler4 = sub_query.filler4,
            filler5 = sub_query.filler5
        FROM (
                 SELECT fd.id,
                       CASE WHEN fillers[1] = '' THEN NULL ELSE fillers[1] END as filler1,
                        CASE WHEN fillers[2] = '' THEN NULL ELSE fillers[2] END as filler2,
                        CASE WHEN fillers[3] = '' THEN NULL ELSE fillers[3] END as filler3,
                        CASE WHEN fillers[4] = '' THEN NULL ELSE fillers[4] END as filler4,
                        CASE WHEN fillers[5] = '' THEN NULL ELSE fillers[5] END as filler5
                 FROM flow_document fd
                          inner join flow_document_details fdd
                                     on fd.id = fdd.id
             )
                 AS sub_query
        WHERE fdp.id = sub_query.id;

--         Update date_reception of flow_document_report
        UPDATE flow_document_report fdp
        SET date_reception = sub_query.date_time
        FROM (
                 SELECT fd.id, fdh.date_time
                 FROM flow_document fd
                          inner join flow_document_history fdh
                                     on fd.id = fdh.flow_document_id
                 WHERE fdh.event = 'In progress'
             )
                 AS sub_query
        WHERE fdp.id = sub_query.id;

--         Update date_sending of flow_document_report
        UPDATE flow_document_report fdp
        SET date_sending = sub_query.date_time
        FROM (
                 SELECT fd.id, fdh.date_time
                 FROM flow_traceability ft
                          INNER JOIN flow_document fd on ft.id = fd.flow_traceability_id
                          INNER JOIN flow_document_history fdh on fd.id = fdh.flow_document_id
                 WHERE fdh.event = (CASE
                                        WHEN ft.channel = 'Digital' THEN 'Sent'
                                        WHEN ft.channel = 'Postal' THEN 'Stamped' END)
             )
                 AS sub_query
        WHERE fdp.id = sub_query.id;
    end
$$