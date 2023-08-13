package com.boldyrev.library.services;

import com.boldyrev.library.models.Author;
import com.boldyrev.library.models.Book;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface BooksService {

    Page<Book> search(String title, String ISBN, String authorName, int page, int size);

    Book save(Book book);

    Book updateById(long id, Book book);

    Book updateAuthors(Book book, Set<Author> authors);

    void deleteById(long id);
}
