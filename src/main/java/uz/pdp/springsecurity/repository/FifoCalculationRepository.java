package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.Address;
import uz.pdp.springsecurity.entity.FifoCalculation;

import java.util.List;
import java.util.UUID;

public interface FifoCalculationRepository extends JpaRepository<FifoCalculation, UUID> {
    List<FifoCalculation> findAllByBranchIdAndProductIdAndActiveTrueOrderByDateAscCreatedAtAsc(UUID branchId, UUID productId);

    List<FifoCalculation> findAllByBranchIdAndProductTypePriceIdAndActiveTrueOrderByDateAscCreatedAtAsc(UUID branchId, UUID productId);
}
