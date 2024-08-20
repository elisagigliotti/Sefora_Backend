package it.unical.inf.ea.sefora_backend.utils.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogDao extends JpaRepository<AuditLog, Long> {
}
