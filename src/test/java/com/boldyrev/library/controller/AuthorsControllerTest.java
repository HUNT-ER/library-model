package com.boldyrev.library.controller;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.exceptions.DataNotFoundException;
import com.boldyrev.library.exceptions.EntityNotFoundException;
import com.boldyrev.library.exceptions.ValidationException;
import com.boldyrev.library.services.AuthorsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
public class AuthorsControllerTest extends ControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @SpyBean
    private final AuthorsService authorsService;

    @Autowired
    public AuthorsControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
        AuthorsService authorsService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authorsService = authorsService;
    }

    @ParameterizedTest
    @CsvSource({"0,1", "0, 2", "0,8"})
    void getAllByPage_PageAndSizeParametersIsValid_ReturnsPageAuthors(int page, int size)
        throws Exception {

        mockMvc.perform(get("/api/v1/authors").params(createPageAndSizeParams(page, size)))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content", Matchers.hasSize(size)))
            .andExpect(jsonPath("$.pageable.pageSize", Matchers.equalTo(size)))
            .andExpect(jsonPath("$.pageable.pageNumber", Matchers.equalTo(page)));
    }

    @ParameterizedTest
    @CsvSource({"-1,1", "0, 0", "0,-1"})
    void getAllByPage_PageOrSizeParametersIsInvalid_ThrowsValidationException(int page, int size)
        throws Exception {
        mockMvc.perform(get("/api/v1/authors")
                .params(createPageAndSizeParams(page, size)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(r -> assertThatExceptionOfType(ConstraintViolationException.class));
    }

    @Test
    void getAllByPage_IfPageIsNotContainsAuthors_ThrowsDataNotFoundException() throws Exception {
        mockMvc.perform(get("/api/v1/authors")
                .params(createPageAndSizeParams(5, 5)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(r -> assertThatExceptionOfType(DataNotFoundException.class));
    }

    @Test
    void create_AuthorDTOIsValid_SavesAuthor() throws Exception {
        AuthorDTO author = new AuthorDTO(null, "Name", LocalDate.now(), "Country");

        mockMvc.perform(post("/api/v1/authors")
                .content(objectMapper.writeValueAsString(author))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.body").exists())
            .andExpect(jsonPath("$.body.id", Matchers.notNullValue()))
            .andExpect(jsonPath("$.body.name", Matchers.equalTo(author.getName())))
            .andExpect(
                jsonPath("$.body.birth_date", Matchers.equalTo(author.getBirthDate().toString())))
            .andExpect(jsonPath("$.body.country", Matchers.equalTo(author.getCountry())));
    }

    @ParameterizedTest
    @MethodSource("getInvalidAuthors")
    void create_AuthorDTOIsInvalid_ThrowsValidationException(AuthorDTO author) throws Exception {
        mockMvc.perform(post("/api/v1/authors")
                .content(objectMapper.writeValueAsString(author))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(r -> assertThatExceptionOfType(ValidationException.class));
    }

    @Test
    void updateById_AuthorDTOIsValidAndIdExists_ReturnsUpdatedAuthor() throws Exception {
        AuthorDTO author = new AuthorDTO(null, "Name", LocalDate.now(), "Country");
        int id = 1;

        mockMvc.perform(put("/api/v1/authors/" + id)
                .content(objectMapper.writeValueAsString(author))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.body").exists())
            .andExpect(jsonPath("$.body.id", Matchers.equalTo(id)))
            .andExpect(jsonPath("$.body.name", Matchers.equalTo(author.getName())))
            .andExpect(
                jsonPath("$.body.birth_date", Matchers.equalTo(author.getBirthDate().toString())))
            .andExpect(jsonPath("$.body.country", Matchers.equalTo(author.getCountry())));
    }

    @ParameterizedTest
    @MethodSource("getInvalidAuthors")
    void updateById_AuthorDTOIsInvalidAndIdExists_ThrowsValidationException(AuthorDTO author)
        throws Exception {
        int id = 1;
        mockMvc.perform(put("/api/v1/authors/" + id)
                .content(objectMapper.writeValueAsString(author))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(r -> assertThatExceptionOfType(ValidationException.class));
    }

    @Test
    void updateById_IdIsNotExists_ThrowsEntityNotFoundException() throws Exception {
        AuthorDTO author = new AuthorDTO(null, "Name", LocalDate.now(), "Country");
        int id = 999;

        mockMvc.perform(put("/api/v1/authors/" + id)
                .content(objectMapper.writeValueAsString(author))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(r -> assertThatExceptionOfType(EntityNotFoundException.class));
    }

    @Test
    void deleteById_ReturnsMessage() throws Exception {
        int id = 999;
        mockMvc.perform(delete("/api/v1/authors/" + id))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.equalTo("Author was deleted or not exists")));

        Mockito.verify(authorsService).deleteById(id);
    }

    public static Stream<AuthorDTO> getInvalidAuthors() {
        return Stream.of(new AuthorDTO(1l, "Name", LocalDate.now(), "Country"),
            new AuthorDTO(null, null, LocalDate.now(), "Country"),
            new AuthorDTO(null, "Name", null, "Country"),
            new AuthorDTO(null, "Name", LocalDate.now(), null),
            new AuthorDTO(null, "", LocalDate.now(), "Country"),
            new AuthorDTO(null, "Name", LocalDate.now(), ""),
            new AuthorDTO(null, "   ", LocalDate.now(), "Country"),
            new AuthorDTO(null, "Name", LocalDate.now(), "   "));
    }
}
