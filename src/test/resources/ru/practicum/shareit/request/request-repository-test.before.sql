INSERT INTO users (user_id, name, email)
VALUES (1, 'Alice', 'alice.anderson@example.com');

INSERT INTO users (user_id, name, email)
VALUES (2, 'John', 'john.doe@example.com');

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (4, 'Vintage coffee table, preferably wooden', 2, '2022-10-12 16:47:00');

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Coffee table', 'Old wooden coffee table', true, 1, 4);

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (6, 'Black tuxedo', 1, '2022-10-10 15:27:00');

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (5, 'Tuxedo', 'Notch lapel modern fit tuxedo, black.', true, 2, 6);
