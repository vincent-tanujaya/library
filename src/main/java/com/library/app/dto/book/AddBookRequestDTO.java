package com.library.app.dto.book;

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
public class AddBookRequestDTO {

  @NotBlank
  private String isbn;

  @NotBlank
  private String title;

  @NotBlank
  private String author;
}
