package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

}
