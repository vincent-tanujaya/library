package com.library.app.service.borrower;

import com.library.app.dto.borrower.UpsertBorrowerRequestDTO;
import com.library.app.dto.borrower.BorrowerResponseDTO;
import com.library.app.entity.Borrower;
import com.library.app.exception.BorrowerAlreadyExistException;
import com.library.app.exception.DataNotFoundException;
import com.library.app.repository.BorrowerRepository;
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

public class UpdateBorrowerServiceTest {

  @Mock
  private BorrowerRepository borrowerRepository;

  @InjectMocks
  private UpdateBorrowerService updateBorrowerService;

  private final UUID existingId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @BeforeMethod
  public void setUp() {
    this.updateBorrowerService = null;

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testUpdateBorrower_Success() {
    UpsertBorrowerRequestDTO upsertBorrowerRequestDTO = UpsertBorrowerRequestDTO.builder()
        .email("new.email@example.com")
        .name("New Name")
        .build();

    Borrower borrower = Borrower.builder()
        .id(existingId)
        .email("old.email@example.com")
        .name("Old Name")
        .build();

    when(borrowerRepository.findByEmail(upsertBorrowerRequestDTO.getEmail()))
        .thenReturn(Optional.empty());
    when(borrowerRepository.findById(existingId))
        .thenReturn(Optional.of(borrower));
    when(borrowerRepository.save(any(Borrower.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BorrowerResponseDTO response = updateBorrowerService.updateBorrower(existingId, upsertBorrowerRequestDTO);

    assertNotNull(response);
    assertEquals(response.getId(), existingId);
    assertEquals(response.getEmail(), "new.email@example.com");
    assertEquals(response.getName(), "New Name");

    verify(borrowerRepository).findByEmail("new.email@example.com");
    verify(borrowerRepository).findById(existingId);
    verify(borrowerRepository).save(any(Borrower.class));
  }

  @Test(expectedExceptions = BorrowerAlreadyExistException.class)
  public void testUpdateBorrower_WhenEmailExists_ShouldThrow() {
    UpsertBorrowerRequestDTO upsertBorrowerRequestDTO = UpsertBorrowerRequestDTO.builder()
        .email("exists@example.com")
        .name("Name")
        .build();

    when(borrowerRepository.findByEmail(upsertBorrowerRequestDTO.getEmail()))
        .thenReturn(Optional.of(Borrower.builder()
            .id(UUID.randomUUID())
            .email("exists@example.com")
            .name("Other")
            .build()
        ));

    updateBorrowerService.updateBorrower(existingId, upsertBorrowerRequestDTO);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void testUpdateBorrower_WhenNotFound_ShouldThrow() {
    UpsertBorrowerRequestDTO upsertBorrowerRequestDTO = UpsertBorrowerRequestDTO.builder()
        .email("unique@example.com")
        .name("Name")
        .build();

    when(borrowerRepository.findByEmail(upsertBorrowerRequestDTO.getEmail()))
        .thenReturn(Optional.empty());
    when(borrowerRepository.findById(existingId))
        .thenReturn(Optional.empty());

    updateBorrowerService.updateBorrower(existingId, upsertBorrowerRequestDTO);
  }
}
