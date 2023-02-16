package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.springsecurity.entity.Warehouse;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByBranchIdAndProductId(UUID branchId, UUID productId);


    Optional<Warehouse> findByBranchId(UUID branch_id);

    Optional<Warehouse> findByBranchIdAndProductTypePriceId(UUID branchId, UUID productTypePriceId);

    Optional<Warehouse> findByBranch_BusinessIdAndProductId(UUID branchId, UUID businessId);
    Optional<Warehouse> findByProductId(UUID productId);

    boolean existsByBranchIdAndProductId(UUID branchId, UUID productId);
}
