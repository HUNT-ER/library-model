package com.boldyrev.library.services.impl;

import com.boldyrev.library.exceptions.DataNotFoundException;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.repositories.BooksRepository;
import com.boldyrev.library.services.BooksService;
import com.boldyrev.library.util.validators.PageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    public Page<Book> findByTitleAndPage(String title, int page, int size) {
        Page<Book> books = booksRepository.findByTitleContainingIgnoreCase(title,
            PageRequest.of(page, size, Sort.by("title")));

        pageValidator.validate(books, title);

        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findByAuthorNameAndPage(String authorName, int page, int size) {
        Page<Book> books = booksRepository.findByAuthorName(authorName, PageRequest.of(page, size,
            Sort.by("title")));

        pageValidator.validate(books, authorName);

        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findByISBNAndPage(String ISBN, int page, int size) {
        Page<Book> books = booksRepository.findByISBNContaining(ISBN, PageRequest.of(page, size,
            Sort.by("title")));

        pageValidator.validate(books, ISBN);

        return books;
    }

    @Override
    @Transactional
    public Book save(Book book) {
        return booksRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateById(long id, Book book) {
        Book storedBook = booksRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Book with id=%d not found", id)));

        storedBook.setTitle(book.getTitle());
        storedBook.setISBN(book.getISBN());
        storedBook.setPublicationDate(book.getPublicationDate());
        storedBook.setNumPages(book.getNumPages());

        //todo m2m, watch
        storedBook.setAuthors(book.getAuthors());

        return storedBook;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        booksRepository.deleteById(id);
    }
}
