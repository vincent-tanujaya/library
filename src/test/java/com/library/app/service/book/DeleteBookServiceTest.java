package com.library.app.service.book;

import com.library.app.entity.Book;
import com.library.app.exception.BookStillOnLoanException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BookLoanRepository;
import com.library.app.repository.BookRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class DeleteBookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BookLoanRepository bookLoanRepository;

  @InjectMocks
  private DeleteBookService deleteBookService;

  private UUID bookId;
  private Book bookEntity;

  @BeforeMethod
  public void setUp() {
    this.deleteBookService = null;

    MockitoAnnotations.openMocks(this);
    bookId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    bookEntity = Book.builder()
        .id(bookId)
        .build();
  }

  @Test
  public void testDeleteBook_Success() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(bookLoanRepository.existsByBookAndReturnedAtIsNull(bookEntity)).thenReturn(false);

    deleteBookService.deleteBook(bookId);

    ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
    verify(bookRepository).save(bookCaptor.capture());

    Book saved = bookCaptor.getValue();
    assertEquals(saved.getId(), bookId, "Saved book ID should match");
    assertNotNull(saved.getDeletedAt(), "deletedAt should be set");
    assertTrue(saved.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
        "deletedAt should be recent");
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testDeleteBook_WhenNotFound_ShouldThrowDataNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    deleteBookService.deleteBook(bookId);
  }

  @Test(expectedExceptions = BookStillOnLoanException.class)
  public void testDeleteBook_WhenActiveLoanExists_ShouldThrowBookStillOnLoan() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(bookLoanRepository.existsByBookAndReturnedAtIsNull(bookEntity)).thenReturn(true);

    deleteBookService.deleteBook(bookId);
  }
}
