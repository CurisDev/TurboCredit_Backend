package api.turbocredit_backend.iam.infrastructure.hashing.bcrypt;

import api.turbocredit_backend.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {}