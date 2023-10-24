INSERT INTO user_accesses (id,name) VALUES (1,'Ses dossiers uniquement') ON DUPLICATE KEY UPDATE id = id;
INSERT INTO user_accesses (id,name) VALUES (2,'Dossiers de son service') ON DUPLICATE KEY UPDATE id = id;
INSERT INTO user_accesses (id,name) VALUES (3,'L’ensemble des dossiers') ON DUPLICATE KEY UPDATE id = id;