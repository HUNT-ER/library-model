package com.boldyrev.library.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.models.Author;
import com.boldyrev.library.models.Book;
import com.boldyrev.library.util.mappers.AuthorMapper;
import com.boldyrev.library.util.mappers.BookMapper;
import com.boldyrev.library.util.mappers.BookMapperImpl;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookMapperTest {

    @Spy
    private AuthorMapper authorMapper;

    @InjectMocks
    private BookMapper bookMapper = new BookMapperImpl();


    public static Stream<Book> getBooks() {
        return Stream.of(
            new Book(1l, "Title", "978-14-2314561-2", 1000, LocalDate.now(), Set.of()),
            new Book(null, null, null, null, null, Set.of(new Author()))
        );
    }

    public static Stream<BookDTO> getBooksDTO() {
        return Stream.of(
            new BookDTO(1l, "Title", "978-14-2314561-2", 1000, LocalDate.now(), Set.of()),
            new BookDTO(null, null, null, null, null, Set.of(new AuthorDTO()))
        );
    }

    @ParameterizedTest
    @MethodSource("getBooksDTO")
    void shouldMapBookToDTOCorrectly(BookDTO bookDTO) {
        Book book = bookMapper.bookDTOToBook(bookDTO);

        assertThat(book.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(book.getISBN()).isEqualTo(bookDTO.getISBN());
        assertThat(book.getNumPages()).isEqualTo(bookDTO.getNumPages());
        assertThat(book.getPublicationDate()).isEqualTo(bookDTO.getPublicationDate());
    }

    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldMapBookToDTOCorrectly(Book book) {
        BookDTO bookDTO = bookMapper.bookToBookDTO(book);

        assertThat(book.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(book.getISBN()).isEqualTo(bookDTO.getISBN());
        assertThat(book.getNumPages()).isEqualTo(bookDTO.getNumPages());
        assertThat(book.getPublicationDate()).isEqualTo(bookDTO.getPublicationDate());
    }
}
