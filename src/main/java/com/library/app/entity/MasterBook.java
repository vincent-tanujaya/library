package com.library.app.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import org.hibernate.annotations.UuidGenerator;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "master_books", uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
public class MasterBook extends BaseEntity {

  @Id
  @UuidGenerator
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  @NotNull
  @Column(name = "isbn")
  private String isbn;

  @NotNull
  @Column(name = "title")
  private String title;

  @NotNull
  @Column(name = "author")
  private String author;
}