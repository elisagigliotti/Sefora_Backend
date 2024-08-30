package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountShortDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String email;
    @NotEmpty
    private String firstname;
}
