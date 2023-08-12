package com.boldyrev.library.services.impl;

import com.boldyrev.library.models.Author;
import com.boldyrev.library.repositories.AuthorsRepository;
import com.boldyrev.library.services.AuthorsService;
import com.boldyrev.library.util.validators.PageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuthorsServiceImpl implements AuthorsService {

    private final AuthorsRepository authorsRepository;
    private final PageValidator pageValidator;

    @Autowired
    public AuthorsServiceImpl(AuthorsRepository authorsRepository, PageValidator pageValidator) {
        this.authorsRepository = authorsRepository;
        this.pageValidator = pageValidator;
    }

    @Override
    public Page<Author> findAllByPage(int page, int size) {
        Page<Author> authors = authorsRepository.findAll(
            PageRequest.of(page, size, Sort.by("name")));

        pageValidator.validate(authors, null);

        return authors;
    }

    @Override
    public Author save(Author author) {
        return authorsRepository.save(author);
    }

    @Override
    public Author updateById(long id, Author author) {
        Author storedAuthor = authorsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Author with id=%d not found", id)));

        storedAuthor.setName(author.getName());
        storedAuthor.setEmail(author.getEmail());
        storedAuthor.setBooks(author.getBooks());

        return storedAuthor;
    }

    @Override
    public void deleteById(long id) {

    }
}
