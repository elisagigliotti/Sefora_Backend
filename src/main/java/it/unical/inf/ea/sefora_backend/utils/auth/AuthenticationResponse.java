package it.unical.inf.ea.sefora_backend.utils.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unical.inf.ea.sefora_backend.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("user")
    private AccountDto account;
    @JsonProperty("expires_in")
    private Long expiresIn;
}
