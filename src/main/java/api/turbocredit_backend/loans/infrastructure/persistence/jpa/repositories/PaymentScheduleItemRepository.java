package api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories;

import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentScheduleItemRepository extends JpaRepository<PaymentScheduleItem, UUID> {
    List<PaymentScheduleItem> findByCreditIdOrderByPeriod(Long creditId);
    List<PaymentScheduleItem> findByUserIdAndIsPaidFalse(UUID userId);
}