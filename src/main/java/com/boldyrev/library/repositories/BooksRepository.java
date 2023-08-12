package com.boldyrev.library.repositories;

import com.boldyrev.library.models.Author;
import com.boldyrev.library.models.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(Author author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    Optional<Book> findByISBN(String isbn);
}
