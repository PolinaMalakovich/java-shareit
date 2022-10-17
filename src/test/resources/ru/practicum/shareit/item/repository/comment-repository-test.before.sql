INSERT INTO users (user_id, name, email)
VALUES (1, 'Alice', 'alice.anderson@example.com');

INSERT INTO users (user_id, name, email)
VALUES (2, 'John', 'john.doe@example.com');

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Coffee table', 'Old wooden coffee table', true, 1, null);

INSERT INTO comments (comment_id, text, item_id, author_id, created)
VALUES (4, 'Nice!', 3, 2, '2023-10-16 16:47:00');