CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(64) NOT NULL CHECK (name <> ''),
    email   VARCHAR(64) NOT NULL UNIQUE CHECK (email <> '')
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(256)                      NOT NULL,
    requester_id BIGINT REFERENCES users (user_id) NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE       NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(64)                       NOT NULL CHECK (name <> ''),
    description  VARCHAR(256),
    is_available BOOLEAN,
    owner_id     BIGINT REFERENCES users (user_id) NOT NULL,
    request_id   BIGINT REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE       NOT NULL CHECK (start_date < end_date),
    end_date   TIMESTAMP WITHOUT TIME ZONE       NOT NULL CHECK (end_date > start_date),
    item_id    BIGINT REFERENCES items (item_id) NOT NULL,
    booker_id  BIGINT REFERENCES users (user_id) NOT NULL,
    status     VARCHAR                           NOT NULL CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text       VARCHAR(256)                NOT NULL CHECK (text <> ''),
    item_id    BIGINT REFERENCES items (item_id),
    author_id  BIGINT REFERENCES users (user_id),
    created    TIMESTAMP WITHOUT TIME ZONE NOT NULL
);