package com.boldyrev.library.controller;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.exceptions.DataNotFoundException;
import com.boldyrev.library.exceptions.EntityNotFoundException;
import com.boldyrev.library.exceptions.ValidationException;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.services.BooksService;
import com.boldyrev.library.util.mappers.BookMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Sql("/db/migration/V1__data.sql")
@Transactional
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class BooksControllerTest extends ControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @SpyBean
    private final BooksService booksService;

    private final BookMapper bookMapper;

    @Autowired
    public BooksControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
        BooksService booksService, BookMapper bookMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.booksService = booksService;
        this.bookMapper = bookMapper;
    }

    public BookDTO getCorrectBookDTO() {
        return new BookDTO(null, "Анна Каренина", "9785041079277", 1000,
            LocalDate.of(1873, 1, 1),
            Set.of(new AuthorDTO(1l, null, null, null)));
    }

    @ParameterizedTest
    @MethodSource("getSearchParameters")
    void search_AllParametersIsValid_ReturnsPageBooks(SearchFilter filter,
        int resultSize) throws Exception {
        HttpHeaders params = new HttpHeaders();
        params.add("title", filter.title);
        params.add("isbn", filter.isbn);
        params.add("author", filter.author);

        mockMvc.perform(get("/api/v1/books/search")
                .characterEncoding(Charset.defaultCharset())
                .params(params)
                .header("Accept-Charset", "windows-1251")
                .characterEncoding("windows-1251"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content", Matchers.hasSize(resultSize)))
            .andExpect(jsonPath("$.pageable.pageSize").exists())
            .andExpect(jsonPath("$.pageable.pageNumber").exists());

        Mockito.verify(booksService)
            .search(anyString(), anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    void search_SearchIsNotContainsResults_ThrowsDataNotFoundException() throws Exception {
        HttpHeaders params = new HttpHeaders();
        params.add("title", "Такого названия не существует");

        mockMvc.perform(get("/api/v1/books/search")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charset.defaultCharset())
                .params(params)
                .header("Accept-Charset", "windows-1251")
                .characterEncoding("windows-1251"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(r -> assertThatExceptionOfType(DataNotFoundException.class));

        Mockito.verify(booksService)
            .search(anyString(), anyString(), anyString(), anyInt(), anyInt());
    }


    @ParameterizedTest
    @CsvSource({"0,1", "0, 2", "0,8"})
    void search_PageAndSizeParametersIsValid_ReturnsPageBooks(int page, int size)
        throws Exception {

        mockMvc.perform(get("/api/v1/books/search")
                .params(createPageAndSizeParams(page, size)))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content", Matchers.hasSize(size)))
            .andExpect(jsonPath("$.pageable.pageSize", Matchers.equalTo(size)))
            .andExpect(jsonPath("$.pageable.pageNumber", Matchers.equalTo(page)));

        Mockito.verify(booksService)
            .search(anyString(), anyString(), anyString(), anyInt(), anyInt());
    }

    @ParameterizedTest
    @CsvSource({"-1,1", "0, 0", "0,-1"})
    void search_PageOrSizeParametersIsInvalid_ThrowsValidationException(int page, int size)
        throws Exception {
        mockMvc.perform(get("/api/v1/books/search")
                .params(createPageAndSizeParams(page, size)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(r -> assertThatExceptionOfType(ConstraintViolationException.class));

        Mockito.verifyNoInteractions(booksService);
    }


    @Test
    void create_BookDTOIsValid_SavesBook() throws Exception {
        BookDTO book = getCorrectBookDTO();
        mockMvc.perform(post("/api/v1/books")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.body").exists())
            .andExpect(jsonPath("$.body.id", Matchers.notNullValue()))
            .andExpect(jsonPath("$.body.title", Matchers.equalTo(book.getTitle())))
            .andExpect(jsonPath("$.body.isbn", Matchers.equalTo(book.getISBN())))
            .andExpect(jsonPath("$.body.num_pages", Matchers.equalTo(book.getNumPages())))
            .andExpect(jsonPath("$.body.publication_date",
                Matchers.equalTo(book.getPublicationDate().toString())))
            .andExpect(jsonPath("$.body.authors", Matchers.hasSize(book.getAuthors().size())));

        Mockito.verify(booksService).save(any());
    }

    @ParameterizedTest
    @MethodSource("getInvalidBooks")
    void create_AuthorDTOIsInvalid_ThrowsValidationException(BookDTO book) throws Exception {
        mockMvc.perform(post("/api/v1/authors")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(r -> assertThatExceptionOfType(ValidationException.class));

        Mockito.verifyNoInteractions(booksService);
    }

    @Test
    void updateById_BookDTOIsValidAndIdExists_ReturnsUpdatedBook() throws Exception {
        BookDTO book = getCorrectBookDTO();
        Book bookToService = bookMapper.bookDTOToBook(book);
        int id = 1;

        mockMvc.perform(put("/api/v1/books/" + id)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.body").exists())
            .andExpect(jsonPath("$.body.id", Matchers.equalTo(id)))
            .andExpect(jsonPath("$.body.title", Matchers.equalTo(book.getTitle())))
            .andExpect(jsonPath("$.body.isbn", Matchers.equalTo(book.getISBN())))
            .andExpect(jsonPath("$.body.num_pages", Matchers.equalTo(book.getNumPages())))
            .andExpect(jsonPath("$.body.publication_date",
                Matchers.equalTo(book.getPublicationDate().toString())))
            .andExpect(jsonPath("$.body.authors", Matchers.hasSize(book.getAuthors().size())));

        Mockito.verify(booksService).updateById(id, bookToService);
    }


    @ParameterizedTest
    @MethodSource("getInvalidBooks")
    void updateById_BookDTOIsInvalidAndIdExists_ThrowsValidationException(BookDTO book)
        throws Exception {
        int id = 1;
        mockMvc.perform(put("/api/v1/authors/" + id)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(r -> assertThatExceptionOfType(ValidationException.class));

        Mockito.verifyNoInteractions(booksService);
    }

    @Test
    void updateById_IdIsNotExists_ThrowsEntityNotFoundException() throws Exception {
        BookDTO book = getCorrectBookDTO();
        Book bookToService = bookMapper.bookDTOToBook(book);
        int id = 999;

        mockMvc.perform(put("/api/v1/books/" + id)
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(r -> assertThatExceptionOfType(EntityNotFoundException.class));

        Mockito.verify(booksService).updateById(id, bookToService);
    }

    @Test
    void deleteById_ReturnsMessage() throws Exception {
        int id = 999;
        mockMvc.perform(delete("/api/v1/books/" + id))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.equalTo("Book was deleted or not exists")));

        Mockito.verify(booksService).deleteById(id);
    }

    public static Stream<BookDTO> getInvalidBooks() {
        return Stream.of(
            new BookDTO(1l, "Анна Каренина", "9785041079277", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),

            new BookDTO(null, null, "9785041079277", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),
            new BookDTO(null, "", "9785041079277", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),

            new BookDTO(null, "Анна Каренина", null, 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),
            new BookDTO(null, "Анна Каренина", "", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),
            new BookDTO(null, "Анна Каренина", "9785041079276", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),

            new BookDTO(null, "Анна Каренина", "9785041079277", null, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),
            new BookDTO(null, "Анна Каренина", "9785041079277", 0, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(1l, null, null, null))),

            new BookDTO(null, "Анна Каренина", "9785041079277", 1000, null,
                Set.of(new AuthorDTO(1l, null, null, null))),

            new BookDTO(null, "Анна Каренина", "9785041079277", 1000, LocalDate.of(1873, 1, 1),
                null),
            new BookDTO(null, "Анна Каренина", "9785041079277", 1000, LocalDate.of(1873, 1, 1),
                Set.of(new AuthorDTO(null, null, null, null)))
        );
    }

    public static Stream<Arguments> getSearchParameters() {
        return Stream.of(
            Arguments.of(new SearchFilter("Колесо", null, null), 2),
            Arguments.of(new SearchFilter(null, "547", null), 2),
            Arguments.of(new SearchFilter(null, null, "Толстой"), 1),
            Arguments.of(new SearchFilter(null, null, null), 10),
            Arguments.of(new SearchFilter("Колесо", "978", "Роберт"), 2)
        );
    }

    private static class SearchFilter {

        private String title;
        private String isbn;
        private String author;

        public SearchFilter(String title, String isbn, String author) {
            this.title = title;
            this.isbn = isbn;
            this.author = author;
        }
    }
}
