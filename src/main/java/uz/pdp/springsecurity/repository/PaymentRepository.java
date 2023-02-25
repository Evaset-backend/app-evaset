package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Address;
import uz.pdp.springsecurity.entity.Payment;
import uz.pdp.springsecurity.entity.Trade;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByTrade_BranchId(UUID branchId);
    List<Payment> findAllByPayMethod_BusinessId(UUID businessId);
    List<Payment> findAllByPayMethodId(UUID businessId);
    List<Payment> findAllByTradeId(UUID tradeId);
}
