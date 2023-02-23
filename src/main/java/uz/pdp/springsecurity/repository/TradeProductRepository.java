package uz.pdp.springsecurity.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.TradeProduct;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface TradeProductRepository extends JpaRepository<TradeProduct, UUID> {
  List<TradeProduct> findAllByProduct_Id(UUID product_id);
  List<TradeProduct> findAllByProduct_IdAndTrade_CustomerId(UUID product_id, UUID trade_customer_id);
  List<TradeProduct> findAllByCreatedAtBetweenAndProductId(Timestamp startDate, Timestamp endDate, UUID product_id);
  List<TradeProduct> findAllByCreatedAtBetween(Timestamp createdAt, Timestamp createdAt2);
  List<TradeProduct> findAllByProduct_CategoryId(UUID categoryId);
  List<TradeProduct> findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp startDate, Timestamp endDate, UUID product_category_id);
  List<TradeProduct> findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp startDate, Timestamp endDate, UUID product_brand_id);
  List<TradeProduct> findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp startDate, Timestamp endDate, UUID trade_customer_id);
  List<TradeProduct> findAllByProduct_BrandId(UUID brandId);
  List<TradeProduct> findAllByTradeId(UUID tradeId);
  List<TradeProduct> findAllByProductBusiness_Id(UUID product_id);
  List<TradeProduct> findAllByProduct_BusinessId(UUID product_business_id);
  List<TradeProduct> findAllByProduct_BranchId(UUID product_branch_id);
  List<TradeProduct> findAllByCreatedAtBetweenAndProduct_BranchId(Timestamp startDate, Timestamp endDate, UUID product_branch_id);
  List<TradeProduct> findAllByTrade_CustomerId(UUID customerId);
  List<TradeProduct> findAllByCreatedAtBetweenAndProduct_BranchIdAndTrade_CustomerId(Timestamp from, Timestamp to, UUID branchId, UUID customerId);
  List<TradeProduct> findAllByProduct_Business_IdOrderByTradedQuantity(UUID product_business_id);

    TradeProduct findByProduct_Id(UUID key);

    List<TradeProduct> findAllByCreatedAtBetweenAndTrade_Customer_Id(Timestamp createdAt, Timestamp createdAt2, UUID trade_customer_id);
    List<TradeProduct> findAllByCreatedAtBetweenAndProduct_BusinessId(Timestamp createdAt, Timestamp createdAt2, UUID product_business_id);

    List<TradeProduct> findAllByProductTypePriceId(UUID productTypePrice_id);
    List<TradeProduct> findAllByProductTypePriceIdAndTrade_CustomerId(UUID productTypePrice_id, UUID trade_customer_id);
}

