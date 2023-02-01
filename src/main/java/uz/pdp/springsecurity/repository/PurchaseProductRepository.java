package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.PurchaseProduct;

import java.util.UUID;

public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, UUID> {
}
