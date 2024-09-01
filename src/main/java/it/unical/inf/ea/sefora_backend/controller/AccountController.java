package it.unical.inf.ea.sefora_backend.controller;

import it.unical.inf.ea.sefora_backend.dto.AccountDto;
import it.unical.inf.ea.sefora_backend.dto.AccountShortDto;
import it.unical.inf.ea.sefora_backend.entities.ChangePasswordRequest;
import it.unical.inf.ea.sefora_backend.service.AccountService;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationRequest;
import it.unical.inf.ea.sefora_backend.utils.auth.AuthenticationResponse;
import it.unical.inf.ea.sefora_backend.utils.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            Principal connectedAccount
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return ResponseEntity.ok(service.refreshToken(request, response));
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedAccount
    ) {
        service.changePassword(request, connectedAccount);
        return ResponseEntity.ok("Password changed successfully!");
    }

    @GetMapping("/login-success")
    public ResponseEntity<String> loginSuccess() {
        return ResponseEntity.ok("OAuth2 login successful!");
    }

    @GetMapping("/login-failure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 login failed.");
    }

    @PutMapping
    public ResponseEntity<AuthenticationResponse> updateAccount(
            @RequestBody AccountDto request,
            Principal connectedAccount
    ) {
        return ResponseEntity.ok(service.updateAccount(request, connectedAccount));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(Principal connectedAccount) {
        service.logout(connectedAccount);
        return ResponseEntity.ok("Logged out successfully!");
    }

    @GetMapping("/current")
    public ResponseEntity<AccountDto> getConnectedAccount(Principal connectedAccount) {
        return ResponseEntity.ok(service.getConnectedAccount(connectedAccount));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/ban/{accountId}")
    public ResponseEntity<?> banAccount(
            @PathVariable Long accountId,
            Principal connectedAccount
    ) {
        service.banAccount(accountId, connectedAccount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/unban/{accountId}")
    public ResponseEntity<?> unbanAccount(
            @PathVariable Long accountId,
            Principal connectedAccount
    ) {
        service.unbanAccount(accountId, connectedAccount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteAccount(
            @PathVariable Long accountId
    ) {
        service.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/makeAdmin/{accountId}")
    public ResponseEntity<?> makeAdmin(
            @PathVariable Long accountId,
            Principal connectedAccount
    ) {
        service.makeAdmin(accountId, connectedAccount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/removeAdmin/{accountId}")
    public ResponseEntity<?> removeAdmin(
            @PathVariable Long accountId,
            Principal connectedAccount
    ) {
        service.removeAdmin(accountId, connectedAccount);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AccountShortDto>> getAllAccounts() {
        return ResponseEntity.ok(service.getAllAccount());
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(service.getAccountById(accountId));
    }

    @PatchMapping("/update-image")
    public ResponseEntity<String> updateImage(
            @RequestBody AccountShortDto shortDto,
            Principal connectedAccount
    ){
        service.updateImage(shortDto, connectedAccount);
        return ResponseEntity.ok("Image updated successfully!");
    }
}
