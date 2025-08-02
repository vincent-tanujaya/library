package com.library.app.dto.borrower;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertBorrowerRequestDTO {

  @NotBlank
  private String name;

  @NotBlank
  private String email;
}
