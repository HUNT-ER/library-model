package com.boldyrev.library.repositories;

import com.boldyrev.library.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BooksRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN FETCH b.authors a WHERE UPPER(a.name) LIKE CONCAT('%', UPPER(:name), '%') ")
    Page<Book> findByAuthorName(@Param("name") String authorName, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.authors WHERE UPPER(b.title) LIKE CONCAT('%', UPPER(:title), '%') ")
    Page<Book> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.authors WHERE UPPER(b.ISBN) LIKE CONCAT('%', UPPER(:isbn), '%') ")
    Page<Book> findByISBNContaining(@Param("isbn") String isbn, Pageable pageable);
}
