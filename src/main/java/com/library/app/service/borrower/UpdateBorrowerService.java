package com.library.app.service.borrower;

import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.entity.Borrower;
import com.library.app.exception.BorrowerAlreadyExistException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BorrowerRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBorrowerService {

  private final BorrowerRepository borrowerRepository;

  public BorrowerResponseDTO updateBorrower(
      UUID borrowerId,
      UpsertBorrowerRequestDTO requestDTO
  ) {
    final Optional<Borrower> existingBorrower = this.borrowerRepository
        .findByEmail(requestDTO.getEmail());

    if (existingBorrower.isPresent()) {
      throw new BorrowerAlreadyExistException();
    }

    final Borrower borrower = this.borrowerRepository.findById(borrowerId)
        .orElseThrow(DataNotFoundException::new);

    borrower.setName(requestDTO.getName());
    borrower.setEmail(requestDTO.getEmail());

    this.borrowerRepository.save(borrower);

    return BorrowerResponseDTO.builder()
        .id(borrower.getId())
        .email(borrower.getEmail())
        .name(borrower.getName())
        .build();
  }
}
