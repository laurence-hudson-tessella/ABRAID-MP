-- Script: testdata.sql
--
-- Description: Adds test data to the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

\copy expert (name, email, hashed_password, is_administrator) FROM 'expert.txt' (ENCODING utf8)
\copy location (id, name, geom, precision, country_id, geonames_id, geonames_feature_code) FROM 'location.txt' (ENCODING utf8)
\copy alert (id, feed_id, title, publication_date, url, summary, healthmap_alert_id) FROM 'alert.txt' (ENCODING utf8)
\copy disease_occurrence (id, disease_group_id, location_id, alert_id, occurrence_start_date) FROM 'diseaseoccurrence.txt' (ENCODING utf8)
\copy expert_disease_group (expert_id, disease_group_id) FROM 'expert_disease_group.txt' (ENCODING utf8)

-- Some of the data above contains explicit values of serial primary keys, so that child tables can refer
-- to known IDs. So now we need to reset the sequences of such primary keys.
\pset footer off
\echo Resetting sequences after creating data:
\echo
SELECT setval('location_id_seq', (SELECT MAX(id) FROM location)) max_location_id;
SELECT setval('alert_id_seq', (SELECT MAX(id) FROM alert)) max_alert_id;
SELECT setval('disease_occurrence_id_seq', (SELECT MAX(id) FROM disease_occurrence)) max_disease_occurrence_id;

VACUUM ANALYZE;
