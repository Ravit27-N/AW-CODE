DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM
        pg_catalog.set_config('search_path', 'schema_xxxx', false);

--         Update functionality key of statistic_report
            UPDATE profile_details
            SET privileges = (SELECT jsonb_agg(
                                             CASE
                                                 WHEN elem ->>'key' = 'cxm_enrichment_mailing_modify_resource'
                                                     THEN jsonb_set(elem, '{key}',
                                                                    '"cxm_enrichment_mailing_modify_a_custom_resource"')
                                                 WHEN elem ->>'key' = 'cxm_enrichment_mailing_delete_resource'
                                                     THEN jsonb_set(elem, '{key}',
                                                                    '"cxm_enrichment_mailing_delete_a_custom_resource"')
                                                 ELSE elem
                                                 END
                                         )
                              FROM jsonb_array_elements(privileges) AS elem)
            where functionality_key = 'cxm_enrichment_mailing';


end
$$