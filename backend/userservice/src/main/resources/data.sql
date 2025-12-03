-- User Service Data
-- Insert Users with PROPERLY HASHED passwords
INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'admin@eventflow.com', 'System', 'Administrator', '$2a$10$qpEkOhddOvR57kBW1StWn.Y8Mci.6D1Eme51ek5iEE0wB.jCxVlV.', 'ADMIN', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'marketing.manager@eventflow.com', 'Sarah', 'Johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Gc3ZJwGZcB6Zt5Xp7v8Wz5E6JQ1q/O', 'MARKETING_MANAGER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'marketing.user@eventflow.com', 'Mike', 'Chen', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Gc3ZJwGZcB6Zt5Xp7v8Wz5E6JQ1q/O', 'MARKETING_USER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'event.organizer@eventflow.com', 'Emma', 'Davis', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Gc3ZJwGZcB6Zt5Xp7v8Wz5E6JQ1q/O', 'MARKETING_USER', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);

-- FIXED: Now using the same BCrypt hash for "admin"
INSERT INTO users (id, email, first_name, last_name, password, role, status, last_login, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'chakourimad01@gmail.com', 'Imad', 'Chakour', '$2a$10$qpEkOhddOvR57kBW1StWn.Y8Mci.6D1Eme51ek5iEE0wB.jCxVlV.', 'ADMIN', 'ACTIVE', NULL, SYSTIMESTAMP, SYSTIMESTAMP);