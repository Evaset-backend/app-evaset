package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Address;
import uz.pdp.springsecurity.entity.Payment;
import uz.pdp.springsecurity.entity.Trade;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByTrade_BranchId(UUID branchId);
    List<Payment> findAllByCreatedAtBetweenAndTrade_BranchId(Timestamp start, Timestamp end, UUID trade_branch_id);
    List<Payment> findAllByPayMethod_BusinessId(UUID businessId);
    List<Payment> findAllByPayMethodId(UUID businessId);
    List<Payment> findAllByTradeId(UUID tradeId);
}
