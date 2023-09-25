DO
$$
    BEGIN
-- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'database_schema_xxxx', false);

-- Step 1: Create a sequence for use with column "id" of table "user_entity"
    CREATE SEQUENCE IF NOT EXISTS user_entity_sequence
        INCREMENT BY 1
        MINVALUE 1
        MAXVALUE 2147483647
        START 1
        CACHE 1
        NO CYCLE;

-- step 2: renaming column id to technical_ref;
        ALTER TABLE if exists user_entity
            RENAME COLUMN id TO technical_ref;
        ALTER TABLE if exists user_entity
            RENAME COLUMN deleted TO is_active;
        ALTER TABLE if exists user_entity
            ALTER COLUMN is_active SET DEFAULT true;

-- step 3: remove exists user_profiles constraint key(note CONSTRAINT foreign keys will create automatically in runtime);
        ALTER TABLE IF EXISTS user_profiles
            DROP CONSTRAINT fk1000rudncghbgrpmnccl8eyw7,
            DROP CONSTRAINT FKnulwiyoh90ir6a3vgnermb060,
            DROP CONSTRAINT user_profiles_pkey;

-- step 4: Rename existing constraint of table "user_hub" in case it exists
        ALTER TABLE user_hub
            DROP CONSTRAINT IF EXISTS fk4s9vqpu33iqrdds8pymfat2dg;

-- step 5: remove exist constraint primary key;
        ALTER TABLE if exists user_entity
            DROP constraint user_entity_pkey;

-- step 6: create new column id with default sequence generator name 'user_entity_id_seq';
        ALTER TABLE if EXISTS user_entity
            ADD COLUMN id BIGINT PRIMARY KEY DEFAULT nextval('user_entity_sequence');

-- step 7: create tmp columns for store tmp data;
        ALTER TABLE IF EXISTS user_profiles
            ADD COLUMN technical_ref varchar(255),
            ADD COLUMN profile_ref   bigint;

-- step 9: transform exists data from profile_id & user_id to the new tmp columns (technical_ref, profile_ref);
        UPDATE user_profiles
        set technical_ref = user_id,
            profile_ref   = profile_id
        where created_by is not null;

-- step 9: remove exists columns (profile_id, user_id);
        ALTER TABLE IF EXISTS user_profiles
            drop column profile_id,
            drop column user_id;

-- step 10: recreate exists columns (profile_id, user_id) again with new type bigint;
        ALTER TABLE IF EXISTS user_profiles
            ADD COLUMN user_id    bigint,
            ADD COLUMN profile_id bigint;

-- step 12: transform data from tmp columns(technical_ref, profile_ref) to the new columns(user_id, profile_id);
        UPDATE user_profiles
        SET user_id    = (SELECT ue.id FROM user_entity ue WHERE ue.technical_ref = user_profiles.technical_ref),
            profile_id = profile_ref
        WHERE created_by IS NOT NULL;

        -- As the field "is_active" is renamed from the field "deleted"
        UPDATE user_entity
        SET is_active = NOT is_active
            -- Add where clause to just disable SQL warning
            -- as the field technical_ref are always has value
        WHERE technical_ref notnull;

-- step 12: recreate primary key again because it not auto automatically in runtime
        ALTER TABLE user_profiles
            ADD PRIMARY KEY (user_id, profile_id);

-- step 13: remove tmp columns(technical_ref, profile_ref)
        ALTER TABLE IF EXISTS user_profiles
            DROP COLUMN technical_ref,
            DROP COLUMN profile_ref;
-- step 14: Remove default value of column "id" of table "user_entity"
        ALTER TABLE user_entity ALTER COLUMN id DROP DEFAULT;

    end
$$ LANGUAGE plpgsql;
