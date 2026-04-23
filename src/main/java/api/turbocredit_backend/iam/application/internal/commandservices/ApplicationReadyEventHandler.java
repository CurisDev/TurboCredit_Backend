package api.turbocredit_backend.iam.application.internal.commandservices;

import api.turbocredit_backend.iam.domain.model.commands.SeedRolesCommand;
import api.turbocredit_backend.iam.domain.services.RoleCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationReadyEventHandler {

    private final RoleCommandService roleCommandService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);

    public ApplicationReadyEventHandler(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        logger.info("Seeding roles...");
        roleCommandService.handle(new SeedRolesCommand());
        logger.info("Roles seeded successfully.");
    }
}