INSERT INTO member (member_id, username, password, name, email, activated, created_time, oauth_provider, oauth_provider_id, picture, sns_url, website_url, introduction, history, artist_status, updated_time) VALUES (0x1BE444176FCD41FE8AD77507D1350FCA, 'admin', '$2a$10$.rfADTPSqpCpD5vF7F7zoefe1v7JjtOym/k0uVa2rudXMqm/fzk06', 'Admin', 'admin@artscope.kr', 1, '2023-04-04 22:14:49', null, null, null, null, null, null, null, 'NONE', null);

INSERT INTO member_authority (member_id, authority_name) VALUES (0x1BE444176FCD41FE8AD77507D1350FCA, 'ROLE_USER');
INSERT INTO member_authority (member_id, authority_name) VALUES (0x1BE444176FCD41FE8AD77507D1350FCA, 'ROLE_ADMIN');
