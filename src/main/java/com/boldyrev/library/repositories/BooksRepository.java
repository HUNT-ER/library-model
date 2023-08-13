package com.boldyrev.library.repositories;

import com.boldyrev.library.models.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BooksRepository extends JpaRepository<Book, Long> {

    @Query("""
        SELECT b 
        FROM Book b 
        JOIN FETCH b.authors a
        WHERE UPPER(b.title) LIKE CONCAT('%', UPPER(?1), '%')
            AND UPPER(b.ISBN) LIKE CONCAT('%', UPPER(?2), '%')
            AND b.id IN(
                SELECT DISTINCT b.id 
                FROM Book b 
                JOIN b.authors a 
                WHERE UPPER(a.name) LIKE CONCAT('%', UPPER(?3), '%'))
        """)
    Page<Book> findByParameters(String title, String ISBN, String authorName, Pageable pageable);

    @Override
    @Query("SELECT b from Book b JOIN FETCH b.authors a JOIN FETCH a.books WHERE b.id = :id")
    Optional<Book> findById(@Param("id") Long id);
}
