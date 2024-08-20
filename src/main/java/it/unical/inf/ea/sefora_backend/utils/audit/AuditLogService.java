package it.unical.inf.ea.sefora_backend.utils.audit;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    private final AuditLogDao auditLogDao;

    public AuditLogService(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    public void log(String email, String action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEmail(email);
        auditLog.setAction(action);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);

        auditLogDao.save(auditLog);
    }
}
