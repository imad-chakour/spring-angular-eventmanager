-- User Service Data
-- Insert Users
INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'admin@eventflow.com', 'System', 'Administrator', '$2a$10$adminEncodedPassword', 'ADMIN', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'marketing.manager@eventflow.com', 'Sarah', 'Johnson', '$2a$10$marketingEncodedPass', 'MARKETING_MANAGER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'marketing.user@eventflow.com', 'Mike', 'Chen', '$2a$10$marketingUserPass', 'MARKETING_USER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'event.organizer@eventflow.com', 'Emma', 'Davis', '$2a$10$eventOrgPass', 'MARKETING_USER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

-- Commit the transaction
COMMIT;