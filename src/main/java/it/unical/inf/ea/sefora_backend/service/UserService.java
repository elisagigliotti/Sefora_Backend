package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.UserDao;
import it.unical.inf.ea.sefora_backend.entities.ChangePasswordRequest;
import it.unical.inf.ea.sefora_backend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserDao dao;

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
}
