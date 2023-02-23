package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByBranchIdAndProductId(UUID branchId, UUID productId);


    Optional<Warehouse> findByBranchId(UUID branch_id);

    Optional<Warehouse> findByBranchIdAndProductTypePriceId(UUID branchId, UUID productTypePriceId);

    Optional<Warehouse> findByBranch_BusinessIdAndProductId(UUID branchId, UUID businessId);
    Optional<Warehouse> findByProductIdAndBranchId(UUID product_id, UUID branch_id);

    Optional<Warehouse> findByProduct_Id(UUID productId);
    List<Warehouse> findAllByProduct_Id(UUID productId);

    boolean existsByBranchIdAndProductId(UUID branchId, UUID productId);
    boolean existsByBranchIdAndProductTypePriceId(UUID branchId, UUID productTypePriceId);
}
