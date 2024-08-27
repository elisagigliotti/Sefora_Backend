package it.unical.inf.ea.sefora_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unical.inf.ea.sefora_backend.dao.TokenDao;
import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.dto.UserDto;
import it.unical.inf.ea.sefora_backend.entities.ChangePasswordRequest;
import it.unical.inf.ea.sefora_backend.entities.User;
import it.unical.inf.ea.sefora_backend.entities.token.Token;
import it.unical.inf.ea.sefora_backend.entities.token.TokenType;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationRequest;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationResponse;
import it.unical.inf.ea.sefora_backend.utils.auth.RegisterRequest;
import it.unical.inf.ea.sefora_backend.utils.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserDao dao;
    private final TokenDao tokenDao;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setBanned(user.getBanned());
        return userDto;
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

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
        var user = User.builder()
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

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenDao.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenDao.findAllValidTokenByUser(user.getId());
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

    public void updateUser(UserDto request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setBanned(request.getBanned());
        dao.save(user);
    }

    public void logout(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        revokeAllUserTokens(user);
    }

    public UserDto getConnectedUser(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return convertToDto(user);
    }

    public void banUser(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        user.setBanned(true);
        dao.save(user);
    }

    public void unbanUser(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        user.setBanned(false);
        dao.save(user);
    }

    public void deleteUser(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        dao.delete(user);
    }

    public UserDto getUserById(Long userId) {
        var user = dao.findById(userId)
                .orElseThrow();
        return convertToDto(user);
    }
}
