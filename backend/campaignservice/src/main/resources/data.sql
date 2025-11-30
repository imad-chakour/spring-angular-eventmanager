-- Campaign Service Data
-- Insert Campaigns
INSERT INTO campaigns (id, reference, name, description, start_date, end_date, budget, status, channel, organizer_id, created_at, updated_at)
VALUES (campaigns_seq.NEXTVAL, 'CAMP-Q1WEB', 'Q1 Webinar Series', 'Quarterly webinar series for technology trends', SYSTIMESTAMP, SYSTIMESTAMP + INTERVAL '30' DAY, 5000.00, 'ACTIF', 'EMAIL', 2, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO campaigns (id, reference, name, description, start_date, end_date, budget, status, channel, organizer_id, created_at, updated_at)
VALUES (campaigns_seq.NEXTVAL, 'CAMP-SP24', 'Spring 2024 Product Launch', 'Launch campaign for new product features', SYSTIMESTAMP, SYSTIMESTAMP + INTERVAL '60' DAY, 15000.00, 'ACTIF', 'MULTI_CANAL', 2, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO campaigns (id, reference, name, description, start_date, end_date, budget, status, channel, organizer_id, created_at, updated_at)
VALUES (campaigns_seq.NEXTVAL, 'CAMP-EDU23', 'Educational Content Series', 'Year-end educational content campaign', SYSTIMESTAMP - INTERVAL '10' DAY, SYSTIMESTAMP + INTERVAL '20' DAY, 8000.00, 'ACTIF', 'EMAIL', 3, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO campaigns (id, reference, name, description, start_date, end_date, budget, status, channel, organizer_id, created_at, updated_at)
VALUES (campaigns_seq.NEXTVAL, 'CAMP-RETENT', 'Customer Retention Program', 'Program to increase customer loyalty and retention', SYSTIMESTAMP + INTERVAL '5' DAY, SYSTIMESTAMP + INTERVAL '90' DAY, 12000.00, 'BROUILLON', 'MULTI_CANAL', 3, SYSTIMESTAMP, SYSTIMESTAMP);

-- Insert Campaign Segments
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (1, 'TECHNOLOGY');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (1, 'ENGINEERING');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (2, 'ALL_SEGMENTS');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (3, 'PRODUCT_MANAGEMENT');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (3, 'LEADERSHIP');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (4, 'EXECUTIVE');
INSERT INTO campaign_segments (campaign_id, segment_name) VALUES (4, 'MARKETING');

-- Commit the transaction
COMMIT;