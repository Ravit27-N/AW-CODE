DO
$$BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

    UPDATE setting_instruction
    SET sub_channel = 'SMS'
    WHERE sub_channel = 'Sms';
end;
$$