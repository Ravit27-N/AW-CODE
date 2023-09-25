DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Rename column "type" to "source" (for store resourceFileType One_Time_Upload Or Library).
        ALTER TABLE IF EXISTS background_file RENAME COLUMN type TO source;
        -- Add column "type" for store resource type Attachment or Background.
        ALTER TABLE IF EXISTS background_file ADD type varchar(255);
        -- Update old data set "type" is Background
        Update background_file set type='Background' where type is NULL ;

        -- Remove unused columns
        ALTER TABLE background_file DROP COLUMN created_by;
        ALTER TABLE background_file DROP COLUMN last_modified_by;

        -- Rename table "background_file" to "resource_file".
        ALTER TABLE background_file RENAME TO resource_file;
        -- Rename sequence "background_file_sequence" to "resource_file_sequence".
        ALTER SEQUENCE background_file_sequence RENAME TO resource_file_sequence;

END
$$ LANGUAGE plpgsql;