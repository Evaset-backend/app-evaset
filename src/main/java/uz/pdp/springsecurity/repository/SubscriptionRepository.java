package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Subscription;
import uz.pdp.springsecurity.enums.StatusTariff;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByBusinessIdAndActiveTrue(UUID BusinessId);
    List<Subscription> findAllByDeleteIsFalse();
    List<Subscription> findAllByBusiness_Id(UUID business_id);

    List<Subscription> findAllByCreatedAtAfterAndStatusTariff(Timestamp startTime, StatusTariff statusTariff);

    Integer countAllByStatusTariff(StatusTariff statusTariff);
}
