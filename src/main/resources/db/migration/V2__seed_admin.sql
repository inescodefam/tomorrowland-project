INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@tomorrowland.com',
 '$2b$12$Hj/7qeIuC0euo1MMKpLsxuxVZe1ADSazUue9TL5LTuZMAucFnF586',
 'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;
