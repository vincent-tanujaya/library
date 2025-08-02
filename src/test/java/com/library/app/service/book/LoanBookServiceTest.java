package com.library.app.service.book;

import com.library.app.entity.Book;
import com.library.app.entity.BookLoan;
import com.library.app.entity.Borrower;
import com.library.app.exception.BookStillOnLoanException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BookLoanRepository;
import com.library.app.repository.BookRepository;
import com.library.app.repository.BorrowerRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class LoanBookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BorrowerRepository borrowerRepository;

  @Mock
  private BookLoanRepository bookLoanRepository;

  @InjectMocks
  private LoanBookService loanBookService;

  private UUID bookId;
  private UUID borrowerId;
  private Book bookEntity;
  private Borrower borrowerEntity;

  @BeforeMethod
  public void setUp() {
    this.loanBookService = null;
    MockitoAnnotations.openMocks(this);

    bookId = UUID.fromString("111e4567-e89b-12d3-a456-426614174000");
    borrowerId = UUID.fromString("222e4567-e89b-12d3-a456-426614174001");
    bookEntity = Book.builder().id(bookId).build();
    borrowerEntity = Borrower.builder().id(borrowerId).build();
  }

  @Test
  public void testLoanBook_Success() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrowerEntity));
    when(bookLoanRepository.existsByBookAndBorrowerAndReturnedAtIsNull(bookEntity, borrowerEntity))
        .thenReturn(false);
    when(bookLoanRepository.save(any(BookLoan.class))).thenAnswer(invocation -> invocation.getArgument(0));

    loanBookService.loanBook(bookId, borrowerId);

    ArgumentCaptor<BookLoan> captor = ArgumentCaptor.forClass(BookLoan.class);
    verify(bookLoanRepository).save(captor.capture());
    BookLoan savedLoan = captor.getValue();

    assertEquals(savedLoan.getBook(), bookEntity);
    assertEquals(savedLoan.getBorrower(), borrowerEntity);
    assertNull(savedLoan.getReturnedAt(), "returnedAt should be null on loan");
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testLoanBook_WhenBookNotFound_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
    loanBookService.loanBook(bookId, borrowerId);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testLoanBook_WhenBorrowerNotFound_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());
    loanBookService.loanBook(bookId, borrowerId);
  }

  @Test(expectedExceptions = BookStillOnLoanException.class)
  public void testLoanBook_WhenAlreadyLoaned_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrowerEntity));
    when(bookLoanRepository.existsByBookAndBorrowerAndReturnedAtIsNull(bookEntity, borrowerEntity))
        .thenReturn(true);
    loanBookService.loanBook(bookId, borrowerId);
  }

  @Test
  public void testReturnBook_Success() {
    BookLoan existingLoan = BookLoan.builder()
        .book(bookEntity)
        .borrower(borrowerEntity)
        .build();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrowerEntity));
    when(bookLoanRepository.findByBookAndBorrowerAndReturnedAtIsNull(bookEntity, borrowerEntity))
        .thenReturn(Optional.of(existingLoan));
    when(bookLoanRepository.save(any(BookLoan.class))).thenAnswer(invocation -> invocation.getArgument(0));

    loanBookService.returnBook(bookId, borrowerId);

    ArgumentCaptor<BookLoan> captor = ArgumentCaptor.forClass(BookLoan.class);
    verify(bookLoanRepository).save(captor.capture());
    BookLoan updatedLoan = captor.getValue();

    assertEquals(updatedLoan.getBook(), bookEntity);
    assertEquals(updatedLoan.getBorrower(), borrowerEntity);
    assertNotNull(updatedLoan.getReturnedAt(), "returnedAt should be set on return");
    assertTrue(updatedLoan.getReturnedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testReturnBook_WhenBookNotFound_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
    loanBookService.returnBook(bookId, borrowerId);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testReturnBook_WhenBorrowerNotFound_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());
    loanBookService.returnBook(bookId, borrowerId);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testReturnBook_WhenLoanNotFound_ShouldThrow() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrowerEntity));
    when(bookLoanRepository.findByBookAndBorrowerAndReturnedAtIsNull(bookEntity, borrowerEntity))
        .thenReturn(Optional.empty());
    loanBookService.returnBook(bookId, borrowerId);
  }
}
