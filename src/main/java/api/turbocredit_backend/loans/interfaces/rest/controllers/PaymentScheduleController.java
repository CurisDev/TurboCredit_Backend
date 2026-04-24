package api.turbocredit_backend.loans.interfaces.rest.controllers;

import api.turbocredit_backend.loans.application.internal.queryservices.VehicleCreditQueryServiceImpl;
import api.turbocredit_backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import api.turbocredit_backend.loans.interfaces.rest.resources.AmortizationScheduleResource;
import api.turbocredit_backend.loans.interfaces.rest.resources.PaymentScheduleItemResource;
import api.turbocredit_backend.loans.interfaces.rest.transform.VehicleCreditDtoAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payment-schedules")
@Tag(name = "Payment Schedules", description = "Payment schedule endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PaymentScheduleController {

    private final VehicleCreditQueryServiceImpl vehicleCreditQueryService;

    @GetMapping("/credit/{creditId}")
    @Operation(summary = "Get payment schedule for a credit")
    public ResponseEntity<AmortizationScheduleResource> getPaymentSchedule(
            @PathVariable Long creditId) {

        var credit = vehicleCreditQueryService.findById(creditId);
        if (credit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var scheduleItems = vehicleCreditQueryService.getPaymentSchedule(creditId);
        var resource = VehicleCreditDtoAssembler.toAmortizationResource(credit.get(), scheduleItems);

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending payment schedule items for authenticated user")
    public ResponseEntity<List<PaymentScheduleItemResource>> getPendingPayments(
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        // Este método requiere una consulta personalizada en el repositorio
        // que retorne solo los items pendientes del usuario

        return ResponseEntity.ok(List.of());
    }
}