package com.boldyrev.library.repositories;

import com.boldyrev.library.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorsRepository extends JpaRepository<Author, Long> {

}
