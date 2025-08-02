package com.library.app.repository;

import com.library.app.entity.Borrower;
import com.library.app.entity.MasterBook;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterBookRepository extends JpaRepository<MasterBook, UUID> {

  Optional<MasterBook> findByIsbnAndAuthorAndTitle(String isbn, String author, String title);
}
