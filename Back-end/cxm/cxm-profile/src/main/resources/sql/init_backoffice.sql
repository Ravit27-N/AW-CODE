
-- Please change 'schema_xxxx' to your specific schema.
SELECT pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- Client functionality
INSERT INTO functionality (id, key) VALUES (1, 'cxm_flow_traceability') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (2, 'statistic_and_report_area') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (3, 'cxm_template') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (4, 'cxm_sms_template') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (5, 'cxm_campaign') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (6, 'cxm_sms_campaign') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (7, 'cxm_flow_deposit') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (8, 'cxm_setting_up_document_template') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (9, 'cxm_directory') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (10, 'cxm_communication_interactive') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (11, 'cxm_espace_validation') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (12, 'cxm_client_management') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (13, 'cxm_user_management') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (14, 'cxm_management_library_resource') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (15, 'cxm_enrichment_mailing') on conflict do nothing;
INSERT INTO functionality (id, key) VALUES (16, 'cxm_watermark_enhancement_postal_delivery') on conflict do nothing;
