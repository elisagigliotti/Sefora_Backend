package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAllById(Iterable<Long> bySharedWithUsersId);
}
