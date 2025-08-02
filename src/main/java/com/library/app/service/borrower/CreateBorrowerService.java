package com.library.app.service.borrower;

import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.entity.Borrower;
import com.library.app.exception.BorrowerAlreadyExistException;
import com.library.app.repository.BorrowerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBorrowerService {

  private final BorrowerRepository borrowerRepository;

  public BorrowerResponseDTO createBorrower(UpsertBorrowerRequestDTO requestDTO) {
    final Optional<Borrower> existingBorrower = this.borrowerRepository
        .findByEmail(requestDTO.getEmail());

    if (existingBorrower.isPresent()) {
      throw new BorrowerAlreadyExistException();
    }

    final Borrower newBorrower = Borrower.builder()
        .email(requestDTO.getEmail())
        .name(requestDTO.getName())
        .build();

    this.borrowerRepository.save(newBorrower);

    return BorrowerResponseDTO.builder()
        .id(newBorrower.getId())
        .email(newBorrower.getEmail())
        .name(newBorrower.getName())
        .build();
  }
}
