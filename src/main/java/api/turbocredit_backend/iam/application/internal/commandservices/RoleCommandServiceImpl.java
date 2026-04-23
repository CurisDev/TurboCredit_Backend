package api.turbocredit_backend.iam.application.internal.commandservices;

import api.turbocredit_backend.iam.domain.model.commands.SeedRolesCommand;
import api.turbocredit_backend.iam.domain.model.valueobjects.Role;
import api.turbocredit_backend.iam.domain.model.valueobjects.Roles;
import api.turbocredit_backend.iam.domain.services.RoleCommandService;
import api.turbocredit_backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(role));
            }
        });
    }
}