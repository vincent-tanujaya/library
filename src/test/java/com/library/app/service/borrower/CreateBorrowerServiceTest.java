package com.library.app.service.borrower;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.entity.Borrower;
import com.library.app.exception.BorrowerAlreadyExistException;
import com.library.app.repository.BorrowerRepository;
import java.util.Optional;
import java.util.UUID;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateBorrowerServiceTest {

  @Mock
  private BorrowerRepository borrowerRepository;

  @InjectMocks
  private CreateBorrowerService createBorrowerService;

  @BeforeMethod
  public void setUp() {
    this.createBorrowerService = null;

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateBorrower_Success() {
    UpsertBorrowerRequestDTO upsertBorrowerRequestDTO = UpsertBorrowerRequestDTO.builder()
        .email("john.doe@example.com")
        .name("John Doe")
        .build();

    Mockito.when(borrowerRepository.findByEmail(upsertBorrowerRequestDTO.getEmail()))
        .thenReturn(Optional.empty());

    final UUID id = UUID.randomUUID();

    Mockito.when(borrowerRepository.save(Mockito.any(Borrower.class))).thenAnswer(invocation -> {
      Borrower borrower = invocation.getArgument(0);
      borrower.setId(id);

      return borrower;
    });

    BorrowerResponseDTO borrowerResponseDTO = this.createBorrowerService.createBorrower(upsertBorrowerRequestDTO);

    assertNotNull(borrowerResponseDTO);
    assertEquals(borrowerResponseDTO.getId(), id);
    assertEquals(borrowerResponseDTO.getEmail(), "john.doe@example.com");
    assertEquals(borrowerResponseDTO.getName(), "John Doe");

    verify(borrowerRepository).findByEmail("john.doe@example.com");
    verify(borrowerRepository).save(any(Borrower.class));
  }

  @Test(expectedExceptions = BorrowerAlreadyExistException.class)
  public void testCreateBorrower_AlreadyExists() {
    UpsertBorrowerRequestDTO upsertBorrowerRequestDTO = UpsertBorrowerRequestDTO.builder()
        .email("existing@example.com")
        .name("Existing User")
        .build();

    when(borrowerRepository.findByEmail(upsertBorrowerRequestDTO.getEmail()))
        .thenReturn(Optional.of(
            Borrower.builder()
                .id(UUID.randomUUID())
                .email("existing@example.com")
                .name("Existing User")
                .build()
        ));

    createBorrowerService.createBorrower(upsertBorrowerRequestDTO);
  }
}