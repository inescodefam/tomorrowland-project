INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@tomorrowland.com',
 '${seed_admin_bcrypt}',
 'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;
