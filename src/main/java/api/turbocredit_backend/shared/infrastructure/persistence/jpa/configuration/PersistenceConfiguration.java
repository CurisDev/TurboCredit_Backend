package api.turbocredit_backend.shared.infrastructure.persistence.jpa.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "api.turbocredit_backend.iam.infrastructure.persistence.jpa.repositories",
                "api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories",
                "api.turbocredit_backend.profiles.infrastructure.persistence.jpa.repositories"
        }
)
public class PersistenceConfiguration {
    // Configuración centralizada de JPA Repositories
}