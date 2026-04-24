package api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleCreditRepository extends JpaRepository<VehicleCredit, Long> {
    List<VehicleCredit> findByUserId(UUID userId);
}