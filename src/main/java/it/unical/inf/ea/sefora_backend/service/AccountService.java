package it.unical.inf.ea.sefora_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dao.TokenDao;
import it.unical.inf.ea.sefora_backend.dto.AccountDto;
import it.unical.inf.ea.sefora_backend.dto.AccountShortDto;
import it.unical.inf.ea.sefora_backend.entities.Account;
import it.unical.inf.ea.sefora_backend.entities.ChangePasswordRequest;
import it.unical.inf.ea.sefora_backend.entities.Role;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountDao dao;
    private final TokenDao tokenDao;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final String defaultImage = "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";

    static AccountDto convertToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setFirstname(account.getFirstname());
        accountDto.setLastname(account.getLastname());
        accountDto.setEmail(account.getEmail());
        accountDto.setRole(account.getRole());
        accountDto.setBanned(account.getBanned());
        accountDto.setProfileImage(account.getProfileImage());
        accountDto.setPhone(account.getPhone());
        return accountDto;
    }

    public void changePassword(ChangePasswordRequest request, Principal currentAccount) {

        var account = (Account) ((UsernamePasswordAuthenticationToken) currentAccount).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
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
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        dao.save(account);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var account = Account.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .banned(false)
                .profileImage(defaultImage)
                .build();
        var savedAccount = dao.save(account);
        var jwtToken = jwtService.generateToken(account);
        var refreshToken = jwtService.generateRefreshToken(account);
        saveAccountToken(savedAccount, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .account(convertToDto(savedAccount))
                .expiresIn(jwtService.getJwtDateExpiration())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + request.getEmail());
            e.printStackTrace();
            throw e;
        }


        var account = dao.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(account);
        var refreshToken = jwtService.generateRefreshToken(account);
        revokeAllAccountTokens(account);
        saveAccountToken(account, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .account(convertToDto(account))
                .expiresIn(jwtService.getJwtDateExpiration())
                .build();
    }

    private void saveAccountToken(Account account, String jwtToken) {
        var token = Token.builder()
                .account(account)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenDao.save(token);
    }

    private void revokeAllAccountTokens(Account account) {
        var validAccountTokens = tokenDao.findAllValidTokenByAccount(account.getId());
        if (validAccountTokens.isEmpty())
            return;
        validAccountTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenDao.saveAll(validAccountTokens);
    }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String accountEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        refreshToken = authHeader.substring(7);
        accountEmail = jwtService.extractUsername(refreshToken);
        if (accountEmail != null) {
            var account = this.dao.findByEmail(accountEmail)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            if (jwtService.isTokenValid(refreshToken, account)) {
                var accessToken = jwtService.generateToken(account);
                revokeAllAccountTokens(account);
                saveAccountToken(account, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .account(convertToDto(account))
                        .expiresIn(jwtService.getJwtDateExpiration())
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                return authResponse;
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public AuthenticationResponse updateAccount(AccountDto request, Principal connectedAccount) {
        var account = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();

        if(!account.getEmail().equals(request.getEmail()))
            throw new RuntimeException("You can't change your email!");

        var account1 = dao.findById(account.getId())
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account1.setFirstname(request.getFirstname());
        account1.setLastname(request.getLastname());
        account1.setEmail(request.getEmail());
        account1.setRole(request.getRole());
        account1.setBanned(request.getBanned());
        account1.setPhone(request.getPhone());
        if(request.getProfileImage() == null) account1.setProfileImage(defaultImage);
        else account1.setProfileImage(request.getProfileImage());
        dao.save(account1);

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(account1))
                .refreshToken(jwtService.generateRefreshToken(account1))
                .account(convertToDto(account1))
                .expiresIn(jwtService.getJwtDateExpiration())
                .build();
    }

    public void logout(Principal connectedAccount) {
        var account = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        revokeAllAccountTokens(account);
    }

    public AccountDto getConnectedAccount(Principal connectedAccount) {
        var account = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        return convertToDto(account);
    }

    public void banAccount(Long accountId, Principal connectedAccount) {
        var account1 = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        if(account1.getId().equals(accountId))
            throw new RuntimeException("You can't ban yourself!");

        var account = dao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account.setBanned(true);
        dao.save(account);
    }

    public void unbanAccount(Long accountId, Principal connectedAccount) {
        var account1 = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        if(account1.getId().equals(accountId))
            throw new RuntimeException("You can't unban yourself!");

        var account = dao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account.setBanned(false);
        dao.save(account);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        var account = dao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        tokenDao.deleteByAccount_Id(accountId);
        dao.delete(account);
    }

    public AccountDto getAccountById(Long accountId) {
        var account = dao.findById(accountId)
                .orElseThrow();
        return convertToDto(account);
    }

    public void makeAdmin(Long accountId, Principal connectedAccount) {
        var account1 = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        if(account1.getId().equals(accountId))
            throw new RuntimeException("You can't make yourself an admin!");

        var account = dao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account.setRole(Role.ADMIN);
        dao.save(account);
    }

    public void removeAdmin(Long accountId, Principal connectedAccount) {
        var account1 = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();
        if(account1.getId().equals(accountId))
            throw new RuntimeException("You can't remove your admin role!");

        var account = dao.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account.setRole(Role.USER);
        dao.save(account);
    }

    public void updateImage(AccountShortDto shortDto, Principal connectedAccount) {
        var account = (Account) ((UsernamePasswordAuthenticationToken) connectedAccount).getPrincipal();

        if(shortDto.getProfileImage() == null)
            throw new RuntimeException("Profile image is required!");

        var account1 = dao.findById(account.getId())
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account1.setProfileImage(shortDto.getProfileImage());
        dao.save(account1);
        convertToDto(account1);
    }

    public List<AccountShortDto> getAllAccount() {
        return dao.findAll().stream()
                .map(account -> AccountShortDto.builder()
                        .id(account.getId())
                        .firstname(account.getFirstname())
                        .email(account.getEmail())
                        .profileImage(account.getProfileImage())
                        .role(account.getRole())
                        .isBanned(account.getBanned())
                        .build())
                .toList();
    }
}
