package com.boldyrev.library.dto;

import com.boldyrev.library.dto.transfer.NewOrUpdateAuthor;
import com.boldyrev.library.dto.transfer.NewOrUpdateBook;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AuthorDTO {

    @Null(groups = {NewOrUpdateAuthor.class})
    @NotNull(groups = {NewOrUpdateBook.class})
    private Long id;

    @NotBlank(groups = {NewOrUpdateAuthor.class})
    private String name;

    @NotNull(groups = {NewOrUpdateAuthor.class})
    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @NotBlank(groups = {NewOrUpdateAuthor.class})
    private String country;
}
