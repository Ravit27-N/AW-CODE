DO
$$
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM
            pg_catalog.set_config('search_path', 'schema_xxxx', false);

--         Update functionality key of statistic_report
        UPDATE functionality
        SET key = 'cxm_statistic_report'
        WHERE key = 'statistic_and_report_area';

--         Remove unused privilege keys
        UPDATE profile_details pd
        SET privileges = sub_query.privileges
        FROM (
                 SELECT id, array_to_json(array_agg(elem)) AS privileges
                 FROM profile_details pd2
                    , json_array_elements(pd2.privileges::json) elem
                 WHERE (elem ->> 'key' IN ('cxm_statistic_report_generate_statistic',
                                           'cxm_statistic_report_download_statistic'))
                   AND pd2.functionality_key = 'cxm_statistic_report'
                 GROUP BY 1
             ) AS sub_query
        WHERE functionality_key = 'cxm_statistic_report'
          AND pd.id = sub_query.id;
    end
$$