package com.boldyrev.library.services;

import com.boldyrev.library.models.Author;
import org.springframework.data.domain.Page;

public interface AuthorsService {

    Page<Author> findAllByPage(int page, int size);

    Author save(Author author);

    Author updateById(long id, Author author);

    void deleteById(long id);
}
