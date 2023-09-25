DO
$$
    declare
        item text;
        capitalizeStatus
             text;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        UPDATE flow_traceability_report
        SET sub_channel = 'SMS'
        WHERE sub_channel = 'Sms';

        FOR item in
            SELECT *
            FROM unnest(string_to_array('In Process,To Validate,In Error,In Processing',
                                        ','))
            LOOP
                capitalizeStatus := (SELECT upper(substring(item from 1 for 1)) ||
                                            lower(substring(item from 2)));
                UPDATE flow_document_report
                SET status = capitalizeStatus
                WHERE status = item;
                RAISE
                    NOTICE 'Status: %', capitalizeStatus;
            END LOOP;
    END
$$;