package it.unical.inf.ea.sefora_backend.utils.auth;

import it.unical.inf.ea.sefora_backend.entities.Role;
import it.unical.inf.ea.sefora_backend.utils.validation.ValidEmailAndPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidEmailAndPassword
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}
