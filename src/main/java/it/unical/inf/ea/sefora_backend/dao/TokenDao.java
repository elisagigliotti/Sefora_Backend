package it.unical.inf.ea.sefora_backend.dao;

import it.unical.inf.ea.sefora_backend.entities.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenDao extends JpaRepository<Token, Long> {

    @Query(value = """
            select t from Token t inner join Account u\s
            on t.account.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByAccount(Long id);

    Optional<Token> findByToken(String token);

    Optional<Token> deleteByAccount_Id(Long id);
}
