-- Please change 'schema_xxxx' to your specific schema.
SELECT pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- unloading_scheduler_job_sequence definition

-- DROP SEQUENCE unloading_scheduler_job_sequence;

CREATE SEQUENCE IF NOT EXISTS unloading_scheduler_job_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;
