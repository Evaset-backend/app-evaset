package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.ProductTypePrice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductTypePriceRepository extends JpaRepository<ProductTypePrice, UUID> {
    List<ProductTypePrice> findAllByProductId(UUID product_id);
    List<ProductTypePrice> findAllByProduct_BranchIdAndProduct_ActiveIsTrue(UUID product_branch_id);

    List<ProductTypePrice> findAllByProduct_Category_IdAndProduct_Branch_IdAndProduct_ActiveIsTrue(UUID product_category_id, UUID product_branch_id);
    List<ProductTypePrice> findAllByProduct_Brand_IdAndProduct_Branch_IdAndProduct_ActiveIsTrue(UUID product_brand_id, UUID product_branch_id);


    Optional<ProductTypePrice> findByProductId(UUID product_id);

    boolean existsByBarcodeAndProduct_BusinessId(String barcode, UUID businessId);
    boolean existsByBarcodeAndProduct_BusinessIdAndIdIsNot(String barcode, UUID businessId, UUID productTypePriceId);
}
