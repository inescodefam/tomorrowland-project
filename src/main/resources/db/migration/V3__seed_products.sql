INSERT INTO categories (name, description) VALUES
('Weekend Passes', 'Full festival weekend access'),
('Day Tickets', 'Single day access');

INSERT INTO products (name, description, price, stock, version, category_id) VALUES
('Full Madness Pass Weekend 1', '3-day ticket camping included', 299.99, 50, 0,
 (SELECT id FROM categories WHERE name = 'Weekend Passes')),
('Full Madness Pass Weekend 2', '3-day ticket camping included', 299.99, 50, 0,
 (SELECT id FROM categories WHERE name = 'Weekend Passes')),
('Day Ticket Friday', 'Friday only access', 89.99, 200, 0,
 (SELECT id FROM categories WHERE name = 'Day Tickets')),
('Day Ticket Saturday', 'Saturday only access', 89.99, 150, 0,
 (SELECT id FROM categories WHERE name = 'Day Tickets'));
