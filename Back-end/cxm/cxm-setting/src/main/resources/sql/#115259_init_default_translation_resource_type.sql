DO
$$
BEGIN
        -- Please change 'schema_xxxx' to your specific schema.
        PERFORM pg_catalog.set_config('search_path', 'schema_xxxx', false);

        -- Insert default translate to English, French
        insert into resource_type_translate (id, "key", "translate", "language")
        values (1, 'Attachment', 'Attachment','en'),
               (2, 'Attachment', 'Pièce jointe','fr'),
               (3, 'Background', 'Background','en'),
               (4, 'Background', 'Fond de page','fr'),
               (5, 'Signature', 'Signature','en'),
               (6, 'Signature', 'Signature','fr');

END
$$ LANGUAGE plpgsql;