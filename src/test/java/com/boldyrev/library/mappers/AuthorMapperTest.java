package com.boldyrev.library.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.models.Author;
import com.boldyrev.library.util.mappers.AuthorMapper;
import com.boldyrev.library.util.mappers.AuthorMapperImpl;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AuthorMapperTest {

    private final AuthorMapper mapper;

    public AuthorMapperTest() {
        this.mapper = new AuthorMapperImpl();
    }

    public static Stream<Author> getAuthors() {
        return Stream.of(
            new Author(null, "Name", LocalDate.of(2000, 12, 1), "Country", null),
            new Author()
        );
    }

    public static Stream<AuthorDTO> getAuthorsDTO() {
        return Stream.of(
            new AuthorDTO(null, "Name", LocalDate.of(2000, 12, 1), "Country"),
            new AuthorDTO()
        );
    }

    @ParameterizedTest
    @MethodSource("getAuthors")
    void shouldMapAuthorToDTOCorrectly(Author author) {
        AuthorDTO authorDTO = mapper.authorToAuthorDTO(author);

        assertThat(author.getName()).isEqualTo(authorDTO.getName());
        assertThat(author.getBirthDate()).isEqualTo(authorDTO.getBirthDate());
        assertThat(author.getCountry()).isEqualTo(authorDTO.getCountry());
    }

    @ParameterizedTest
    @MethodSource("getAuthorsDTO")
    void shouldMapAuthorToDTOCorrectly(AuthorDTO authorDTO) {
        Author author = mapper.authorDTOToAuthor(authorDTO);

        assertThat(author.getName()).isEqualTo(authorDTO.getName());
        assertThat(author.getBirthDate()).isEqualTo(authorDTO.getBirthDate());
        assertThat(author.getCountry()).isEqualTo(authorDTO.getCountry());
    }
}
