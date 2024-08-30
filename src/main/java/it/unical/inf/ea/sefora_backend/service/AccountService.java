package it.unical.inf.ea.sefora_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unical.inf.ea.sefora_backend.dao.TokenDao;
import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dto.AccountDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.ChangePasswordRequest;
import it.unical.inf.ea.sefora_backend.entities.token.Token;
import it.unical.inf.ea.sefora_backend.entities.token.TokenType;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationRequest;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationResponse;
import it.unical.inf.ea.sefora_backend.utils.auth.RegisterRequest;
import it.unical.inf.ea.sefora_backend.utils.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountDao dao;
    private final TokenDao tokenDao;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    static AccountDto convertToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setFirstname(account.getFirstname());
        accountDto.setLastname(account.getLastname());
        accountDto.setEmail(account.getEmail());
        accountDto.setRole(account.getRole());
        accountDto.setBanned(account.getBanned());
        return accountDto;
    }

    public void changePassword(ChangePasswordRequest request, Principal currentUser) {

        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalStateException("New password is the same as the current password");
        }

        if (!request.getNewPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
            throw new IllegalStateException("Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and have a minimum length of 8 characters");
        }

        if (!request.getConfirmationPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
            throw new IllegalStateException("Confirmation password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and have a minimum length of 8 characters");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        dao.save(user);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Account.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .banned(false)
                .build();
        var savedUser = dao.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(convertToDto(savedUser))
                .expiresIn(jwtService.getJwtDateExpiration())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = dao.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(convertToDto(user))
                .expiresIn(jwtService.getJwtDateExpiration())
                .build();
    }

    private void saveUserToken(Account account, String jwtToken) {
        var token = Token.builder()
                .account(account)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenDao.save(token);
    }

    private void revokeAllUserTokens(Account account) {
        var validUserTokens = tokenDao.findAllValidTokenByAccount(account.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenDao.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.dao.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void updateUser(AccountDto request, Principal connectedUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setBanned(request.getBanned());
        dao.save(user);
    }

    public void logout(Principal connectedUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        revokeAllUserTokens(user);
    }

    public AccountDto getConnectedUser(Principal connectedUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return convertToDto(user);
    }

    public void banUser(Long userId, Principal connectedUser) {
        var user1 = (Account) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if(user1.getId().equals(userId)){
            throw new RuntimeException("You can't ban yourself!");
        }
        var user = dao.findById(userId)
                .orElseThrow();
        user.setBanned(true);
        dao.save(user);
    }

    public void unbanUser(Long userId, Principal connectedUser) {
        var user1 = (Account) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if(user1.getId().equals(userId)){
            throw new RuntimeException("You can't unban yourself!");
        }
        var user = dao.findById(userId)
                .orElseThrow();
        user.setBanned(false);
        dao.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        //devo eliminare tutti i token quando elimino un utente senno
        //ERROR: update or delete on table "s_users" violates foreign key constraint "fkad7iy5w5lcn120sxxnl1khhcf" on table "token"
        tokenDao.deleteByAccount_Id(userId);
        dao.delete(user);
    }

    public AccountDto getUserById(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        return convertToDto(user);
    }
}
