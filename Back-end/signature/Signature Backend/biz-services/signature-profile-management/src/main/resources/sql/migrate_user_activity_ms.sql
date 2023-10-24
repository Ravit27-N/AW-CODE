-- Related to SIG-804, we alter one table named “user_activity” to store token for forgetting password, activate user, change mail, password login attempts….

DELIMITER $$
CREATE PROCEDURE `user_activity_migrate`()
BEGIN
    DECLARE _rollback BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET _rollback = 1;
    START TRANSACTION;
    -- After deploy the application. Should run these script for migration.
    INSERT INTO user_activity (token, expire_time, user_id)
    SELECT reset_token, expire_time, id
    FROM users
    WHERE reset_token IS NOT NULL
      AND expire_time > CURRENT_TIMESTAMP;

    -- We moved two columns from users table to “user_activity”. So those columns redundant.
    ALTER TABLE users DROP COLUMN expire_time;
    ALTER TABLE users DROP COLUMN reset_token;

    IF _rollback THEN
        SELECT 'Errors, Rolling back' AS MESSAGE;
        SHOW ERRORS;
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$
DELIMITER ;
CALL `user_activity_migrate`;
DROP PROCEDURE `user_activity_migrate`;