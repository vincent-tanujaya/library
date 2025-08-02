CREATE TABLE master_books
(
    id         UUID PRIMARY KEY,
    isbn       VARCHAR(20)  NOT NULL UNIQUE,
    title      VARCHAR(255) NOT NULL,
    author     VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE books
(
    id             UUID PRIMARY KEY,
    master_book_id UUID      NOT NULL REFERENCES master_books (id),
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    deleted_at     TIMESTAMPTZ
);

CREATE INDEX idx_books_master_book_id
    ON books (master_book_id);

CREATE TABLE borrowers
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE book_loans
(
    id          UUID PRIMARY KEY,
    book_id     UUID      NOT NULL REFERENCES books (id),
    borrower_id UUID      NOT NULL REFERENCES borrowers (id),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    returned_at TIMESTAMPTZ
);

CREATE INDEX idx_book_loans_active
    ON book_loans (book_id) WHERE returned_at IS NULL;

CREATE INDEX idx_book_loans_borrower
    ON book_loans (borrower_id, returned_at);

CREATE INDEX idx_book_loans_by_book
    ON book_loans (book_id, returned_at);

CREATE UNIQUE INDEX ux_book_loans_active
    ON book_loans(book_id)
    WHERE returned_at IS NULL;