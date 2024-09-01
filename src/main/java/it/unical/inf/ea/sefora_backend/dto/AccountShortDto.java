package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountShortDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String email;
    @NotEmpty
    private String firstname;
    private String profileImage;
    @NotNull
    private Role role;
    @NotNull
    private Boolean isBanned;
}
