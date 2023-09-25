DO
$$
BEGIN
    -- Please change 'schema_xxxx' to your specific schema.
    PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);
    UPDATE flow_document_report AS fdr SET sub_channel = (SELECT sub_channel FROM flow_document AS fd WHERE fd.id = fdr.id);
end
$$
LANGUAGE plpgsql;