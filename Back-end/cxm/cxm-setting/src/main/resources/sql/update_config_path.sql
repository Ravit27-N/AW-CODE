DO
$$
    DECLARE
    -- Base path that storing the client configurations
    -- Must end with '/'
    base_path VARCHAR := '/apps/cxm/common/config/';
    BEGIN
		-- Please change 'schema_xxxx' to your specific schema. For example: public.
		PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);


        UPDATE portal_setting ps SET config_path = (SELECT concat( base_path, replace(lower(s.customer),' ', '_'), '/config.ini') FROM setting s WHERE s.id = ps.setting_id);

    END
$$ LANGUAGE plpgsql;