package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.springsecurity.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByCategoryIdAndActiveTrue(UUID categoryId);
    Optional<Product> findByBarcodeAndBranchIdAndActiveTrue(String barcode, UUID branch_id);

    Optional<Product> findByBarcodeAndBranchIdAndActive(String barcode, UUID branch_id, boolean active);

    Optional<Product> findByNameAndBranchIdAndActiveTrue(String name,UUID branchId);

    void deleteByIdAndBranchId(UUID id, UUID branch_id);

    Optional<Product> findByIdAndBranchIdAndActiveTrue(UUID id, UUID branchId);

    Optional<Product> findAllByBarcodeAndBranchIdAndActiveTrue(String barcode, UUID branch_id);

    List<Product> findAllByCategoryIdAndBranchIdAndActiveTrue(UUID category_id, UUID branch_id);

    List<Product> findAllByBrandIdAndBranchIdAndActiveTrue(UUID brand_id, UUID branch_id);

    List<Product> findAllByBranchIdAndActiveIsTrue(UUID branch_id);

    List<Product> findAllByBranchIdAndBarcodeOrNameAndActiveTrue(UUID branch_id, String barcode, String name);

    List<Product> findAllByMeasurementIdAndBranchIdAndActiveTrue(UUID measurement_id, UUID branch_id);

    List<Product> findAllByNameAndBusinessId(String name, UUID business_id);


    @Query(value = "select * from product p inner join branches b on p.branch_id = b.id where b.business_id = ?1 AND b.id = ?2 AND p.active =true", nativeQuery = true)
    List<Product> findAllByBusinessIdAndBranchIdAndActiveTrue(@Param("businessId") UUID businessId, @Param("branchId") UUID branchId);//tekshirib korish kere

    Optional<Product> findByIdAndBranch_IdAndActiveTrue(UUID productExchangeId, UUID id);


    Optional<Product> findByBarcodeAndBranch_IdAndActiveTrue(String barcode, UUID receivedBranch);

    List<Product> findAllByBusiness_IdAndActiveTrue(UUID businessId);
    List<Product> findAllByBranchIdAndActiveTrue(UUID branch_id);

}
