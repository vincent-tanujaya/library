package com.library.app.service.book;

import com.library.app.dto.book.AddBookRequestDTO;
import com.library.app.dto.book.BookDTO;
import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.entity.Book;
import com.library.app.entity.Borrower;
import com.library.app.entity.MasterBook;
import com.library.app.exception.BorrowerAlreadyExistException;
import com.library.app.repository.BookRepository;
import com.library.app.repository.BorrowerRepository;
import com.library.app.repository.MasterBookRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddBookService {

  private final BookRepository bookRepository;
  private final MasterBookRepository masterBookRepository;

  public BookDTO addBook(AddBookRequestDTO requestDTO) {
    final MasterBook masterBook = this.fetchOrCreateMasterBook(requestDTO);

    final Book book = Book.builder()
        .masterBook(masterBook)
        .build();

    this.bookRepository.save(book);

    return BookDTO.builder()
        .id(book.getId())
        .author(book.getMasterBook().getAuthor())
        .isbn(book.getMasterBook().getIsbn())
        .title(book.getMasterBook().getTitle())
        .build();
  }

  private MasterBook fetchOrCreateMasterBook(AddBookRequestDTO requestDTO) {
    final Optional<MasterBook> masterBook = this.masterBookRepository.findByIsbnAndAuthorAndTitle(
        requestDTO.getIsbn(),
        requestDTO.getAuthor(),
        requestDTO.getTitle()
    );

    if (masterBook.isPresent()) {
      return masterBook.get();
    } else {
      final MasterBook newMasterBook = MasterBook.builder()
          .author(requestDTO.getAuthor())
          .isbn(requestDTO.getIsbn())
          .title(requestDTO.getTitle())
          .build();

      return this.masterBookRepository.save(newMasterBook);
    }
  }
}
