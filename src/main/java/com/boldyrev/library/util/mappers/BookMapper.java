package com.boldyrev.library.util.mappers;

import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.models.Book;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public abstract class BookMapper {

    @Autowired
    protected AuthorMapper authorMapper;

    @Mapping(target = "authors", expression = "java(book.getAuthors().stream().map(a -> authorMapper.authorToAuthorDTO(a)).collect(Collectors.toSet()))")
    public abstract BookDTO bookToBookDTO(Book book);

    @Mapping(target = "authors", expression = "java(book.getAuthors().stream().map(a -> authorMapper.authorDTOToAuthor(a)).collect(Collectors.toSet()))")
    public abstract Book bookDTOToBook(BookDTO book);
}
