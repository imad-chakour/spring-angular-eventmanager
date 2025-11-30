-- Participant Service Data
-- Insert Participants
INSERT INTO participants (id, email, first_name, last_name, phone, company, job_title, status, registration_date, last_activity, opt_in_marketing)
VALUES (participants_seq.NEXTVAL, 'john.doe@techcorp.com', 'John', 'Doe', '+1-555-0101', 'TechCorp Inc.', 'Software Engineer', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

INSERT INTO participants (id, email, first_name, last_name, phone, company, job_title, status, registration_date, last_activity, opt_in_marketing)
VALUES (participants_seq.NEXTVAL, 'maria.garcia@innovate.com', 'Maria', 'Garcia', '+1-555-0102', 'Innovate Solutions', 'Product Manager', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

INSERT INTO participants (id, email, first_name, last_name, phone, company, job_title, status, registration_date, last_activity, opt_in_marketing)
VALUES (participants_seq.NEXTVAL, 'alex.wang@futuretech.com', 'Alex', 'Wang', '+1-555-0103', 'FutureTech', 'Data Scientist', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

INSERT INTO participants (id, email, first_name, last_name, phone, company, job_title, status, registration_date, last_activity, opt_in_marketing)
VALUES (participants_seq.NEXTVAL, 'sophie.martin@digital.com', 'Sophie', 'Martin', '+1-555-0104', 'Digital Ventures', 'Marketing Director', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

INSERT INTO participants (id, email, first_name, last_name, phone, company, job_title, status, registration_date, last_activity, opt_in_marketing)
VALUES (participants_seq.NEXTVAL, 'david.smith@cloudsys.com', 'David', 'Smith', '+1-555-0105', 'Cloud Systems', 'CTO', 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP, 0);

-- Insert Participant Segments
INSERT INTO participant_segments (participant_id, segment_name) VALUES (1, 'TECHNOLOGY');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (1, 'ENGINEERING');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (2, 'PRODUCT_MANAGEMENT');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (2, 'LEADERSHIP');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (3, 'DATA_SCIENCE');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (3, 'AI_ML');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (4, 'MARKETING');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (4, 'EXECUTIVE');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (5, 'EXECUTIVE');
INSERT INTO participant_segments (participant_id, segment_name) VALUES (5, 'TECHNOLOGY');

-- Insert Participant Preferences
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (1, 'EMAIL');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (1, 'NEWSLETTER');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (2, 'EMAIL');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (3, 'SMS');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (4, 'EMAIL');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (4, 'PUSH_NOTIFICATIONS');
INSERT INTO participant_preferences (participant_id, preference_value) VALUES (5, 'EMAIL');

-- Commit the transaction
COMMIT;