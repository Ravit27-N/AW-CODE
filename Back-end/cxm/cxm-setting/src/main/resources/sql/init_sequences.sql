
-- Please change 'schema_xxxx' to your specific schema.
SELECT pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- setting_instruction_sequence definition

-- DROP SEQUENCE setting_instruction_sequence;

CREATE SEQUENCE IF NOT EXISTS setting_instruction_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- setting_sequence definition

-- DROP SEQUENCE setting_sequence;

CREATE SEQUENCE IF NOT EXISTS setting_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;
