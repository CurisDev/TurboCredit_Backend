package api.turbocredit_backend.loans.interfaces.rest.controllers;

import api.turbocredit_backend.loans.application.internal.commandservices.VehicleCreditCommandServiceImpl;
import api.turbocredit_backend.loans.application.internal.queryservices.VehicleCreditQueryServiceImpl;
import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.valueobjects.*;
import api.turbocredit_backend.loans.domain.services.RiskValidationService;
import api.turbocredit_backend.loans.interfaces.rest.resources.CreateVehicleCreditRequest;
import api.turbocredit_backend.loans.interfaces.rest.resources.VehicleCreditResource;
import api.turbocredit_backend.loans.interfaces.rest.transform.VehicleCreditDtoAssembler;
import api.turbocredit_backend.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-credits")
@Tag(name = "Vehicle Credits", description = "Loan management endpoints")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class VehicleCreditController {

    private final VehicleCreditCommandServiceImpl vehicleCreditCommandService;
    private final VehicleCreditQueryServiceImpl vehicleCreditQueryService;
    private final RiskValidationService riskValidationService;

    @PostMapping
    @Operation(summary = "Create a new vehicle credit")
    public ResponseEntity<VehicleCreditResource> createVehicleCredit(
            @Valid @RequestBody CreateVehicleCreditRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {
            var credit = new api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit(
                    userId,
                    request.getVehiclePrice(),
                    request.getDownPayment(),
                    request.getVehiclePrice().subtract(request.getDownPayment()),
                    InterestRate.effectiveAnnual(request.getInterestRate()),
                    request.getTermMonths(),
                    request.getGracePeriodMonths(),
                    GracePeriodType.valueOf(request.getGracePeriodType()),
                    Currency.of(request.getCurrency()),
                    request.getInsuranceCost()
            );

            // Validar riesgo
            if (request.getMonthlyIncome() != null) {
                riskValidationService.validateLoan(credit, request.getMonthlyIncome());
            }

            credit.setBankName(request.getBankName());
            VehicleCredit savedCredit = vehicleCreditCommandService.createVehicleCredit(credit);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(VehicleCreditDtoAssembler.toResource(savedCredit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{creditId}")
    @Operation(summary = "Get vehicle credit by ID")
    public ResponseEntity<VehicleCreditResource> getVehicleCredit(@PathVariable Long creditId) {
        var credit = vehicleCreditQueryService.findById(creditId);
        return credit.map(c -> ResponseEntity.ok(VehicleCreditDtoAssembler.toResource(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all vehicle credits for authenticated user")
    public ResponseEntity<?> getUserVehicleCredits(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        var credits = vehicleCreditQueryService.findByUserId(userId);
        var resources = credits.stream()
                .map(VehicleCreditDtoAssembler::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{creditId}/approve")
    @Operation(summary = "Approve a vehicle credit")
    public ResponseEntity<VehicleCreditResource> approveCredit(@PathVariable Long creditId) {
        var credit = vehicleCreditCommandService.approveLoan(creditId);
        return ResponseEntity.ok(VehicleCreditDtoAssembler.toResource(credit));
    }

    @PutMapping("/{creditId}/activate")
    @Operation(summary = "Activate a vehicle credit")
    public ResponseEntity<VehicleCreditResource> activateCredit(@PathVariable Long creditId) {
        var credit = vehicleCreditCommandService.activateLoan(creditId);
        return ResponseEntity.ok(VehicleCreditDtoAssembler.toResource(credit));
    }
}