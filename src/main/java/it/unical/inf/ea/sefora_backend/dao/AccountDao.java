package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<Account, Long> {

    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);

    List<Account> findAllById(Iterable<Long> bySharedWithUsersId);
}
