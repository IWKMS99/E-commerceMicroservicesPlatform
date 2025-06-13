ALTER TABLE user_management.users
    ADD COLUMN role VARCHAR(50);

UPDATE user_management.users SET role = 'USER' WHERE role IS NULL;

ALTER TABLE user_management.users
    ALTER COLUMN role SET NOT NULL;