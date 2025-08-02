package com.library.app.repository;

import com.library.app.entity.Book;
import com.library.app.entity.Borrower;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {

  Optional<Borrower> findByEmail(String email);
}
