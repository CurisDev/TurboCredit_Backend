package api.turbocredit_backend.loans.interfaces.rest.controllers;

import api.turbocredit_backend.loans.application.internal.commandservices.VehicleCreditCommandServiceImpl;
import api.turbocredit_backend.loans.application.internal.queryservices.VehicleCreditQueryServiceImpl;
import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.valueobjects.*;
import api.turbocredit_backend.loans.domain.services.RiskValidationService;
import api.turbocredit_backend.loans.interfaces.rest.resources.CreateVehicleCreditRequest;
import api.turbocredit_backend.loans.interfaces.rest.resources.VehicleCreditResource;
import api.turbocredit_backend.loans.interfaces.rest.transform.VehicleCreditDtoAssembler;
import api.turbocredit_backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import api.turbocredit_backend.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-credits")
@Tag(name = "Vehicle Credits", description = "Loan management endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class VehicleCreditController {

    private final VehicleCreditCommandServiceImpl vehicleCreditCommandService;
    private final VehicleCreditQueryServiceImpl vehicleCreditQueryService;
    private final RiskValidationService riskValidationService;

    @PostMapping("/request")
    @Operation(summary = "Request a new vehicle credit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<VehicleCreditResource> createVehicleCredit(
            @Valid @RequestBody CreateVehicleCreditRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        try {
            VehicleCredit credit = buildCreditFromRequest(request, userId);
            VehicleCredit savedCredit = vehicleCreditCommandService.createVehicleCredit(credit);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(VehicleCreditDtoAssembler.toResource(savedCredit));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{creditId}")
    @Operation(summary = "Edit and re-save an existing vehicle credit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<VehicleCreditResource> updateVehicleCredit(
            @PathVariable Long creditId,
            @Valid @RequestBody CreateVehicleCreditRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        try {
            VehicleCredit credit = buildCreditFromRequest(request, userId);
            VehicleCredit updated = vehicleCreditCommandService.updateVehicleCredit(creditId, credit, userId);
            return ResponseEntity.ok(VehicleCreditDtoAssembler.toResource(updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Construye el agregado VehicleCredit a partir del request, respetando el tipo de tasa
     * (efectiva/nominal) y persistiendo los resultados calculados en el cliente cuando se envían.
     */
    private VehicleCredit buildCreditFromRequest(CreateVehicleCreditRequest request, UUID userId) {
        InterestRate interestRate = "NOMINAL".equalsIgnoreCase(request.getRateType())
                ? InterestRate.nominal(request.getInterestRate())
                : InterestRate.effectiveAnnual(request.getInterestRate());

        VehicleCredit credit = new VehicleCredit(
                userId,
                request.getVehiclePrice(),
                request.getDownPayment(),
                request.getVehiclePrice().subtract(request.getDownPayment()),
                interestRate,
                request.getTermMonths(),
                request.getGracePeriodMonths(),
                GracePeriodType.valueOf(request.getGracePeriodType()),
                Currency.of(request.getCurrency()),
                request.getInsuranceCost()
        );

        // Datos adicionales del crédito vehicular
        credit.setClientName(request.getClientName());
        credit.setVehicleBrand(request.getVehicleBrand());
        credit.setVehicleModel(request.getVehicleModel());
        credit.setResidualPercentage(request.getResidualPercentage());
        credit.setSeguroDesgravamenRate(request.getSeguroDesgravamenRate());
        credit.setSeguroVehicularMonthly(request.getSeguroVehicularMonthly());
        credit.setPortes(request.getPortes());
        credit.setGastosAdministrativos(request.getGastosAdministrativos());
        credit.setComisionDesembolso(request.getComisionDesembolso());
        credit.setComisionEvaluacion(request.getComisionEvaluacion());
        credit.setCok(request.getCok());
        credit.setBankName(request.getBankName());

        // Resultados calculados en el frontend (fuente de verdad). Si se envían,
        // se persisten tal cual; el servicio omite el recálculo correspondiente.
        if (request.getFixedInstallment() != null) credit.setFixedInstallment(request.getFixedInstallment());
        if (request.getPrincipalFinanced() != null) credit.setPrincipalFinanced(request.getPrincipalFinanced());
        if (request.getResidualValue() != null) credit.setResidualValue(request.getResidualValue());
        if (request.getTotalInterestPaid() != null) credit.setTotalInterestPaid(request.getTotalInterestPaid());
        if (request.getTotalPaid() != null) credit.setTotalPaid(request.getTotalPaid());
        if (request.getNpv() != null) credit.setNpv(request.getNpv());
        if (request.getIrr() != null) credit.setIrr(request.getIrr());
        if (request.getTcea() != null) credit.setTcea(request.getTcea());

        // Validación de riesgo (solo si se envía el ingreso mensual)
        if (request.getMonthlyIncome() != null) {
            if (credit.getFixedInstallment() == null) {
                credit.calculateFixedInstallment();
            }
            riskValidationService.validateLoan(credit, request.getMonthlyIncome());
        }

        return credit;
    }

    @GetMapping("/{creditId}")
    @Operation(summary = "Get vehicle credit by ID")
    public ResponseEntity<VehicleCreditResource> getVehicleCredit(@PathVariable Long creditId) {
        var credit = vehicleCreditQueryService.findById(creditId);
        return credit.map(c -> ResponseEntity.ok(VehicleCreditDtoAssembler.toResource(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-all")
    @Operation(summary = "Get all vehicle credits for authenticated user")
    public ResponseEntity<?> getUserVehicleCredits(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        var credits = vehicleCreditQueryService.findByUserId(userId);
        var resources = credits.stream()
                .map(VehicleCreditDtoAssembler::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }
}