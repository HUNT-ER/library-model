package com.boldyrev.library.dto;

import com.boldyrev.library.dto.transfer.NewOrUpdateBook;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class BookDTO {

    @Null(groups = {NewOrUpdateBook.class})
    private Long id;

    @NotBlank(groups = {NewOrUpdateBook.class})
    private String title;

    @NotBlank(groups = {NewOrUpdateBook.class})
    @JsonProperty("isbn")
    private String ISBN;

    @NotNull(groups = {NewOrUpdateBook.class})
    @Min(value = 1, groups = {NewOrUpdateBook.class})
    @JsonProperty("num_pages")
    private Integer numPages;

    @NotNull(groups = {NewOrUpdateBook.class})
    @JsonProperty("publication_date")
    private LocalDate publicationDate;

    @Valid
    @NotNull(groups = {NewOrUpdateBook.class})
    private Set<AuthorDTO> authors;
}
