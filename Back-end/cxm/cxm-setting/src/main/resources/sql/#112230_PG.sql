-- Find deleted user base on the "id_creator" of the table "setting"
    SELECT DISTINCT st.id_creator AS deleted_user
    FROM setting st LEFT JOIN user_entity ue ON st.id_creator = ue.technical_ref
    WHERE ue.username IS NULL AND st.id_creator IS NOT NULL AND st.id_creator NOT IN ('XXX', '');

-- SQL script: Modify the "id_creator" of the table setting
DO
$$
    DECLARE
-- Define the id of the desired default user
        default_id_creator BIGINT := 0;
    begin

-- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'database_schema_xxxx', false);

-- step 1: add tmp column to store exist id_creator string;
        ALTER TABLE If EXISTS setting
            ADD COLUMN id_creator_tmp VARCHAR(255);
-- step 2: populate exist data from id_creator column to id_creator_tmp;
        UPDATE setting SET id_creator_tmp = id_creator WHERE id_creator NOTNULL;

-- step 3: remove column id_creator;
        ALTER TABLE IF EXISTS setting DROP COLUMN id_creator;

-- step 4: recreate column id_creator with a new data type;
        ALTER TABLE If EXISTS setting
            ADD COLUMN id_creator BIGINT DEFAULT NUll;

-- step 5: update id_creator data by id_creator_tmp
        UPDATE setting
        SET id_creator = COALESCE( (SELECT ue.id
                          FROM user_entity ue
                          WHERE ue.technical_ref = id_creator_tmp
                            AND ue.is_active = true),
                          default_id_creator)
        WHERE id_creator_tmp NOTNULL AND id_creator_tmp NOT IN ('XXX', '');

-- step 6: remove tmp column id_creator_tmp;
        ALTER TABLE IF EXISTS setting DROP COLUMN id_creator_tmp;

    end
$$ LANGUAGE plpgsql;
