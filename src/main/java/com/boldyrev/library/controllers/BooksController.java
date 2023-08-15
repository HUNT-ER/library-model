package com.boldyrev.library.controllers;

import com.boldyrev.library.controllers.responses.CustomResponse;
import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.dto.transfer.NewOrUpdateBook;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.services.BooksService;
import com.boldyrev.library.util.mappers.BookMapper;
import com.boldyrev.library.util.validators.entity_validators.BookValidator;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@Validated
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

    /**
     * POST endpoint для создания новой книги
     *
     * @param book данные новой книги
     * @param errors ошибки валидации
     * @return сохраненная книга
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Validated(NewOrUpdateBook.class) BookDTO book,
        BindingResult errors) {
        bookValidator.validate(book, errors);
        Book savedBook = booksService.save(bookMapper.bookDTOToBook(book));

        return new ResponseEntity<>(CustomResponse.builder()
            .body(bookMapper.bookToBookDTO(savedBook)).build(), HttpStatus.CREATED);
    }

    /**
     * PUT endpoint для обновления существующей книги по ID
     *
     * @param id идентификатор обновляемой книги
     * @param book книга с обновленной информацией
     * @param errors ошибки валидации
     * @return обновленная книга
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") Long id,
        @RequestBody @Validated(NewOrUpdateBook.class) BookDTO book, BindingResult errors) {
        bookValidator.validate(book, errors);
        Book savedBook = booksService.updateById(id, bookMapper.bookDTOToBook(book));

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(CustomResponse.builder().body(bookMapper.bookToBookDTO(savedBook)).build());
    }

    /**
     * DELETE endpoint для удаления книги по ID
     *
     * @param id идентификатор удаляемой книги
     * @return сообщение с успешным удалением
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        booksService.deleteById(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(CustomResponse.builder().message("Book was deleted or not exists").build());
    }

    /**
     * GET endpoint для поиска книг по названию/ISBN/имени автора
     *
     * @param title фильтр названия книги (по умолчанию "")
     * @param ISBN фильтр ISBN (по умолчанию "")
     * @param authorName фильтр имени автора (по умолчанию "")
     * @param page номер возвращаемой страницы (по умолчаниб 0)
     * @param size размер возвращаемой страницы (по умолчанию 10)
     * @return страница с найденными книгами
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchByParameters(
        @RequestParam(value = "title", defaultValue = "") String title,
        @RequestParam(value = "isbn", defaultValue = "") String ISBN,
        @RequestParam(value = "author", defaultValue = "") String authorName,
        @RequestParam(value = "page", defaultValue = "0") @PositiveOrZero Integer page,
        @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {

        Page<BookDTO> books = booksService.search(title, ISBN, authorName, page, size)
            .map(bookMapper::bookToBookDTO);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(books);
    }
}
