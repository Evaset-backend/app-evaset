package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportsService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProductionRepository productionRepository;

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
    private final static LocalDate WEEK_START_DAY = localDate.minusDays(7+localDate.getDayOfWeek().getValue()-1);
    private final static LocalDate WEEK_END_DAY = localDate.minusDays(7+localDate.getDayOfWeek().getValue()-7);
    private final static LocalDate TEMP_START_OF_YEAR = LocalDate.of(localDate.getYear()-1,1,1);
    private final static LocalDate TEMP_FOR_THIS_START_OF_YEAR = LocalDate.of(localDate.getYear(),1,1);
    private final static LocalDate TEMP_START_OF_DAY = localDate.minusDays(1);
    private final static LocalDate TEMP_END_OF_DAY = LocalDate.of(localDate.getYear(),localDate.getMonth(),localDate.getDayOfMonth());
    private final static LocalDate TEMP_END_OF_YEAR = LocalDate.of(localDate.getYear()-1,12,31);
    private final static LocalDate TEMP_START_OF_MONTH_ONE =LocalDate.of(localDate.getYear(),localDate.getMonth().getValue(),1);
    private final static LocalDate TEMP_START_OF_MONTH = TEMP_START_OF_MONTH_ONE.minusMonths(1);
    private final static LocalDate TEMP_END_OF_MONTH = LocalDate.of(localDate.getYear(),TEMP_START_OF_MONTH.getMonth(),TEMP_START_OF_MONTH.lengthOfMonth());
    private final static LocalDateTime START_OF_YEAR = TEMP_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_YEAR_FOR_THIS = TEMP_FOR_THIS_START_OF_YEAR.atStartOfDay();
    private final static LocalDateTime END_OF_YEAR = TEMP_END_OF_YEAR.atStartOfDay();
    private final static LocalDateTime START_OF_MONTH = TEMP_START_OF_MONTH.atStartOfDay();
    private final static LocalDateTime END_OF_MONTH = TEMP_END_OF_MONTH.atStartOfDay();
    private final static LocalDateTime START_OF_DAY = TEMP_START_OF_DAY.atStartOfDay();
    private final static LocalDateTime END_OF_DAY = TEMP_END_OF_DAY.atStartOfDay();
    public ApiResponse allProductAmount(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }

        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }

        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);

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

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(),optionalBranch.get().getId());
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
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductIdAndBranchId(product.getId(),optionalBranch.get().getId());
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
            double amount  = 0;
            for (TradeProduct product : allByProductId) {
                amount+=product.getTradedQuantity();
                productAmount.put(product.getProduct().getId(),amount);
            }
        }
        List<MostSaleProductsDto> mostSaleProductsDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            MostSaleProductsDto mostSaleProductsDto=new MostSaleProductsDto();
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
        return new ApiResponse("Found", true,mostSaleProductsDtoList);
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
    public ApiResponse purchaseReports(UUID branchId) {

            Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found");
        }
        Branch branch = optionalBranch.get();

        List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllByPurchase_BranchId(branch.getId());

        if (purchaseProductList.isEmpty()) {
            return new ApiResponse("Purchase Product Not Found");
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
    public ApiResponse purchaseReportsByDates(UUID branchId,Date startDate,Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found");
        }
        Branch branch = optionalBranch.get();
        Timestamp from = new Timestamp(startDate.getTime());
        Timestamp to = new Timestamp(endDate.getTime());
        List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllByCreatedAtBetweenAndPurchase_BranchId(from,to,branch.getId());

        if (purchaseProductList.isEmpty()) {
            return new ApiResponse("Purchase Product Not Found");
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
    public ApiResponse purchaseReportsBySupplier(UUID branchId,UUID supplierId) {

        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        if (optionalSupplier.isEmpty()){
            return new ApiResponse("Not Found Supplier");
        }
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found Branch");
        }

        List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllByPurchase_BranchIdAndPurchase_SupplierId(branchId,supplierId);

        if (purchaseProductList.isEmpty()) {
            return new ApiResponse("Purchase Product Not Found");
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
    public ApiResponse outlayReports(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }
        List<Outlay> outlayList = outlayRepository.findAllByBranch_Id(branchId);
        if (outlayList.isEmpty()){
            return new ApiResponse("Not Found Outlay");
        }
        outlayList.sort(Comparator.comparing(Outlay::getTotalSum));
        return new ApiResponse("Found",true, outlayList);
    }
    public ApiResponse outlayReportsByDate(UUID branchId,Date startDate,Date endDate){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }
        Timestamp from = new Timestamp(startDate.getTime());
        Timestamp to = new Timestamp(endDate.getTime());
        List<Outlay> outlayList = outlayRepository.findAllByCreatedAtBetweenAndBranchId(from,to,branchId);
        if (outlayList.isEmpty()){
            return new ApiResponse("Not Found Outlay");
        }
        outlayList.sort(Comparator.comparing(Outlay::getTotalSum));
        return new ApiResponse("Found",true, outlayList);
    }
    public ApiResponse outlayReportsByCategory(UUID branchId,UUID categoryId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }
        List<Outlay> outlayList = outlayRepository.findAllByBranch_IdAndOutlayCategoryId(branchId,categoryId);
        if (outlayList.isEmpty()){
            return new ApiResponse("Not Found Outlay");
        }
        outlayList.sort(Comparator.comparing(Outlay::getTotalSum));
        return new ApiResponse("Found",true, outlayList);
    }
    public ApiResponse customerReports(UUID branchId,UUID customerId,Date startDate, Date endDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Not Found");
        }
        List<TradeProduct> tradeProducts = tradeProductRepository.findAllByProduct_BranchId(branchId);
        if (tradeProducts.isEmpty()){
            return new ApiResponse("Traded Product Not Found",false);
        }
        List<CustomerReportsDto> customerReportsDtoList = new ArrayList<>();


        if (startDate != null && endDate != null){
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
        }else {
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
            double amount  = 0;
            for (TradeProduct product : allByProductId) {
                amount+=product.getTradedQuantity();
                productAmount.put(product.getProduct().getId(),amount);
            }
        }

        List<MostSaleProductsDto> mostSaleProductsDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            MostSaleProductsDto mostSaleProductsDto=new MostSaleProductsDto();
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
        return new ApiResponse("Found", true,mostSaleProductsDtoList);
    }
    public ApiResponse dateBenefitAndLostByProductReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount  = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY),tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()),Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()) ,tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }
            else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(LAST_MONTH), currentDay,tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProductId(start,end,tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For This Date",false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_Id(tradeProduct.getProduct().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getId(), amount);
                }
            }
        }
        List<ProfitByProductDto> profitByProductDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByProductDto profitByProductDto=new ProfitByProductDto();
            Optional<Product> optionalProduct = productRepository.findById(entry.getKey());
            profitByProductDto.setName(optionalProduct.get().getName());
            profitByProductDto.setProfit(entry.getValue());
            profitByProductDtoList.add(profitByProductDto);
        }
        profitByProductDtoList.sort(Comparator.comparing(ProfitByProductDto::getProfit).reversed());

        return new ApiResponse("Found",true,profitByProductDtoList);
    }
    public ApiResponse benefitAndLostByCategoryReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount  = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY),tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()),Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()) ,tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }
            else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(LAST_MONTH), currentDay,tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_CategoryId(start,end,tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For This Date",false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_CategoryId(tradeProduct.getProduct().getCategory().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getCategory().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto=new ProfitByCategoryDto();
            Optional<Category> optionalCategory = categoryRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalCategory.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found",true,profitByCategoryDtoList);
    }
    public ApiResponse benefitAndLostByBrandReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount  = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY),tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()),Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()) ,tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndProduct_BrandId(start,end,tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For This Date",false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByProduct_BrandId(tradeProduct.getProduct().getBrand().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(tradeProduct.getProduct().getBrand().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto=new ProfitByCategoryDto();
            Optional<Brand> optionalBrand = brandRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalBrand.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found",true,profitByCategoryDtoList);
    }
    public ApiResponse benefitAndLostByCustomerReports(UUID branchId, String date, Date comingStartDate, Date comingEndDate) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(optionalBranch.get().getId());
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found",false);
        }
        Map<UUID, Double> productAmount = new HashMap<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            double amount  = 0;
            if (Objects.equals(date, "LAST_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_DAY), Timestamp.valueOf(END_OF_DAY),tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_WEEK")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(WEEK_START_DAY.atStartOfDay()),Timestamp.valueOf(WEEK_END_DAY.atStartOfDay()) ,tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getProduct().getBrand().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_MONTH), Timestamp.valueOf(END_OF_MONTH), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getProduct().getBrand().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_MONTH")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(THIS_MONTH), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }else if (Objects.equals(date, "THIS_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_YEAR_FOR_THIS), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }
            else if (Objects.equals(date, "LAST_THIRTY_DAY")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(LAST_MONTH), currentDay, tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()) {
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }else if (Objects.equals(date, "LAST_YEAR")) {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(Timestamp.valueOf(START_OF_YEAR), Timestamp.valueOf(END_OF_YEAR), tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }else if (comingEndDate != null && comingStartDate != null) {
                Timestamp start = new Timestamp(comingStartDate.getTime());
                Timestamp end = new Timestamp(comingEndDate.getTime());
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByCreatedAtBetweenAndTrade_CustomerId(start,end,tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For This Date",false);
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }else {
                List<TradeProduct> allByProductId = tradeProductRepository.findAllByTrade_CustomerId(tradeProduct.getTrade().getCustomer().getId());
                if (allByProductId.isEmpty()){
                    return new ApiResponse("Traded Product Not Found For Last Day");
                }
                for (TradeProduct product : allByProductId) {
                    amount += (product.getProduct().getSalePrice() * product.getTradedQuantity()) - (product.getProduct().getBuyPrice() * product.getTradedQuantity());
                    productAmount.put(product.getTrade().getCustomer().getId(), amount);
                }
            }
        }
        List<ProfitByCategoryDto> profitByCategoryDtoList=new ArrayList<>();
        for ( Map.Entry<UUID, Double> entry : productAmount.entrySet()) {
            ProfitByCategoryDto profitByCategoryDto=new ProfitByCategoryDto();
            Optional<Customer> optionalCustomer = customerRepository.findById(entry.getKey());
            profitByCategoryDto.setCategoryName(optionalCustomer.get().getName());
            profitByCategoryDto.setProfit(entry.getValue());
            profitByCategoryDtoList.add(profitByCategoryDto);
        }
        profitByCategoryDtoList.sort(Comparator.comparing(ProfitByCategoryDto::getProfit).reversed());
        return new ApiResponse("Found",true,profitByCategoryDtoList);
    }
    public ApiResponse benefitAndLostByOneDateReports(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        long aLong = timestamp.getTime();
        long bLong = timestamp.getTime()-86400000;
        Date startDate=new Date(aLong);
        Date endDate=new Date(bLong);
        List<Trade> tradeList = tradeRepository.findAllByPayDateBetween(endDate, startDate);
        for (Trade trade : tradeList) {
            List<TradeProduct> tradeProductList = tradeProductRepository.findAllByTradeId(trade.getId());
            List<ProfitByCategoryDto> profitByCategoryDtoList=new ArrayList<>();
            for (TradeProduct tradeProduct : tradeProductList) {

            }
        }

        return new ApiResponse("Found",true,tradeList);
    }

    public ApiResponse productionReports(UUID branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }
        List<Production> productionList = productionRepository.findAllByBranchId(branchId);

        return new ApiResponse("Found",true,productionList);
    }

}
