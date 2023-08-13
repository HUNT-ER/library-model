package com.boldyrev.library.util.mappers;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.models.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorMapper {

    public abstract AuthorDTO authorToAuthorDTO(Author author);

    public abstract Author authorDTOToAuthor(AuthorDTO author);

}
