package com.library.app.controller;

import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.service.borrower.CreateBorrowerService;
import com.library.app.service.borrower.UpdateBorrowerService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

  private final CreateBorrowerService createBorrowerService;
  private final UpdateBorrowerService updateBorrowerService;

  @PostMapping
  public ResponseEntity<BorrowerResponseDTO> createBorrower(
    @Valid @RequestBody UpsertBorrowerRequestDTO requestDTO
  ) {
    final BorrowerResponseDTO responseDTO = this.createBorrowerService
        .createBorrower(requestDTO);

    return ResponseEntity.ok(responseDTO);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BorrowerResponseDTO> updateBorrower(
      @PathVariable("id") UUID borrowerId,
      @Valid @RequestBody UpsertBorrowerRequestDTO requestDTO
  ) {
    final BorrowerResponseDTO responseDTO = this.updateBorrowerService
        .updateBorrower(borrowerId, requestDTO);

    return ResponseEntity.ok(responseDTO);
  }
}
