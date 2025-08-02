package com.library.app.repository;

import com.library.app.entity.Book;
import com.library.app.entity.BookLoan;
import com.library.app.entity.Borrower;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLoanRepository extends JpaRepository<BookLoan, UUID> {

  boolean existsByBookAndBorrowerAndReturnedAtIsNull(Book book, Borrower borrower);

  boolean existsByBookAndReturnedAtIsNull(Book book);

  Optional<BookLoan> findByBookAndBorrowerAndReturnedAtIsNull(Book book, Borrower borrower);
}
