package com.library.app.controller;

import com.library.app.dto.book.AddBookRequestDTO;
import com.library.app.dto.book.BookDTO;
import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.entity.Book;
import com.library.app.service.book.AddBookService;
import com.library.app.service.book.DeleteBookService;
import com.library.app.service.book.LoanBookService;
import com.library.app.service.borrower.CreateBorrowerService;
import com.library.app.service.borrower.UpdateBorrowerService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

  private final AddBookService addBookService;
  private final DeleteBookService deleteBookService;
  private final LoanBookService loanBookService;

  @PostMapping
  public ResponseEntity<BookDTO> addBook(
    @Valid @RequestBody AddBookRequestDTO requestDTO
  ) {
    final BookDTO responseDTO = this.addBookService
        .addBook(requestDTO);

    return ResponseEntity.ok(responseDTO);
  }

  @DeleteMapping("/{id}")
  public void deleteBook(
      @PathVariable("id") UUID bookId
  ) {
    this.deleteBookService.deleteBook(bookId);
  }

  @PostMapping("/{id}/loan")
  public void loanBook(
      @PathVariable("id") UUID bookId,
      @RequestParam UUID borrowerId
  ) {
    this.loanBookService.loanBook(bookId, borrowerId);
  }

  @PostMapping("/{id}/return")
  public void returnBook(
      @PathVariable("id") UUID bookId,
      @RequestParam UUID borrowerId
  ) {
    this.loanBookService.returnBook(bookId, borrowerId);
  }
}
