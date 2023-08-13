package com.boldyrev.library.controllers;

import com.boldyrev.library.controllers.responses.CustomResponse;
import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.dto.transfer.NewOrUpdateAuthor;
import com.boldyrev.library.models.Author;
import com.boldyrev.library.services.AuthorsService;
import com.boldyrev.library.util.mappers.AuthorMapper;
import com.boldyrev.library.util.validators.entity_validators.AuthorValidator;
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
@RequestMapping(value = "/api/v1/authors", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorsController {

    private final AuthorsService authorsService;
    private final AuthorMapper authorMapper;
    private final AuthorValidator authorValidator;

    @Autowired
    public AuthorsController(AuthorsService authorsService, AuthorMapper authorMapper,
        AuthorValidator authorValidator) {
        this.authorsService = authorsService;
        this.authorMapper = authorMapper;
        this.authorValidator = authorValidator;
    }

    @GetMapping
    public ResponseEntity<?> getAllByPage(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<AuthorDTO> authors = authorsService.findAllByPage(page, size)
            .map(authorMapper::authorToAuthorDTO);

        return ResponseEntity.ok(authors);
    }

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody @Validated(NewOrUpdateAuthor.class) AuthorDTO author,
        BindingResult errors) {
        authorValidator.validate(author, errors);
        Author savedAuthor = authorsService.save(authorMapper.authorDTOToAuthor(author));

        return new ResponseEntity<>(CustomResponse.builder()
            .body(authorMapper.authorToAuthorDTO(savedAuthor)).build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") Long id,
        @RequestBody @Validated(NewOrUpdateAuthor.class) AuthorDTO author, BindingResult errors) {
        authorValidator.validate(author, errors);
        Author savedAuthor = authorsService.updateById(id, authorMapper.authorDTOToAuthor(author));

        return ResponseEntity.ok(
            CustomResponse.builder().body(authorMapper.authorToAuthorDTO(savedAuthor)).build());
    }

    @DeleteMapping(value = "/{id}", consumes = {})
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        authorsService.deleteById(id);
        return ResponseEntity.ok(
            CustomResponse.builder().message("Author was deleted or not exists").build());
    }
}
