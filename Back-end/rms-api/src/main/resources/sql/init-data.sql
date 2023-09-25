--- University ---
INSERT INTO public.university (id,created_at,created_by,last_modified_by,updated_at,address,name) VALUES
    (1, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','ITC'),
    (2, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','RUPP'),
    (3, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','IFL'),
    (4, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','SETEC University'),
    (5, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP, '','PSE'),
    (6, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','Norton University'),
    (7, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','BBU'),
    (8, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'','NPIC'),
    (9, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP, '','CMU'),
    (10, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP, '','RULE')
on conflict do nothing;

--- Mail Template ---

insert
into
    public.mail_template (id, active, body, deleted, is_deletable, subject)
values (1, true, '<div>Reset Password - Recruitment Management System</div><div><br></div><div>Please open the link below in the web browser to reset your password. ${reset_link}.</div><div><br></div><div><br></div><div><br></div><div>Thank You,</div><div><strong style="color: rgb(51, 51, 153);">ALLWEB RMS</strong></div><div><span style="color: rgb(51, 51, 153);">Development Department</span></div><div><span style="color: rgb(51, 51, 153);">------------------------------------------------</span></div><div><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><img src="http://www.allweb.com.kh/storage/app/media/email-signature/ALLWEB-IT-Company.jpg" alt="ALLWEB" height="70" width="85"></a></div><div><em style="color: rgb(51, 51, 153);">ALLWEB Co., Ltd.</em></div><div><span style="color: rgb(51, 51, 153);">Kim Hap Building, N°203, Mao Tse Tong Blvd (St. 245), Phnom Penh, CAMBODIA</span></div><div><span style="color: rgb(51, 51, 153);">Tel.: +855 (0)23 221 320 - Fax : +855 (0)23 221 319 - Mobile : +855 (0)92 424 485</span></div><div><span style="color: rgb(51, 51, 153);">Web:&nbsp;</span><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><em>http://www.allweb.com.kh</em></a></div><div><span style="color: rgb(51, 51, 153);">Map:&nbsp;</span><a href="https://bit.ly/2PQlNO7" rel="noopener noreferrer" target="_blank" style="color: blue;"><em><u>Click Here</u></em></a></div>', false, false, 'Default Mail: Mail password recovery link'),
       (2, true, '<div>Dear,</div><div><br></div><div>It is a special reminder from ALLWEB Recruitment Management System.</div><div><br></div><div><br></div><div><br></div><div>Thank You,</div><div><strong style="color: rgb(51, 51, 153);">ALLWEB Recruiter</strong></div><div><span style="color: rgb(51, 51, 153);">Human Resource Department</span></div><div><span style="color: rgb(51, 51, 153);">------------------------------------------------</span></div><div><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><img src="http://www.allweb.com.kh/storage/app/media/email-signature/ALLWEB-IT-Company.jpg" alt="ALLWEB" height="70" width="85"></a></div><div><em style="color: rgb(51, 51, 153);">ALLWEB Co., Ltd.</em></div><div><span style="color: rgb(51, 51, 153);">Kim Hap Building, N°203, Mao Tse Tong Blvd (St. 245), Phnom Penh, CAMBODIA</span></div><div><span style="color: rgb(51, 51, 153);">Tel.: +855 (0)23 221 320 - Fax : +855 (0)23 221 319 - Mobile : +855 (0)12 713 500</span></div><div><span style="color: rgb(51, 51, 153);">Web:&nbsp;</span><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><em>http://www.allweb.com.kh</em></a></div><div><span style="color: rgb(51, 51, 153);">Map:&nbsp;</span><a href="https://bit.ly/2PQlNO7" rel="noopener noreferrer" target="_blank" style="color: blue;"><em><u>Click Here</u></em></a></div>', false, false, 'Default Mail: Reminder Special'),
       (3, true, '<div>Dear Recruiter, </div><div><br></div><div>Reminder: You have an reminder Title: ${title} Date: ${date_reminder} </div><div>Description: ${description}</div><div><br></div><div><br></div><div><br></div><div>Thank You,</div><div><strong style="color: rgb(51, 51, 153);">ALLWEB Recruiter</strong></div><div><span style="color: rgb(51, 51, 153);">Human Resource Department</span></div><div><span style="color: rgb(51, 51, 153);">------------------------------------------------</span></div><div><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><img src="http://www.allweb.com.kh/storage/app/media/email-signature/ALLWEB-IT-Company.jpg" alt="ALLWEB" height="70" width="85"></a></div><div><em style="color: rgb(51, 51, 153);">ALLWEB Co., Ltd.</em></div><div><span style="color: rgb(51, 51, 153);">Kim Hap Building, N°203, Mao Tse Tong Blvd (St. 245), Phnom Penh, CAMBODIA</span></div><div><span style="color: rgb(51, 51, 153);">Tel.: +855 (0)23 221 320 - Fax : +855 (0)23 221 319 - Mobile : +855 (0)12 713 500</span></div><div><span style="color: rgb(51, 51, 153);">Web:&nbsp;</span><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><em>http://www.allweb.com.kh</em></a></div><div><span style="color: rgb(51, 51, 153);">Map:&nbsp;</span><a href="https://bit.ly/2PQlNO7" rel="noopener noreferrer" target="_blank" style="color: blue;"><em><u>Click Here</u></em></a></div>', false, false, 'Default Mail: Reminder Normal'),
       (4, true, '<div>Dear Recruiter, </div><div><br></div><div>You have an upcoming interviewing schedule with the candidate ${candidate_name} </div><div>Applied for: ${interview_title} </div><div>Time: ${date_interview} </div><div>Location: ALLWEB Co., Ltd. </div><div>Name: ${candidate_name} ${candidate_link}</div><div><br></div><div><br></div><div><br></div><div>Thank You,</div><div><strong style="color: rgb(51, 51, 153);">ALLWEB RMS</strong></div><div><span style="color: rgb(51, 51, 153);">System Development Department</span></div><div><span style="color: rgb(51, 51, 153);">------------------------------------------------</span></div><div><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><img src="http://www.allweb.com.kh/storage/app/media/email-signature/ALLWEB-IT-Company.jpg" alt="ALLWEB" height="70" width="85"></a></div><div><em style="color: rgb(51, 51, 153);">ALLWEB Co., Ltd.</em></div><div><span style="color: rgb(51, 51, 153);">Kim Hap Building, N°203, Mao Tse Tong Blvd (St. 245), Phnom Penh, CAMBODIA</span></div><div><span style="color: rgb(51, 51, 153);">Tel.: +855 (0)23 221 320 - Fax : +855 (0)23 221 319 - Mobile : +855 (0)12 713 500</span></div><div><span style="color: rgb(51, 51, 153);">Web:&nbsp;</span><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><em>http://www.allweb.com.kh</em></a></div><div><span style="color: rgb(51, 51, 153);">Map:&nbsp;</span><a href="https://bit.ly/2PQlNO7" rel="noopener noreferrer" target="_blank" style="color: blue;"><em><u>Click Here</u></em></a></div>', false, false, 'Default Mail: Reminder interview'),
       (5, true, '<div>Dear ${candidate_name},</div><div><br></div><div>Congratulation, your application had been selected for interview on ${date_interview} at ALLWEB Co,. Ltd.</div><div>Do not forget to take this chance.</div><div><br></div><div>Tell: +855(0) 23 221 320</div><div>Address: Kim Hap Building, #203, Mao Tse Tong Blvd, Phnom Penh, CAMBODIA.</div><div>Map: <a href="https://goo.gl/maps/U9TBdsSKbbCnEiyN8" rel="noopener noreferrer" target="_blank">https://goo.gl/maps/U9TBdsSKbbCnEiyN8</a></div><div><br></div><div><br></div><div><br></div><div>Thank You,</div><div><strong style="color: rgb(51, 51, 153);">ALLWEB Recruiter</strong></div><div><span style="color: rgb(51, 51, 153);">Human Resource Department</span></div><div><span style="color: rgb(51, 51, 153);">------------------------------------------------</span></div><div><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><img src="http://www.allweb.com.kh/storage/app/media/email-signature/ALLWEB-IT-Company.jpg" alt="ALLWEB" height="70" width="85"></a></div><div><em style="color: rgb(51, 51, 153);">ALLWEB Co., Ltd.</em></div><div><span style="color: rgb(51, 51, 153);">Kim Hap Building, N°203, Mao Tse Tong Blvd (St. 245), Phnom Penh, CAMBODIA</span></div><div><span style="color: rgb(51, 51, 153);">Tel.: +855 (0)23 221 320 - Fax : +855 (0)23 221 319 - Mobile : +855 (0)12 713 500</span></div><div><span style="color: rgb(51, 51, 153);">Web:&nbsp;</span><a href="http://www.allweb.com.kh/" rel="noopener noreferrer" target="_blank" style="color: blue;"><em>http://www.allweb.com.kh</em></a></div><div><span style="color: rgb(51, 51, 153);">Map:&nbsp;</span><a href="https://bit.ly/2PQlNO7" rel="noopener noreferrer" target="_blank" class="ql-size-small" style="color: blue;"><em><u>Click Here</u></em></a></div>', false, false, 'Default Mail: Send invitation to candidate for Interview')
on conflict (id) do nothing;

--- Company Profile
INSERT INTO public.company_profile (id,created_at,created_by,last_modified_by,updated_at,address,description,email,telephone,title,website) VALUES
       (1, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,'Headquarter: Kim Hap, Mao Tse Toung Blvd, Sangkat Tuol Svay Prey 2,Khan Chamkarmon, Phnom Penh – CAMBODIA.','<p class="ql-align-justify">   ALLWEB Co., Ltd. has been created in 2004 and has built since then a strong European-Asian economic gateway to manage several local and international clients’ needs, from specific development to software integration and IT support.</p>','allweb.rms@allweb.com.kh','023 221 320','ALLWEB Co., Ltd.','www.allweb.com.kh')
on conflict do nothing;
--- Interview Status ---
INSERT INTO public.interview_status (id, created_at, created_by, last_modified_by, updated_at, is_active, "name")
VALUES (1, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'In Processing'),
       (2, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'Following Up'),
       (4, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'Failed'),
       (6, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'Contacting for Interview'),
       (5, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'New Request'),
       (3, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, 'Passed')
on conflict do nothing;
--- Job Description ---
INSERT INTO public.job_description (id, created_at, created_by, last_modified_by, updated_at, active, description, filename, title)
VALUES (1, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'PHP Developer'),
       (2, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Software Tester'),
       (4, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Recruitment Specialist'),
       (5, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Software Tester - Intern'),
       (3, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'PHP Developer - Intern'),
       (6, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Java Developer - Intern'),
       (7, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Java Developer'),
       (8, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Senior Java Developer'),
       (9, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Senior Tester'),
       (10, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '', '', 'Senior PHP Developer'),
       (11, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, '<p>TEST</p>', '', 'TEST')
on conflict do nothing;
--- Candidate Status ---
INSERT INTO public.candidate_status (id,created_at,created_by,last_modified_by,updated_at,active,description,is_deletable,is_deleted,title) VALUES
    (1, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'<p>New Requested Candidate</p>',true,false,'New Request'),
    (3, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'<p>Passed</p>',true,false,'Passed'),
    (4, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'<p>Failed</p>',true,false,'Failed'),
    (2, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'<p>Following up</p>',true,false,'Following Up'),
    (5, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'<p>In Progress</p>',true,false,'In Progress')
on conflict do nothing;
--- Mail Configuration ---
INSERT INTO public.mail_configuration (id, created_at, created_by, last_modified_by, updated_at, active, deleted, "from", title, candidate_status_id, mail_template_id)
VALUES (1, CURRENT_TIMESTAMP, '', '', CURRENT_TIMESTAMP, true, false, 'allweb.recruitment@allweb.com.kh', 'ALLWEB Recruitment Management System', 1, 3)
on conflict do nothing;

INSERT INTO public.mail_configuration_to (mail_configuration_candidate_status_id,mail_configuration_id,mail_configuration_mail_template_id,"to")
VALUES (1,1,3,'allweb.recruitment@allweb.com.kh')
on conflict do nothing;

--- System Configuration ---
INSERT INTO public.system_configuration (id,created_at,created_by,last_modified_by,updated_at,active,config_key,config_value,description) VALUES
(1, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.gmail.host','smtp.gmail.com','This option requires you to authenticate with your Gmail or Google Workspace account and passwords'),
(2, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.gmail.protocol','smtp','This is the protocol used for sending e-mail over the Internet.'),
(4, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.template.id','','A reusable HTML file that is used to build email campaigns. ... An email template is an HTML file'),
(5, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.gmail.password','password','Is a string of characters used for authenticating a user on a computer system'),
(9, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'path.base','localhost:8080','Application address.'),
(10, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'path.candidate','/candidate/candidateDetail','Application address.'),
(14, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'dashboard.gpa','3.0','Grade Point Average, is a number that indicates how well or how high you scored in your courses on average.'),
(6, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.provider','SENDGRID','Option:
1. GMAIL
2. SENDGRID    required to add API key in system.config.mail.sendgrid.api.key'),
(7, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.sendgrid.api.key','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','An application programming interface key (API key) is a unique identifier used to authenticate a use'),
(11, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'path.interview','/interview/view','Application address.'),
(13, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'datetime.format','dd/MMM/yy hh:mm a','Day/Month/Year (21/Mar/21)'),
(8, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.gmail.username','allweb.itcompany@gmail.com',': a sequence of characters that identifies a user when logging onto a computer or website. called also user ID.'),
(12, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.sender','ALLWEB Recruitment System <allweb.recruitment@allweb.com.kh>','who sent it'),
(3, CURRENT_TIMESTAMP,'','', CURRENT_TIMESTAMP,true,'mail.gmail.port','587','port is a communication endpoint')on conflict (id) do nothing;

--- Reminder ---
INSERT INTO reminder_type (id, mail_template_id) VALUES ('NORMAL', 3), ('SPECIAL', 2), ('INTERVIEW', 4) on conflict (id) do nothing;

--- Module ---
INSERT INTO module (id, name, active, description,created_by, created_at, updated_at, last_modified_by)
VALUES
    (1, 'Activity', true, 'Activity module', 'system', localtimestamp, localtimestamp, 'system'),
    (2, 'Candidate', true, 'Candidate module', 'system', localtimestamp, localtimestamp, 'system'),
    (3, 'Company Profile', true, 'Company profile module', 'system', localtimestamp, localtimestamp, 'system'),
    (4, 'Interview', true, 'Interview module', 'system', localtimestamp, localtimestamp, 'system'),
    (5, 'Job Description', true, 'Interview module', 'system', localtimestamp, localtimestamp, 'system'),
    (6, 'Mail Configuration', true, 'Mail configuration module', 'system', localtimestamp, localtimestamp, 'system'),
    (7, 'Mail Template', true, 'Mail template module', 'system', localtimestamp, localtimestamp, 'system'),
    (8, 'Reminder', true, 'Reminder module', 'system', localtimestamp, localtimestamp, 'system'),
    (9, 'Result', true, 'Result module', 'system', localtimestamp, localtimestamp, 'system'),
    (10, 'Status Candidate', true, 'Status module', 'system', localtimestamp, localtimestamp, 'system'),
    (11, 'System Configuration', true, 'System configuration module', 'system', localtimestamp, localtimestamp, 'system'),
    (12, 'User', true, 'User module', 'system', localtimestamp, localtimestamp, 'system')
on conflict (id) do nothing;
--- Module Role---
insert into user_role_detail (module_id, user_role_id, is_insert_able, is_delete_able, is_edit_able, is_view_able)
values (1, 'RMS', true, true, true, true),
       (2, 'RMS', true, true, true, true),
       (3, 'RMS', true, true, true, true),
       (4, 'RMS', true, true, true, true),
       (5, 'RMS', true, true, true, true),
       (6, 'RMS', true, true, true, true),
       (7, 'RMS', true, true, true, true),
       (8, 'RMS', true, true, true, true),
       (9, 'RMS', true, true, true, true),
       (10, 'RMS', true, true, true, true),
       (11, 'RMS', true, true, true, true),
       (12, 'RMS', true, true, true, true)
on conflict (module_id, user_role_id) do nothing;