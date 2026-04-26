INSERT INTO categories (name, description) VALUES
('Weekend Passes', 'Full festival weekend access'),
('Day Tickets', 'Single day access');

WITH category_names AS (
    SELECT 'Weekend Passes' AS weekend_passes, 'Day Tickets' AS day_tickets
)
INSERT INTO products (name, description, price, stock, version, category_id) VALUES
('Full Madness Pass Weekend 1', '3-day ticket camping included', 299.99, 50, 0,
 (SELECT c.id FROM categories c, category_names n WHERE c.name = n.weekend_passes)),
('Full Madness Pass Weekend 2', '3-day ticket camping included', 299.99, 50, 0,
 (SELECT c.id FROM categories c, category_names n WHERE c.name = n.weekend_passes)),
('Day Ticket Friday', 'Friday only access', 89.99, 200, 0,
 (SELECT c.id FROM categories c, category_names n WHERE c.name = n.day_tickets)),
('Day Ticket Saturday', 'Saturday only access', 89.99, 150, 0,
 (SELECT c.id FROM categories c, category_names n WHERE c.name = n.day_tickets));
