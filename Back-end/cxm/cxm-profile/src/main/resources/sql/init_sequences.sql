-- Please change 'schema_xxxx' to your specific schema.
SELECT pg_catalog.set_config('search_path', 'schema_xxxx', false);

-- client_allow_unloading_sequence definition

-- DROP SEQUENCE client_allow_unloading_sequence;

CREATE SEQUENCE IF NOT EXISTS client_allow_unloading_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- client_functionalities_details_sequence definition

-- DROP SEQUENCE client_functionalities_details_sequence;

CREATE SEQUENCE IF NOT EXISTS client_functionalities_details_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- client_functionality_sequence definition

-- DROP SEQUENCE client_functionality_sequence;

CREATE SEQUENCE IF NOT EXISTS client_functionality_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- client_sequence definition

-- DROP SEQUENCE client_sequence;

CREATE SEQUENCE IF NOT EXISTS client_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- client_unloading_sequence definition

-- DROP SEQUENCE client_unloading_sequence;

CREATE SEQUENCE IF NOT EXISTS client_unloading_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- division_sequence definition

-- DROP SEQUENCE division_sequence;

CREATE SEQUENCE IF NOT EXISTS division_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- functionalities_sequence_sequence definition

-- DROP SEQUENCE functionalities_sequence_sequence;

CREATE SEQUENCE IF NOT EXISTS functionalities_sequence_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- profile_detail_sequence definition

-- DROP SEQUENCE profile_detail_sequence;

CREATE SEQUENCE IF NOT EXISTS profile_detail_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- profile_sequence definition

-- DROP SEQUENCE profile_sequence;

CREATE SEQUENCE IF NOT EXISTS profile_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- public_holiday_details_sequence definition

-- DROP SEQUENCE public_holiday_details_sequence;

CREATE SEQUENCE IF NOT EXISTS public_holiday_details_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- public_holiday_sequence definition

-- DROP SEQUENCE public_holiday_sequence;

CREATE SEQUENCE IF NOT EXISTS public_holiday_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- service_sequence definition

-- DROP SEQUENCE service_sequence;

CREATE SEQUENCE IF NOT EXISTS service_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- user_hub_sequence definition

-- DROP SEQUENCE user_hub_sequence;

CREATE SEQUENCE IF NOT EXISTS user_hub_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- user_request_reset_password_sequence definition

-- DROP SEQUENCE user_request_reset_password_sequence;

CREATE SEQUENCE IF NOT EXISTS user_request_reset_password_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;
