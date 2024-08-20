package it.unical.inf.ea.sefora_backend.utils.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();

        String email = (String) joinPoint.getArgs()[0];
        String action = auditable.action();
        String details = (String) joinPoint.getArgs()[1];

        auditLogService.log(email, action, details);

        return result;
    }
}

