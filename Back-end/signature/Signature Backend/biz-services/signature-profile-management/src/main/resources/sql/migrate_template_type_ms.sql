DELIMITER $$
CREATE PROCEDURE `user_template_migrate`()
BEGIN
    DECLARE _rollback BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET _rollback = 1;
START TRANSACTION;
-- According to SIG-835 Create template with predefine participants
-- We added one column name `type`.  By default template created with
alter table templates
    add column step int not null;
alter table templates
    add column type varchar(255) not null;

UPDATE templates
SET `type` = "DEFAULT",
    `step` = 1
WHERE id > 0;

UPDATE templates
SET `recipient` = 0
WHERE id > 0 and recipient IS NULL;

UPDATE templates
SET `viewer` = 0
WHERE id > 0 and viewer IS NULL;

IF _rollback THEN
SELECT 'Errors, Rolling back' AS MESSAGE;
SHOW ERRORS;
ROLLBACK;
ELSE
        COMMIT;
END IF;
END$$
DELIMITER ;
CALL `user_template_migrate`;
DROP PROCEDURE `user_template_migrate`;