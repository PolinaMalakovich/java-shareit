INSERT INTO users (user_id, name, email)
VALUES (1, 'Alice', 'alice.anderson@example.com');

INSERT INTO users (user_id, name, email)
VALUES (2, 'John', 'john.doe@example.com');

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Coffee table', 'Old wooden coffee table', true, 1, null);

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (5, 'Tuxedo', 'Notch lapel modern fit tuxedo, black.', true, 2, null);

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (4, '2023-10-13 16:47:00', '2023-10-15 16:47:00', 3, 2, 'WAITING');

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (7, '2022-10-13 16:47:00', '2022-12-13 16:47:00', 5, 1, 'WAITING');

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (6, '2021-10-13 16:47:00', '2021-10-15 16:47:00', 5, 1, 'WAITING');
