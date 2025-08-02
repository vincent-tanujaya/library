package com.library.app.service.book;

import com.library.app.dto.book.AddBookRequestDTO;
import com.library.app.dto.book.BookDTO;
import com.library.app.entity.Book;
import com.library.app.entity.MasterBook;
import com.library.app.repository.BookRepository;
import com.library.app.repository.MasterBookRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class AddBookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private MasterBookRepository masterBookRepository;

  @InjectMocks
  private AddBookService addBookService;

  private final UUID existingMbId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
  private final UUID newMbId      = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");
  private final UUID savedBookId  = UUID.fromString("323e4567-e89b-12d3-a456-426614174002");

  @BeforeMethod
  public void setUp() {
    this.addBookService = null;

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddBook_WhenMasterBookExists() {
    AddBookRequestDTO addBookRequestDTO = AddBookRequestDTO.builder()
        .isbn("ISBN-EXIST")
        .author("Existing Author")
        .title("Existing Title")
        .build();

    MasterBook existingMasterBook = MasterBook.builder()
        .id(existingMbId)
        .isbn(addBookRequestDTO.getIsbn())
        .author(addBookRequestDTO.getAuthor())
        .title(addBookRequestDTO.getTitle())
        .build();

    when(masterBookRepository
        .findByIsbnAndAuthorAndTitle(addBookRequestDTO.getIsbn(), addBookRequestDTO.getAuthor(), addBookRequestDTO.getTitle()))
        .thenReturn(Optional.of(existingMasterBook));

    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
      Book book = invocation.getArgument(0);
      book.setId(savedBookId);

      return book;
    });

    BookDTO bookDTO = addBookService.addBook(addBookRequestDTO);

    assertNotNull(bookDTO);
    assertEquals(bookDTO.getId(), savedBookId);
    assertEquals(bookDTO.getIsbn(), addBookRequestDTO.getIsbn());
    assertEquals(bookDTO.getAuthor(), addBookRequestDTO.getAuthor());
    assertEquals(bookDTO.getTitle(), addBookRequestDTO.getTitle());

    verify(masterBookRepository)
        .findByIsbnAndAuthorAndTitle(addBookRequestDTO.getIsbn(), addBookRequestDTO.getAuthor(), addBookRequestDTO.getTitle());
    verify(masterBookRepository, never()).save(any(MasterBook.class));
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  public void testAddBook_WhenMasterBookNotExists() {
    AddBookRequestDTO addBookRequestDTO = AddBookRequestDTO.builder()
        .isbn("ISBN-NEW")
        .author("New Author")
        .title("New Title")
        .build();

    when(masterBookRepository
        .findByIsbnAndAuthorAndTitle(addBookRequestDTO.getIsbn(), addBookRequestDTO.getAuthor(), addBookRequestDTO.getTitle()))
        .thenReturn(Optional.empty());

    when(masterBookRepository.save(any(MasterBook.class))).thenAnswer(invocation -> {
      MasterBook masterBook = invocation.getArgument(0);
      masterBook.setId(newMbId);

      return masterBook;
    });

    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
      Book book = invocation.getArgument(0);
      book.setId(savedBookId);

      return book;
    });

    BookDTO bookDTO = addBookService.addBook(addBookRequestDTO);

    assertNotNull(bookDTO);
    assertEquals(bookDTO.getId(), savedBookId);
    assertEquals(bookDTO.getIsbn(), addBookRequestDTO.getIsbn());
    assertEquals(bookDTO.getAuthor(), addBookRequestDTO.getAuthor());
    assertEquals(bookDTO.getTitle(), addBookRequestDTO.getTitle());

    verify(masterBookRepository)
        .findByIsbnAndAuthorAndTitle(addBookRequestDTO.getIsbn(), addBookRequestDTO.getAuthor(), addBookRequestDTO.getTitle());
    verify(masterBookRepository).save(argThat(mb ->
        mb.getIsbn().equals(addBookRequestDTO.getIsbn()) &&
            mb.getAuthor().equals(addBookRequestDTO.getAuthor()) &&
            mb.getTitle().equals(addBookRequestDTO.getTitle())
    ));
    verify(bookRepository).save(any(Book.class));
  }
}
