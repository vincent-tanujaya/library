# Library Management API

A simple library management system built with Java 17 and Spring Boot.

## Contents

* [Introduction](#introduction)
* [Prerequisites](#prerequisites)
* [Setup](#setup)
* [Configuration](#configuration)
* [Database](#database)
* [Running the Application](#running-the-application)
* [API Documentation](#api-documentation)
* [Assumptions](#assumptions)
* [License](#license)

## Introduction

This project provides a RESTful API for managing books, borrowers, and loans in a library. It enforces constraints such as unique ISBN copies and ensures only one active loan per book.

## Prerequisites

Before you begin, make sure you have the following installed on your system:

* **Java 17**

  ```bash
  java -version
  ```
* **Maven 3.6+**

  ```bash
  mvn -v
  ```
* **PostgreSQL 13+**

    * **macOS (Homebrew):**

      ```bash
      brew install postgresql@13
      brew services start postgresql@13
      ```
    * **Ubuntu/Debian:**

      ```bash
      sudo apt update
      sudo apt install postgresql postgresql-contrib
      sudo systemctl start postgresql
      sudo systemctl enable postgresql
      ```
    * **Windows:** Download and install from the official site: [https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/)

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/vincent-tanujaya/library.git
   cd library
   ```
2. Create a `.properties` file in the project root (to provide database credentials). See [Configuration](#configuration).

## Configuration

Create a `src/main/resources/application.properties` file from `src/main/resources/application.properties/example` and configure database credentials:

## Database

This project uses PostgreSQL as its database because:

ACID Compliance: Ensures reliable and consistent transactions when borrowing and returning books.

MVCC (Multi-Version Concurrency Control): Allows high concurrency without locking, so multiple users can read/write concurrently with minimal contention.

Advanced Indexing & Query Optimization: Supports B-tree, GIN, GiST indexes to speed up lookups (e.g., finding available copies) and complex queries.

Rich Data Types & Constraints: Built-in support for JSON, arrays, and strong validation through constraints (UNIQUE, NOT NULL), helping maintain data integrity.

Open-Source & Ecosystem: A mature community with many extensions (PostGIS, pg_trgm), tooling, and frequent updates.

## Running the Application

Start the application with:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/` by default.

## API Documentation

All endpoints are prefixed with `/api/v1` and return JSON unless noted otherwise.

### Base URL

```
http://localhost:8080/api/v1
```

### Create Borrower

* **Endpoint**: `POST /borrowers`
* **Description**: Register a new library member.
* **Request Body**:

  ```json
  {
    "name": "Vincent",
    "email": "vincent.tanujaya@gmail.com"
  }
  ```
* **Response**: `200 OK` with the created borrower’s UUID in the body.

  ```json
  "943520f8-4ea1-48de-8fd4-6c4add4524c9"
  ```

### Update Borrower

* **Endpoint**: `PUT /borrowers/{borrowerId}`
* **Description**: Update an existing borrower’s information.
* **Path Parameters**:

    * `borrowerId` (UUID) – e.g. `943520f8-4ea1-48de-8fd4-6c4add4524c9`
* **Request Body**:

  ```json
  {
    "name": "Vincent",
    "email": "vincent.tanujaya@gmail.com"
  }
  ```
* **Response**: `200 OK` with the borrower’s UUID in the body.

  ```json
  "943520f8-4ea1-48de-8fd4-6c4add4524c9"
  ```

### Add Book

* **Endpoint**: `POST /books`
* **Description**: Add a new book copy to the library.
* **Request Body**:

  ```json
  {
    "isbn": "978123",
    "title": "Effective Java",
    "author": "Joshua Bloch"
  }
  ```
* **Response**: `200 OK` with the new book’s UUID in the body.

  ```json
  "064713cb-9288-4855-9550-ccae6429c665"
  ```

### Loan Book

* **Endpoint**: `POST /books/{bookId}/loan`
* **Description**: Loan an available book to a borrower.
* **Path Parameters**:

    * `bookId` (UUID)
* **Query Parameters**:

    * `borrowerId` (UUID)
* **Response**: `200 OK` with the created loan’s UUID in the body.

  ```json
  "b14e4822-d019-4516-9f1d-f65ea29172fd"
  ```

### Return Book

* **Endpoint**: `POST /books/{bookId}/return`
* **Description**: Mark a loan as returned (sets `returnedAt`).
* **Path Parameters**:

    * `bookId` (UUID)
* **Query Parameters**:

    * `borrowerId` (UUID)
* **Response**: `204 No Content`

### Delete Book

* **Endpoint**: `DELETE /books/{bookId}`
* **Description**: Remove a book copy from the system.
* **Path Parameters**:

    * `bookId` (UUID)
* **Response**: `204 No Content`

## Assumptions

* ISBN uniquely identifies a title/author combination, but each physical copy is a separate record.
* A loan is active until its `returnedAt` timestamp is set.
* Timestamps are managed by the application; no database defaults or triggers.

## Enforcing Single Active Loan (Database Level)

The following **partial unique index** enforces at the database layer that only one active loan can exist per book:

```sql
CREATE UNIQUE INDEX ux_book_loans_active
  ON book_loans(book_id)
  WHERE returned_at IS NULL;
```
Explanation: This index applies only to rows where returned_at is NULL. If an attempt is made to insert a second loan record for the same book_id without a returned_at timestamp, PostgreSQL will reject it with a uniqueness violation. This ensures that, regardless of concurrent requests or application logic, the database itself never allows more than one active loan per book.

## License

MIT © Your Name
