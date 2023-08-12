package com.boldyrev.library.services;

import com.boldyrev.library.models.Book;
import org.springframework.data.domain.Page;

public interface BooksService {

    Page<Book> findByTitleAndPage(String title, int page, int size);

    Page<Book> findByAuthorNameAndPage(String authorName, int page, int size);

    Page<Book> findByISBNAndPage(String isbn, int page, int size);

    Book save(Book book);

    Book updateById(long id, Book book);

    void deleteById(long id);
}
