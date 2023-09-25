-- Find deleted user base on the "created_by" of FlowTraceability
    SELECT DISTINCT fl.created_by AS deleted_user FROM flow_traceability fl LEFT JOIN user_entity ue ON fl.created_by = ue.username WHERE ue.username IS NULL;

-- SQL script: Modify the "owner_id" of FlowTraceability
DO
$$
    DECLARE
        -- Define the id of the desired default user
    default_user_id BIGINT := 0;
    BEGIN

        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'database_schema_xxxx', false);

        -- Drop column unnecessary columns from flow_traceability table
        ALTER TABLE flow_traceability
            DROP COLUMN IF EXISTS department,
            DROP COLUMN IF EXISTS file_url;

        -- Drop column unnecessary columns from flow_document table
        ALTER TABLE flow_document
            DROP COLUMN IF EXISTS file_url;

        -- Add column "owner_id" on table "flow_traceability"
        ALTER TABLE flow_traceability
            ADD COLUMN IF NOT EXISTS owner_id BIGINT DEFAULT 0 NOT NULL;


        -- Alter field "deleted" to "is_active" on table "flow_deposit".
        -- update records of "flow_deposit" table before alter field "deleted" to "is_active".
        UPDATE flow_deposit
        SET deleted = NOT deleted;

        -- Alter field "deleted" to "is_active" on table "flow_deposit".
        ALTER TABLE if exists flow_deposit
            RENAME COLUMN deleted TO is_active;

        -- Populate "owner_id" in table "flow_traceability" by using "id" from table "user_entity"
        -- If created_by is null or not found in table "user_entity", set the owner_id to 0
        UPDATE flow_traceability
        SET owner_id = COALESCE( (SELECT u.id
                        FROM user_entity u
                        WHERE u.username = flow_traceability.created_by
                        LIMIT 1), default_user_id)
        WHERE owner_id = 0;

        -- Remove default value from the field owner_id from the table "flow_traceability"
        ALTER TABLE flow_traceability ALTER COLUMN owner_id DROP DEFAULT;

    end;
$$ LANGUAGE plpgsql;
