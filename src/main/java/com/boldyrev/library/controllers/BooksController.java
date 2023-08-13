package com.boldyrev.library.controllers;

import com.boldyrev.library.controllers.responses.CustomResponse;
import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.dto.transfer.NewOrUpdateBook;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.services.BooksService;
import com.boldyrev.library.util.mappers.BookMapper;
import com.boldyrev.library.util.validators.entity_validators.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BooksController {

    private final BooksService booksService;
    private final BookMapper bookMapper;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BooksService booksService, BookMapper bookMapper,
        BookValidator bookValidator) {
        this.booksService = booksService;
        this.bookMapper = bookMapper;
        this.bookValidator = bookValidator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Validated(NewOrUpdateBook.class) BookDTO book,
        BindingResult errors) {
        bookValidator.validate(book, errors);
        Book savedBook = booksService.save(bookMapper.bookDTOToBook(book));

        return new ResponseEntity<>(CustomResponse.builder()
            .body(bookMapper.bookToBookDTO(savedBook)).build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") Long id,
        @RequestBody @Validated(NewOrUpdateBook.class) BookDTO book, BindingResult errors) {
        bookValidator.validate(book, errors);
        Book savedBook = booksService.updateById(id, bookMapper.bookDTOToBook(book));

        return ResponseEntity.ok()
            .body(CustomResponse.builder().body(bookMapper.bookToBookDTO(savedBook)).build());
    }

    @DeleteMapping(value = "/{id}", consumes = {})
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        booksService.deleteById(id);
        return ResponseEntity.ok()
            .body(CustomResponse.builder().message("Book was deleted or not exists").build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByParameters(
        @RequestParam(value = "title", defaultValue = "") String title,
        @RequestParam(value = "isbn", defaultValue = "") String isbn,
        @RequestParam(value = "author", defaultValue = "") String authorName,
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "5") Integer size) {

        Page<BookDTO> books = booksService.search(title, isbn, authorName, page, size)
            .map(bookMapper::bookToBookDTO);

        return ResponseEntity.ok(books);
    }
}
