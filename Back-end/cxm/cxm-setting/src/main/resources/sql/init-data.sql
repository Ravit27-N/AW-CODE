
-- Please change 'schema_xxxx' to your specific schema.
SELECT pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- Setting --
INSERT INTO setting (id, created_at, created_by, last_modified, last_modified_by, connector,
                            customer, deposit_type, extension, flow_type, id_creator,
                            scan_activation)
VALUES (1, now(), 'super-admin', null, null, 'C1', 'ENI', 'Batch', 'zip',
        'ENI/Batch/C1/zip', 'XXX', true),
       (2, now(), 'super-admin', null, null, 'C2', 'ENI', 'Batch', 'zip',
        'ENI/Batch/C2/zip', 'XXX', true),
       (3, now(), 'super-admin', null, null, null, 'GreenYellow', 'Batch',
        'zip', 'GreenYellow/Batch/zip', 'XXX', true),
       (4, now(), 'super-admin', null, null, null, 'ENI', 'Portal',
        'pdf', 'ENI/Portal/pdf', 'XXX', true)
on conflict do nothing;


-- Setting Instructions --
INSERT INTO setting_instruction (id, address, breaking_page, channel, code_template, data,
                                        email_object, email_recipient, flow_name, id_breaking_page,
                                        id_email_object, id_email_recipient, id_recipient_id,
                                        id_template, model_type, others, pjs, recipient_id,
                                        sub_channel,
                                        template, setting_id)
VALUES (1, '18 to 21', null, 'Digital', 'MOD001', '26 to 27', 'Column 7', 'Column 6',
        'Flow_X56BL1', '/XXX/', null, null, null, 'C6CHORUM1', 'MOD001', null, '17 and 11 to 14',
        null, 'Email', 'MODEL_NAME', 1)
on conflict do nothing;

-- Portal Setting
INSERT INTO portal_setting (setting_id, created_at, created_by, last_modified,
                                   last_modified_by, config_path, "section")
VALUES (4, now(), 'super-admin', null, null, 'C:\\logidoc\\Go2PDF\\config\\config.ini', 'PORENILIBRE')
on conflict do nothing;
