package com.boldyrev.library.services.impl;

import com.boldyrev.library.exceptions.EntityNotFoundException;
import com.boldyrev.library.models.Author;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.repositories.BooksRepository;
import com.boldyrev.library.services.BooksService;
import com.boldyrev.library.util.validators.PageValidator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.ISBNValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BooksServiceImpl implements BooksService {

    private final BooksRepository booksRepository;
    private final PageValidator pageValidator;

    @Autowired
    public BooksServiceImpl(BooksRepository booksRepository, PageValidator pageValidator) {
        this.booksRepository = booksRepository;
        this.pageValidator = pageValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> search(String title, String ISBN, String authorName, int page, int size) {
        Page<Book> books = booksRepository.findByParameters(title, enrichISBN(ISBN), authorName,
            PageRequest.of(page, size, Sort.by("title")));

        pageValidator.validate(books, new String[]{title, ISBN, authorName});

        return books;
    }

    @Override
    @Transactional
    public Book save(Book book) {
        book.getAuthors().forEach(a -> a.addBook(book));
        book.setISBN(enrichISBN(book.getISBN()));
        return booksRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateById(long id, Book book) {
        Book storedBook = booksRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Book with id=%d not found", id)));

        storedBook.setTitle(book.getTitle());
        storedBook.setISBN(enrichISBN(book.getISBN()));
        storedBook.setPublicationDate(book.getPublicationDate());
        storedBook.setNumPages(book.getNumPages());

        updateAuthors(storedBook, book.getAuthors());

        return storedBook;
    }

    @Override
    @Transactional
    public void deleteById(long id) {

        booksRepository.deleteById(id);
    }

    @Transactional
    public Book updateAuthors(Book book, Set<Author> authors) {
        Set<Author> currentAuthors = book.getAuthors();
        if (!authors.equals(currentAuthors)) {
            currentAuthors.forEach(a -> a.removeBook(book));
            currentAuthors.clear();
            book.setAuthors(authors);
        }
        return book;
    }

    private String enrichISBN(String ISBN) {
        String nonDigitsPattern = "[^\\dX]";
        return ISBN.replaceAll(nonDigitsPattern, "");
    }
}
