package com.library.app.service.book;

import com.library.app.dto.book.AddBookRequestDTO;
import com.library.app.dto.book.BookDTO;
import com.library.app.entity.Book;
import com.library.app.entity.MasterBook;
import com.library.app.exception.BookStillOnLoanException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BookLoanRepository;
import com.library.app.repository.BookRepository;
import com.library.app.repository.MasterBookRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteBookService {

  private final BookRepository bookRepository;
  private final BookLoanRepository bookLoanRepository;

  public void deleteBook(UUID bookId) {
    final Book book = this.bookRepository.findById(bookId)
        .orElseThrow(DataNotFoundException::new);

    if (this.bookLoanRepository.existsByBookAndReturnedAtIsNull(book)){
      throw new BookStillOnLoanException();
    }

    book.setDeletedAt(LocalDateTime.now());

    this.bookRepository.save(book);
  }
}
