package uz.pdp.springsecurity.service;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportsService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductionRepository productionRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    BranchRepository branchRepository;
    @Autowired
    TradeProductRepository tradeProductRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    PurchaseProductRepository purchaseProductRepository;
    @Autowired
    OutlayRepository outlayRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    CustomerRepository customerRepository;

    private final static Date date = new Date();
    private final static Timestamp currentDay = new Timestamp(date.getTime());
    private final static Timestamp enDate = new Timestamp(date.getTime());
    private final static LocalDateTime dateTime = enDate.toLocalDateTime();
    private final static LocalDateTime LAST_MONTH = dateTime.minusMonths(1);
    private final static LocalDate localDate = LocalDate.now();
    private final static LocalDateTime THIS_MONTH = localDate.withDayOfMonth(1).atStartOfDay();
    private final static LocalDate WEEK_START_DAY = localDate.minusDays(7 + localDate.getDayOfWeek().getValue() - 1);
    private final static LocalDate WEEK_END_DAY = localDate.minusDays(7 + localDate.getDayOfWeek().getValue() - 7);
    private final static LocalDate TEMP_START_OF_YEAR = LocalDate.of(localDate.getYear() - 1, 1, 1);
    private final static LocalDate TEMP_FOR_THIS_START_OF_YEAR = LocalDate.of(localDate.getYear(), 1, 1);
    private final static LocalDate TEMP_START_OF_DAY = localDate.minusDays(1);
    private final static LocalDate TEMP_END_OF_DAY = LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
    private final static LocalDate TEMP_END_OF_YEAR = LocalDate.of(localDate.getYear() - 1, 12, 31);
    private final static LocalDate TEMP_START_OF_MONTH_ONE = LocalDate.of(localDate.getYear(), localDate.getMonth().getValue(), 1);
    private final static LocalDate TEMP_START_OF_MONTH = TEMP_START_OF_MONTH_ONE.minusMonths(1);
    private final static LocalDate TEMP_END_OF_MONTH = LocalDate.of(localDate.getYear(), TEMP_START_OF_MONTH.getMonth(), TEMP_START_OF_MONTH.lengthOfMonth());
    private final static LocalDateTime START_OF_YEAR = TEMP_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_YEAR_FOR_THIS = TEMP_FOR_THIS_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime END_OF_YEAR = TEMP_END_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_MONTH = TEMP_START_OF_MONTH.atStartOfDay();
    private final static LocalDateTime END_OF_MONTH = TEMP_END_OF_MONTH.atStartOfDay();
    private final static LocalDateTime START_OF_DAY = TEMP_START_OF_DAY.atStartOfDay();
    private final static LocalDateTime END_OF_DAY = TEMP_END_OF_DAY.atStartOfDay();

    public ApiResponse allProductAmount(UUID branchId,UUID brandId,UUID categoryId,String production) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }

        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }
        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList =new ArrayList<>();
        if (categoryId == null && branchId != null && production == null && brandId == null){
            productList = productRepository.findAllByBranchIdAndActiveIsTrue(branchId);
            if (productList.isEmpty()) {
                return new ApiResponse("No Found Products");
            }
        } else if (categoryId != null && brandId == null) {
            productList = productRepository.findAllByCategoryIdAndBranchIdAndActiveTrue(categoryId,branchId);
        } else if (categoryId == null && production == null && brandId != null) {
            productList = productRepository.findAllByBrandIdAndBranchIdAndActiveTrue(brandId,branchId);
        }else if (production != null && categoryId != null && brandId != null) {
            List<Production> productionList = productionRepository.findAllByProduct_CategoryIdAndProduct_BrandIdAndProduct_BranchId(categoryId, brandId, branchId);
            if (productionList.isEmpty()){
                return new ApiResponse("Production Not Found",false);
            }
            List<Product> products =new ArrayList<>();
            for (Production productions : productionList) {
                Optional<Product> optionalProduct = productRepository.findById(productions.getProduct().getId());
                products.add(optionalProduct.get());
            }
            productList = products;
        } else if (production != null && categoryId != null && brandId == null) {
            List<Production> productionList = productionRepository.findAllByProduct_CategoryIdAndProduct_BranchId(categoryId, branchId);
            if (productionList.isEmpty()){
                return new ApiResponse("Production Not Found",false);
            }
            List<Product> products =new ArrayList<>();
            for (Production productions : productionList) {
                Optional<Product> optionalProduct = productRepository.findById(productions.getProduct().getId());
                products.add(optionalProduct.get());
            }
            productList = products;
        } else if (production != null && categoryId == null && brandId != null) {
            List<Production> productionList = productionRepository.findAllByProduct_BrandIdAndProduct_BranchId(categoryId, branchId);
            if (productionList.isEmpty()){
                return new ApiResponse("Production Not Found",false);
            }
            List<Product> products =new ArrayList<>();
            for (Production productions : productionList) {
                Optional<Product> optionalProduct = productRepository.findById(productions.getProduct().getId());
                products.add(optionalProduct.get());
            }
            productList = products;
        } else if (brandId != null && categoryId != null) {
            productList = productRepository.findAllByBrandIdAndCategoryIdAndBranchIdAndActiveTrue(brandId,categoryId,branchId);
        }else if (production != null && categoryId == null && brandId == null){
            List<Production> productionList = productionRepository.findAllByBranchId(branchId);
            if (productionList.isEmpty()){
                return new ApiResponse("Production Not Found",false);
            }
            List<Product> products =new ArrayList<>();
            for (Production productions : productionList) {
                Optional<Product> optionalProduct = productRepository.findById(productions.getProduct().getId());
                products.add(optionalProduct.get());
            }
            productList = products;
        }


        double SumBySalePrice = 0;
        double SumByBuyPrice = 0;

        List<ProductReportDto> productReportDtoList = new ArrayList<>();
        ProductReportDto productReportDto = new ProductReportDto();
        for (Product product : productList) {
            productReportDto = new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(optionalBranch.get().getName());
            productReportDto.setCategory(product.getCategory().getName());
            productReportDto.setBuyPrice(product.getBuyPrice());
            productReportDto.setSalePrice(product.getSalePrice());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(), optionalBranch.get().getId());
            Warehouse warehouse = new Warehouse();
            if (optionalWarehouse.isPresent()) {
                warehouse = optionalWarehouse.get();
            }
            productReportDto.setAmount(warehouse.getAmount());

            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();

            SumBySalePrice = amount * salePrice;
            SumByBuyPrice = amount * buyPrice;
            productReportDto.setSumBySalePrice(SumBySalePrice);
            productReportDto.setSumByBuyPrice(SumByBuyPrice);
            productReportDtoList.add(productReportDto);
        }
        productReportDtoList.sort(Comparator.comparing(ProductReportDto::getAmount).reversed());
        return new ApiResponse("Business Products Amount", true, productReportDtoList);
    }
    public ApiResponse tradeProductByBranch(UUID branchId,UUID payMethodId,UUID customerId,Date startDate,Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }

        List<TradeProduct> tradeProductList =new ArrayList<>();
        if (payMethodId == null && customerId == null && startDate == null && endDate == null){
            tradeProductList = tradeProductRepository.findAllByTrade_BranchId(branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        } else if (payMethodId != null && customerId == null && startDate == null && endDate == null) {
            tradeProductList = tradeProductRepository.findAllByTrade_PayMethodIdAndTrade_BranchId(payMethodId,branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        }else if (payMethodId != null && customerId == null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            tradeProductList = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_PayMethodIdAndTrade_BranchId(from,to,payMethodId,branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        }else if (customerId != null && payMethodId == null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            tradeProductList = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerIdAndTrade_BranchId(from,to,customerId,branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        } else if (customerId == null && payMethodId == null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            tradeProductList = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_BranchId(from,to,branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        } else if (payMethodId == null && customerId != null && startDate == null && endDate == null) {
            tradeProductList = tradeProductRepository.findAllByTrade_CustomerIdAndTrade_BranchId(customerId,branchId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        } else if (customerId != null && payMethodId != null && startDate == null && endDate == null) {
            tradeProductList = tradeProductRepository.findAllByTrade_CustomerIdAndTrade_BranchIdAndTrade_PayMethodId(customerId,branchId,payMethodId);
            if (tradeProductList.isEmpty()){
                return new ApiResponse("Trade Not Found",false);
            }
        }

        List<TradeReportsDto> tradeReportsDtoList = new ArrayList<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            TradeReportsDto tradeReportsDto = new TradeReportsDto();
            tradeReportsDto.setTradeProductId(tradeProduct.getTrade().getId());
            tradeReportsDto.setName(tradeProduct.getProduct().getName());
            tradeReportsDto.setBarcode(tradeProduct.getProduct().getBarcode());
            tradeReportsDto.setTradedDate(tradeProduct.getTrade().getPayDate());
            tradeReportsDto.setCustomerName(tradeProduct.getTrade().getCustomer().getName());
            tradeReportsDto.setPayMethod(tradeProduct.getTrade().getPayMethod().getType());
            tradeReportsDto.setAmount(tradeProduct.getTradedQuantity());
            tradeReportsDto.setDiscount(tradeProduct.getTrade().getCustomer().getCustomerGroup().getPercent());
            tradeReportsDto.setTotalSum(tradeProduct.getTotalSalePrice());
            tradeReportsDto.setSalePrice(tradeProduct.getProduct().getSalePrice());
            tradeReportsDtoList.add(tradeReportsDto);
        }
        tradeReportsDtoList.sort(Comparator.comparing(TradeReportsDto::getAmount).reversed());
        return new ApiResponse("Traded Products", true, tradeReportsDtoList);
    }

    public ApiResponse allProductByBrand(UUID branchId, UUID brandId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        if (optionalBrand.isEmpty()) {
            return new ApiResponse("Brand Not Found");
        }

        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }
        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByBrandIdAndBusinessIdAndActiveTrue(brandId, businessId);

        if (productList.isEmpty()) {
            return new ApiResponse("No Found Products");
        }

        double SumBySalePrice = 0;
        double SumByBuyPrice = 0;

        List<ProductReportDto> productReportDtoList = new ArrayList<>();
        ProductReportDto productReportDto = new ProductReportDto();
        for (Product product : productList) {
            productReportDto = new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(optionalBranch.get().getName());
            productReportDto.setCategory(product.getCategory().getName());
            productReportDto.setBuyPrice(product.getBuyPrice());
            productReportDto.setSalePrice(product.getSalePrice());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(), optionalBranch.get().getId());
            Warehouse warehouse = new Warehouse();
            if (optionalWarehouse.isPresent()) {
                warehouse = optionalWarehouse.get();
            }
            productReportDto.setAmount(warehouse.getAmount());

            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();

            SumBySalePrice = amount * salePrice;
            SumByBuyPrice = amount * buyPrice;
            productReportDto.setSumBySalePrice(SumBySalePrice);
            productReportDto.setSumByBuyPrice(SumByBuyPrice);
            productReportDtoList.add(productReportDto);
        }
        productReportDtoList.sort(Comparator.comparing(ProductReportDto::getAmount).reversed());
        return new ApiResponse("Business Products Amount", true, productReportDtoList);
    }

    public ApiResponse allProductByCategory(UUID branchId, UUID categoryId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            return new ApiResponse("Category Not Found");
        }
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }
        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByCategoryIdAndBusinessIdAndActiveTrue(categoryId, businessId);

        if (productList.isEmpty()) {
            return new ApiResponse("No Found Products");
        }

        double SumBySalePrice = 0;
        double SumByBuyPrice = 0;

        List<ProductReportDto> productReportDtoList = new ArrayList<>();
        ProductReportDto productReportDto = new ProductReportDto();
        for (Product product : productList) {
            productReportDto = new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(optionalBranch.get().getName());
            productReportDto.setCategory(product.getCategory().getName());
            productReportDto.setBuyPrice(product.getBuyPrice());
            productReportDto.setSalePrice(product.getSalePrice());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(), optionalBranch.get().getId());
            Warehouse warehouse = new Warehouse();
            if (optionalWarehouse.isPresent()) {
                warehouse = optionalWarehouse.get();
            }
            productReportDto.setAmount(warehouse.getAmount());

            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();

            SumBySalePrice = amount * salePrice;
            SumByBuyPrice = amount * buyPrice;
            productReportDto.setSumBySalePrice(SumBySalePrice);
            productReportDto.setSumByBuyPrice(SumByBuyPrice);
            productReportDtoList.add(productReportDto);
        }
        productReportDtoList.sort(Comparator.comparing(ProductReportDto::getAmount).reversed());
        return new ApiResponse("Business Products Amount", true, productReportDtoList);
    }

    public ApiResponse allProductAmountByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<Product> productList = productRepository.findAllByBranchIdAndActiveTrue(optionalBranch.get().getId());

        if (productList.isEmpty()) {
            return new ApiResponse("No Found Products");
        }
        double totalSumBySalePrice = 0D;
        double totalSumByBuyPrice = 0D;
        Amount amounts = new Amount();
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(), optionalBranch.get().getId());
            if (optionalWarehouse.isPresent()) {
                double amount = optionalWarehouse.get().getAmount();
                double salePrice = product.getSalePrice();
                double buyPrice = product.getBuyPrice();

                totalSumBySalePrice += amount * salePrice;
                totalSumByBuyPrice += amount * buyPrice;
                amounts.setTotalSumBySalePrice(totalSumBySalePrice);
                amounts.setTotalSumByBuyPrice(totalSumByBuyPrice);
            }

        }
        return new ApiResponse("Business Products Amount", true, amounts);
    }


    public ApiResponse mostUnSaleProducts(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found");
        }
        Business business = optionalBranch.get().getBusiness();
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BusinessId(business.getId());

        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found");
        }

        Map<UUID, Double> productAmount = new HashMap<>();


        for (TradeProduct tradeProduct : tradeProductList) {
            List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_Id(tradeProduct.getProduct().getId());
            double amount = 0;
            if (tradeProduct.getProduct() != null) {
                for (TradeProduct product : allByProductId) {
                    amount += product.getTradedQuantity();
                    productAmount.put(product.getProduct().getId(), amount);
                }
            } else {
                for (TradeProduct product : allByProductId) {
                    amount += product.getTradedQuantity();
                    productAmount.put(product.getProductTypePrice().getId(), amount);
                }
            }
        }

        List<MostSaleProductsDto> mostSaleProductsDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            MostSaleProductsDto mostSaleProductsDto = new MostSaleProductsDto();
            Optional<Product> product = productRepository.findById(entry.getKey());
            mostSaleProductsDto.setName(product.get().getName());
            mostSaleProductsDto.setAmount(entry.getValue());
            mostSaleProductsDto.setSalePrice(product.get().getSalePrice());
            mostSaleProductsDto.setBuyPrice(product.get().getBuyPrice());
            mostSaleProductsDto.setBarcode(product.get().getBarcode());
            mostSaleProductsDto.setMeasurement(product.get().getMeasurement().getName());
            mostSaleProductsDto.setBranchName(product.get().getBranch().get(0).getName());
            mostSaleProductsDtoList.add(mostSaleProductsDto);
        }
        mostSaleProductsDtoList.sort(Comparator.comparing(MostSaleProductsDto::getAmount));
        return new ApiResponse("Found", true, mostSaleProductsDtoList);
    }

    public ApiResponse findByName(UUID branchId, String name) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Not Found");
        }
        Business business = optionalBranch.get().getBusiness();
        List<Product> productList = productRepository.findAllByNameAndBusinessId(name, business.getId());
        if (productList.isEmpty()) {
            return new ApiResponse("Not Found", false);
        }
        return new ApiResponse("Found", true, productList);
    }

    public ApiResponse purchaseReports(UUID branchId,UUID supplierId,Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found",false);
        }
        List<PurchaseProduct> purchaseProductList = new ArrayList<>();
        Branch branch = optionalBranch.get();
        if (supplierId == null && startDate == null && endDate == null){
            purchaseProductList = purchaseProductRepository.findAllByPurchase_BranchId(branch.getId());
            if (purchaseProductList.isEmpty()){
                return new ApiResponse("Purchase Not Found",false);
            }
        } else if (supplierId != null && startDate == null && endDate == null) {
            purchaseProductList = purchaseProductRepository.findAllByPurchase_BranchIdAndPurchase_SupplierId(branchId,supplierId);
            if (purchaseProductList.isEmpty()){
                return new ApiResponse("Purchase Not Found",false);
            }
        } else if (supplierId == null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            purchaseProductList = purchaseProductRepository.findAllByCreatedAtBetweenAndPurchase_BranchId(from,to,branchId);
            if (purchaseProductList.isEmpty()){
                return new ApiResponse("Purchase Not Found",false);
            }
        }

        List<PurchaseReportsDto> purchaseReportsDtoList = new ArrayList<>();
        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            PurchaseReportsDto purchaseReportsDto = new PurchaseReportsDto();
            purchaseReportsDto.setPurchaseId(purchaseProduct.getPurchase().getId());
            purchaseReportsDto.setPurchasedAmount(purchaseProduct.getPurchasedQuantity());
            purchaseReportsDto.setName(purchaseProduct.getProduct().getName());
            purchaseReportsDto.setBuyPrice(purchaseProduct.getBuyPrice());
            purchaseReportsDto.setBarcode(purchaseProduct.getProduct().getBarcode());
            purchaseReportsDto.setTax(purchaseProduct.getProduct().getTax());
            purchaseReportsDto.setTotalSum(purchaseProduct.getTotalSum());
            purchaseReportsDto.setPurchasedDate(purchaseProduct.getCreatedAt());
            purchaseReportsDto.setSupplier(purchaseProduct.getPurchase().getSupplier().getName());
            purchaseReportsDto.setDebt(purchaseProduct.getPurchase().getDebtSum());
            purchaseReportsDtoList.add(purchaseReportsDto);
        }
        return new ApiResponse("Found", true, purchaseReportsDtoList);
    }

    public ApiResponse deliveryPriceGet(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Not Found");
        }

        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);

        if (purchaseList.isEmpty()) {
            return new ApiResponse("Not Found Purchase", false);
        }

        double totalDelivery = 0;
        for (Purchase purchase : purchaseList) {
            totalDelivery += purchase.getDeliveryPrice();
        }
        return new ApiResponse("Found", true, totalDelivery);
    }

    public ApiResponse outlayReports(UUID branchId, UUID categoryId, Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Not Found",false);
        }
        List<Outlay> outlayList = new ArrayList<>();
        if (categoryId == null && startDate == null && endDate == null) {
            outlayList = outlayRepository.findAllByBranch_Id(branchId);
            if (outlayList.isEmpty()) {
                return new ApiResponse("Not Found Outlay",false);
            }
        } else if (categoryId != null && startDate == null && endDate == null) {
            outlayList = outlayRepository.findAllByBranch_IdAndOutlayCategoryId(branchId, categoryId);
            if (outlayList.isEmpty()) {
                return new ApiResponse("Not Found Outlay",false);
            }
        } else if (categoryId != null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranchIdAndOutlayCategoryId(from, to, branchId, categoryId);
            if (outlayList.isEmpty()) {
                return new ApiResponse("Not Found Outlay",false);
            }
        } else if (categoryId == null && startDate != null && endDate != null) {
            Timestamp from = new Timestamp(startDate.getTime());
            Timestamp to = new Timestamp(endDate.getTime());
            outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranchId(from,to,branchId);
            if (outlayList.isEmpty()){
                return new ApiResponse("Not Found Outlay" ,false);
            }
        }

        outlayList.sort(Comparator.comparing(Outlay::getTotalSum));
        return new ApiResponse("Found", true, outlayList);
    }

    public ApiResponse customerReports(UUID branchId, UUID customerId, Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Not Found");
        }
        List<TradeProduct> tradeProducts = tradeProductRepository.findAllByProduct_BranchId(branchId);
        if (tradeProducts.isEmpty()) {
            return new ApiResponse("Traded Product Not Found", false);
        }
        List<CustomerReportsDto> customerReportsDtoList = new ArrayList<>();


        if (startDate != null && endDate != null) {
            Timestamp to = new Timestamp(startDate.getTime());
            Timestamp from = new Timestamp(endDate.getTime());
            List<TradeProduct> tradeProductList = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BranchId(to,from,branchId);
            if (tradeProductList.isEmpty()) {
                return new ApiResponse("Not Found Purchase", false);
            }
            for (TradeProduct tradeProduct : tradeProductList) {
                CustomerReportsDto customerReportsDto = new CustomerReportsDto();
                customerReportsDto.setCustomerName(tradeProduct.getTrade().getCustomer().getName());
                customerReportsDto.setDate(tradeProduct.getTrade().getPayDate());
                customerReportsDto.setDebt(tradeProduct.getTrade().getDebtSum());
                customerReportsDto.setProduct(tradeProduct.getProduct().getName());
                customerReportsDto.setPaidSum(tradeProduct.getTrade().getPaidSum());
                customerReportsDto.setTradedQuantity(tradeProduct.getTradedQuantity());
                customerReportsDto.setBranchName(tradeProduct.getTrade().getBranch().getName());
                customerReportsDto.setTotalSum(tradeProduct.getTrade().getTotalSum());
                customerReportsDto.setPayMethod(tradeProduct.getTrade().getPayMethod().getType());
                customerReportsDto.setPaymentStatus(tradeProduct.getTrade().getPaymentStatus().getStatus());
                customerReportsDtoList.add(customerReportsDto);
            }
        } else if (customerId != null) {
            List<TradeProduct> tradeProductList = tradeProductRepository.findAllByTrade_CustomerId(customerId);
            if (tradeProductList.isEmpty()) {
                return new ApiResponse("Not Found Purchase With This Customer", false);
            }
            for (TradeProduct tradeProduct : tradeProductList) {
                CustomerReportsDto customerReportsDto = new CustomerReportsDto();
                customerReportsDto.setCustomerName(tradeProduct.getTrade().getCustomer().getName());
                customerReportsDto.setDate(tradeProduct.getTrade().getPayDate());
                customerReportsDto.setDebt(tradeProduct.getTrade().getDebtSum());
                customerReportsDto.setProduct(tradeProduct.getProduct().getName());
                customerReportsDto.setPaidSum(tradeProduct.getTrade().getPaidSum());
                customerReportsDto.setTradedQuantity(tradeProduct.getTradedQuantity());
                customerReportsDto.setBranchName(tradeProduct.getTrade().getBranch().getName());
                customerReportsDto.setTotalSum(tradeProduct.getTrade().getTotalSum());
                customerReportsDto.setPayMethod(tradeProduct.getTrade().getPayMethod().getType());
                customerReportsDto.setPaymentStatus(tradeProduct.getTrade().getPaymentStatus().getStatus());
                customerReportsDtoList.add(customerReportsDto);
            }
        } else {
            List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(branchId);
            if (tradeProductList.isEmpty()) {
                return new ApiResponse("Not Found Purchase", false);
            }
            for (TradeProduct tradeProduct : tradeProductList) {
                CustomerReportsDto customerReportsDto = new CustomerReportsDto();
                customerReportsDto.setCustomerName(tradeProduct.getTrade().getCustomer().getName());
                customerReportsDto.setDate(tradeProduct.getTrade().getPayDate());
                customerReportsDto.setDebt(tradeProduct.getTrade().getDebtSum());
                customerReportsDto.setProduct(tradeProduct.getProduct().getName());
                customerReportsDto.setPaidSum(tradeProduct.getTrade().getPaidSum());
                customerReportsDto.setTradedQuantity(tradeProduct.getTradedQuantity());
                customerReportsDto.setBranchName(tradeProduct.getTrade().getBranch().getName());
                customerReportsDto.setTotalSum(tradeProduct.getTrade().getTotalSum());
                customerReportsDto.setPayMethod(tradeProduct.getTrade().getPayMethod().getType());
                customerReportsDto.setPaymentStatus(tradeProduct.getTrade().getPaymentStatus().getStatus());
                customerReportsDtoList.add(customerReportsDto);
            }
        }
        customerReportsDtoList.sort(Comparator.comparing(CustomerReportsDto::getTotalSum).reversed());

        return new ApiResponse("Found", true, customerReportsDtoList);

    }

    public ApiResponse mostSaleProducts(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found");
        }
        Business business = optionalBranch.get().getBusiness();
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BusinessId(business.getId());

        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found");
        }

        Map<UUID, Double> productAmount = new HashMap<>();


        for (TradeProduct tradeProduct : tradeProductList) {
            List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_Id(tradeProduct.getProduct().getId());
            double amount = 0;
            for (TradeProduct product : allByProductId) {
                amount += product.getTradedQuantity();
                productAmount.put(product.getProduct().getId(), amount);
            }
        }

        List<MostSaleProductsDto> mostSaleProductsDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            MostSaleProductsDto mostSaleProductsDto = new MostSaleProductsDto();
            Optional<Product> product = productRepository.findById(entry.getKey());
            mostSaleProductsDto.setName(product.get().getName());
            mostSaleProductsDto.setAmount(entry.getValue());
            mostSaleProductsDto.setSalePrice(product.get().getSalePrice());
            mostSaleProductsDto.setBuyPrice(product.get().getBuyPrice());
            mostSaleProductsDto.setBarcode(product.get().getBarcode());
            mostSaleProductsDto.setMeasurement(product.get().getMeasurement().getName());
            mostSaleProductsDto.setBranchName(optionalBranch.get().getName());
            mostSaleProductsDtoList.add(mostSaleProductsDto);
        }
        mostSaleProductsDtoList.sort(Comparator.comparing(MostSaleProductsDto::getAmount).reversed());
        return new ApiResponse("Found", true, mostSaleProductsDtoList);
    }

    public ApiResponse dateBenefitAndLostByProductReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found", false);
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()), Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Week", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(start, end, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For This Date", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            } else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_Id(tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }
        }
        List<ProfitByProductDto> profitByProductDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByProductDto profitByProductDto = new ProfitByProductDto();
            Optional<Product> optionalProduct = productRepository.findById(entry.getKey());
            profitByProductDto.setName(optionalProduct.get().getName());
            profitByProductDto.setProfit(entry.getValue());
            profitByProductDtoList.add(profitByProductDto);
        }
        profitByProductDtoList.sort(Comparator.comparing(ProfitByProductDto::getProfit).reversed());

        return new ApiResponse("Found", true, profitByProductDtoList);
    }

    public ApiResponse benefitAndLostByCategoryReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found", false);
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()), Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(start, end, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For This Date", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            } else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_CategoryId(tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto = new ProfitByCategoryDto();
            Optional<Category> optionalCategory = categoryRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalCategory.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found", true, profitByCategoryDtoList);
    }

    public ApiResponse benefitAndLostByBrandReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found", false);
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()), Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(start, end, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For This Date", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            } else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_BrandId(tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto = new ProfitByCategoryDto();
            Optional<Brand> optionalBrand = brandRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalBrand.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found", true, profitByCategoryDtoList);
    }

    public ApiResponse benefitAndLostByCustomerReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()) {
            return new ApiResponse("Traded Product Not Found", false);
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()), Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getProduct().getBrand().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(start, end, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For This Date", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            } else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByTrade_CustomerId(tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day", false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto = new ProfitByCategoryDto();
            Optional<Customer> optionalCustomer = customerRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalCustomer.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found", true, profitByCategoryDtoList);
    }

    public ApiResponse benefitAndLostByOneDateReports(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long aLong = timestamp.getTime();
        long bLong = timestamp.getTime() - 86400000;
        Date startDate = new Date(aLong);
        Date endDate = new Date(bLong);
        List<Trade> tradeList = tradeRepository.findAllByPayDateBetween(endDate, startDate);
        for (Trade trade : tradeList) {
            List<TradeProduct> tradeProductList = tradeProductRepository.findAllByTradeId(trade.getId());
            List<ProfitByCategoryDto> profitByCategoryDtoList = new ArrayList<>();
            for (TradeProduct tradeProduct : tradeProductList) {

            }
        }

        return new ApiResponse("Found", true, tradeList);
    }

    public ApiResponse productionReports(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<Production> productionList = productionRepository.findAllByBranchId(branchId);

        return new ApiResponse("Found", true, productionList);
    }

    public ApiResponse productsReport(UUID customerId, UUID branchId, String date, Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("not found branch", false);
        }

        if (customerId != null) {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isPresent()) {
                List<ProductReportDto> productReport = getProductReport(customerId, branchId, date, startDate, endDate, true);
                return new ApiResponse("all", true, productReport);
            }
        }
        List<ProductReportDto> productReport = getProductReport(customerId, branchId, date, startDate, endDate, false);
        return new ApiResponse("all", true, productReport);
    }

    private List<ProductReportDto> getProductReport(UUID customerId, UUID branchId, String date, Date startDate, Date endDate, boolean isByCustomerId) {
        Map<UUID, Double> productAmount = new HashMap<>();
        List<ProductReportDto> all = new ArrayList<>();
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;
        if (startDate != null && endDate != null) {
            startTimestamp = new Timestamp(startDate.getTime());
            endTimestamp = new Timestamp(endDate.getTime());
        }

        if (startDate != null && endDate != null) {
            startTimestamp = new Timestamp(startDate.getTime());
            endTimestamp = new Timestamp(endDate.getTime());
        }
        switch (date) {
            case ("LAST_DAY"):
                startTimestamp = Timestamp.valueOf(START_OF_DAY);
                endTimestamp = Timestamp.valueOf(END_OF_DAY);
                break;
            case ("LAST_WEEK"):
                startTimestamp = Timestamp.valueOf(WEEK_START_DAY.atStartOfDay());
                endTimestamp = Timestamp.valueOf(WEEK_END_DAY.atStartOfDay());
                break;
            case ("LAST_THIRTY_DAY"):
                startTimestamp = Timestamp.valueOf(END_OF_MONTH);
                endTimestamp = currentDay;
                break;
            case ("THIS_YEAR"):
                startTimestamp = Timestamp.valueOf(START_OF_YEAR_FOR_THIS);
                endTimestamp = currentDay;
                break;
            case ("LAST_YEAR"):
                startTimestamp = Timestamp.valueOf(START_OF_YEAR);
                endTimestamp = Timestamp.valueOf(END_OF_MONTH);
                break;
            case ("LAST_MONTH"):
                startTimestamp = Timestamp.valueOf(START_OF_MONTH);
                endTimestamp = Timestamp.valueOf(END_OF_MONTH);
                break;
            case ("THIS_MONTH"):
                startTimestamp = Timestamp.valueOf(THIS_MONTH);
                endTimestamp = currentDay;
                break;
            case ("ALL"):
                List<Trade> allByCustomerId = tradeRepository.findAllByCustomer_Id(customerId);
                for (Trade trade : allByCustomerId) {
                    List<TradeProduct> allTradeCustomerId = tradeProductRepository.findAllByTradeId(trade.getId());
                    for (TradeProduct tradeProduct : allTradeCustomerId) {
                        if (tradeProduct.getProduct() != null) {
                            List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_IdAndTrade_CustomerId(tradeProduct.getProduct().getId(), customerId);
                            double totalAmount = 0;
                            for (TradeProduct product : allByProductId) {
                                totalAmount += product.getTradedQuantity();
                                productAmount.put(product.getProduct().getId(), totalAmount);
                            }
                        }
                    }
                    for (TradeProduct tradeProduct : allTradeCustomerId) {
                        if (tradeProduct.getProductTypePrice() != null) {
                            List<TradeProduct> allByProductTypePriceId = tradeProductRepository.findAllByProductTypePriceIdAndTrade_CustomerId(tradeProduct.getProductTypePrice().getId(), customerId);
                            double totalAmount = 0;
                            for (TradeProduct product : allByProductTypePriceId) {
                                totalAmount += product.getTradedQuantity();
                                productAmount.put(product.getProductTypePrice().getId(), totalAmount);
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        if (isByCustomerId && !date.equals("ALL")) {
            List<Trade> allByCustomerId = tradeRepository.findAllByCreatedAtBetweenAndCustomer_Id(startTimestamp, endTimestamp, customerId);
            for (Trade trade : allByCustomerId) {
                List<TradeProduct> allTradeCustomerId = tradeProductRepository.findAllByTradeId(trade.getId());
                for (TradeProduct tradeProduct : allTradeCustomerId) {
                    if (tradeProduct.getProduct() != null) {
                        List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_IdAndTrade_CustomerId(tradeProduct.getProduct().getId(), customerId);
                        double totalAmount = 0;
                        for (TradeProduct product : allByProductId) {
                            totalAmount += product.getTradedQuantity();
                            productAmount.put(product.getProduct().getId(), totalAmount);
                        }
                    }
                }
                for (TradeProduct tradeProduct : allTradeCustomerId) {
                    if (tradeProduct.getProductTypePrice() != null) {
                        List<TradeProduct> allByProductTypePriceId = tradeProductRepository.findAllByProductTypePriceIdAndTrade_CustomerId(tradeProduct.getProductTypePrice().getId(), customerId);
                        double totalAmount = 0;
                        for (TradeProduct product : allByProductTypePriceId) {
                            totalAmount += product.getTradedQuantity();
                            productAmount.put(product.getProductTypePrice().getId(), totalAmount);
                        }
                    }
                }
            }
        } else if (!date.equals("ALL")) {
            Optional<Branch> optionalBranch = branchRepository.findById(branchId);
            if (optionalBranch.isEmpty()) {
                return null;
            }
            Business business = optionalBranch.get().getBusiness();
            List<TradeProduct> allTradeBranch = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BusinessId(startTimestamp, endTimestamp, business.getId());
            for (TradeProduct tradeProduct : allTradeBranch) {
                if (tradeProduct.getProduct() != null) {
                    List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_Id(tradeProduct.getProduct().getId());
                    double totalAmount = 0;
                    for (TradeProduct product : allByProductId) {
                        totalAmount += product.getTradedQuantity();
                        productAmount.put(product.getProduct().getId(), totalAmount);
                    }
                }
            }
            for (TradeProduct tradeProduct : allTradeBranch) {
                if (tradeProduct.getProductTypePrice() != null) {
                    List<TradeProduct> allByProductTypePriceId = tradeProductRepository.findAllByProductTypePriceId(tradeProduct.getProductTypePrice().getId());
                    double totalAmount = 0;
                    for (TradeProduct product : allByProductTypePriceId) {
                        totalAmount += product.getTradedQuantity();
                        productAmount.put(product.getProductTypePrice().getId(), totalAmount);
                    }
                }
            }
        }

        for (Map.Entry<UUID, Double> productAmounts : productAmount.entrySet()) {
            Optional<Product> optionalProduct = productRepository.findById(productAmounts.getKey());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                ProductReportDto productReportDto = new ProductReportDto();
                productReportDto.setName(product.getName());
                productReportDto.setBranch(product.getBranch().get(0).getName());
                productReportDto.setBarcode(product.getBarcode());
                productReportDto.setSalePrice(product.getSalePrice());
                productReportDto.setBuyPrice(product.getBuyPrice());
                productReportDto.setAmount(productAmounts.getValue());
                all.add(productReportDto);
            }
        }
        return all;
    }
}
