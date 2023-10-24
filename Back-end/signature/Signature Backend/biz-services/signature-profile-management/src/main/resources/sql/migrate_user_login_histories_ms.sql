DELIMITER
$$
CREATE PROCEDURE `login_histories_migration`()
BEGIN
    DECLARE
_rollback BOOL DEFAULT 0;
    DECLARE
CONTINUE HANDLER FOR SQLEXCEPTION SET _rollback = 1;
START TRANSACTION;

INSERT INTO login_histories (created_at, login_email, user_id)
SELECT created_at, logged_email, user_id
FROM admin_login;

DROP TABLE admin_login;

IF
_rollback THEN
SELECT 'Errors, Rolling back' AS MESSAGE;
SHOW
ERRORS;
ROLLBACK;
ELSE
        COMMIT;
END IF;
END$$
DELIMITER ;
CALL `login_histories_migration`;
DROP PROCEDURE `login_histories_migration`;
