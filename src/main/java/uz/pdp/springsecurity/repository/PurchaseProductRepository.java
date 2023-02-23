package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Purchase;
import uz.pdp.springsecurity.entity.PurchaseProduct;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, UUID> {
    List<PurchaseProduct> findAllByPurchaseId(UUID purchaseId);
    List<PurchaseProduct> findAllByPurchase_BranchId(UUID branchId);
    List<PurchaseProduct> findAllByCreatedAtBetweenAndProduct_BranchId(Timestamp from, Timestamp to, UUID branch_id);
    List<PurchaseProduct> findAllByPurchase_BranchIdAndPurchase_SupplierId(UUID purchase_branch_id, UUID purchase_supplier_id);
    List<PurchaseProduct> findAllByCreatedAtBetweenAndProduct_BranchIdAndPurchase_SupplierId(Timestamp from, Timestamp to, UUID product_branch_id, UUID purchase_supplier_id);
    List<PurchaseProduct> findAllByCreatedAtBetweenAndPurchase_BranchId(Timestamp startDate, Timestamp endDate, UUID purchase_branch_id);
}
