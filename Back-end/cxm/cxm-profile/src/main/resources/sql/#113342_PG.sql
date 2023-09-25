-- Find deleted user base on the created_by of Profile
    SELECT DISTINCT pro.created_by AS deleted_user FROM profile pro LEFT JOIN user_entity ue ON pro.created_by = ue.username WHERE ue.username IS NULL;

DO
$$
    DECLARE
        -- Define the id of the desired default user
    default_user_id BIGINT := 0;
    BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Drop column "service_id" in table "profile"
        ALTER TABLE profile
            DROP COLUMN IF EXISTS service_id;

        -- Add column "owner_id" on table "profile"
        ALTER TABLE profile
            ADD COLUMN owner_id BIGINT DEFAULT 0;


        -- Populate "owner_id" in table "profile" by using "id" from table "user_entity"
        -- If created_by is null or not found in table "user_entity", set the owner_id to 0
        UPDATE profile
        SET owner_id = COALESCE( (SELECT u.id
                        FROM user_entity u
                        WHERE u.username = profile.created_by
                        LIMIT 1), default_user_id)
        WHERE owner_id = 0;

        -- Remove default value from the field owner_id from the table "profile"
        ALTER TABLE profile ALTER COLUMN owner_id DROP DEFAULT;

    end;
$$ LANGUAGE plpgsql;
