package com.library.app.service.book;

import com.library.app.entity.Book;
import com.library.app.entity.BookLoan;
import com.library.app.entity.Borrower;
import com.library.app.exception.BookStillOnLoanException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BookLoanRepository;
import com.library.app.repository.BookRepository;
import com.library.app.repository.BorrowerRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanBookService {

  private final BookRepository bookRepository;
  private final BorrowerRepository borrowerRepository;

  private final BookLoanRepository bookLoanRepository;

  public void loanBook(
      UUID bookId,
      UUID borrowerId
  ) {
    final Book book = this.bookRepository.findById(bookId)
        .orElseThrow(DataNotFoundException::new);
    final Borrower borrower = this.borrowerRepository.findById(borrowerId)
        .orElseThrow(DataNotFoundException::new);

    if (this.bookLoanRepository.existsByBookAndBorrowerAndReturnedAtIsNull(book, borrower)) {
      throw new BookStillOnLoanException();
    }

    final BookLoan bookLoan = BookLoan.builder()
        .book(book)
        .borrower(borrower)
        .build();

    this.bookLoanRepository.save(bookLoan);
  }

  public void returnBook(
      UUID bookId,
      UUID borrowerId
  ) {
    final Book book = this.bookRepository.findById(bookId)
        .orElseThrow(DataNotFoundException::new);
    final Borrower borrower = this.borrowerRepository.findById(borrowerId)
        .orElseThrow(DataNotFoundException::new);

    final BookLoan bookLoan = this.bookLoanRepository
        .findByBookAndBorrowerAndReturnedAtIsNull(
            book,
            borrower
        )
        .orElseThrow(DataNotFoundException::new);

    bookLoan.setReturnedAt(LocalDateTime.now());

    this.bookLoanRepository.save(bookLoan);
  }
}
