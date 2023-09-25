DO
$$
    declare
        item text;
        capitalizeStatus
             text;
    BEGIN
                -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        UPDATE flow_traceability
        SET sub_channel = 'SMS'
        WHERE sub_channel = 'Sms';
        UPDATE flow_document
        SET sub_channel = 'SMS'
        WHERE sub_channel = 'Sms';

        FOR item in
            SELECT *
            FROM unnest(string_to_array('In Process,To Validate,In Error,In Processing',
                                        ','))
            LOOP
                capitalizeStatus := (SELECT upper(substring(item from 1 for 1)) ||
                                            lower(substring(item from 2)));
                UPDATE flow_traceability
                SET status = capitalizeStatus
                WHERE status = item;
                UPDATE flow_history
                SET event = capitalizeStatus
                WHERE event = item;
                UPDATE flow_document
                SET status = capitalizeStatus
                WHERE status = item;
                UPDATE flow_document_history
                SET event = capitalizeStatus
                WHERE event = item;
                RAISE
                    NOTICE 'Status: %', capitalizeStatus;
            END LOOP;
    END
$$;