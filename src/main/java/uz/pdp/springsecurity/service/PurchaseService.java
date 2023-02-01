package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.PurchaseDto;
import uz.pdp.springsecurity.payload.PurchaseProductDto;
import uz.pdp.springsecurity.payload.Statistic;
import uz.pdp.springsecurity.repository.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    PurchaseProductRepository purchaseProductRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ExchangeStatusRepository exchangeStatusRepository;

    @Autowired
    PaymentStatusRepository paymentStatusRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    CurrentCourceRepository currentCourceRepository;

    @Autowired
    FifoCalculationService fifoCalculationService;

    @Autowired
    ProductTypePriceRepository productTypePriceRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    WarehouseService warehouseService;


    public ApiResponse add(PurchaseDto purchaseDto) {
        Purchase purchase = new Purchase();
        return addPurchase(purchase, purchaseDto);
    }

    private ApiResponse addPurchase(Purchase purchase, PurchaseDto purchaseDto) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(purchaseDto.getSupplerId());
        if (optionalSupplier.isEmpty()) return new ApiResponse("SUPPLIER NOT FOUND", false);
        Supplier supplier = optionalSupplier.get();

        Optional<User> optionalUser = userRepository.findById(purchaseDto.getSeller());
        if (optionalUser.isEmpty()) return new ApiResponse("SELLER NOT FOUND", false);
        purchase.setSeller(optionalUser.get());

        Optional<ExchangeStatus> optionalPurchaseStatus = exchangeStatusRepository.findById(purchaseDto.getPurchaseStatusId());
        if (optionalPurchaseStatus.isEmpty()) return new ApiResponse("PURCHASE STATUS NOT FOUND", false);
        purchase.setPurchaseStatus(optionalPurchaseStatus.get());

        Optional<PaymentStatus> optionalPaymentStatus = paymentStatusRepository.findById(purchaseDto.getPaymentStatusId());
        if (optionalPaymentStatus.isEmpty()) return new ApiResponse("PAYMENT STATUS NOT FOUND", false);
        purchase.setPaymentStatus(optionalPaymentStatus.get());

        Optional<Branch> optionalBranch = branchRepository.findById(purchaseDto.getBranchId());
        if (optionalBranch.isEmpty()) return new ApiResponse("BRANCH NOT FOUND", false);
        Branch branch = optionalBranch.get();
        purchase.setBranch(branch);

        purchase.setDate(purchaseDto.getDate());
        purchase.setDescription(purchaseDto.getDescription());

//        Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(branch.getBusiness().getId());
//        CurrentCource course = currentCourceRepository.getByCurrencyIdAndActive(currency.getId(), true);
        List<PurchaseProductDto> purchaseProductDtoList = purchaseDto.getPurchaseProductsDto();
        List<PurchaseProduct> purchaseProductList = new ArrayList<>();

        for (PurchaseProductDto purchaseProductDto : purchaseProductDtoList) {
            PurchaseProduct purchaseProduct = new PurchaseProduct();

            //SINGLE TYPE
            if (purchaseProductDto.getProductId()!=null) {
                UUID productId = purchaseProductDto.getProductId();
                Product product = productRepository.findById(productId).get();
                product.setSalePrice(purchaseProductDto.getSalePrice());
                product.setBuyPrice(purchaseProductDto.getBuyPrice());
                productRepository.save(product);
                purchaseProduct.setProduct(product);
            } else {//MANY TYPE
                UUID productTypePriceId = purchaseProductDto.getProductTypePriceId();
                ProductTypePrice productTypePrice = productTypePriceRepository.findById(productTypePriceId).get();
                productTypePrice.setBuyPrice(purchaseProductDto.getBuyPrice());
                productTypePrice.setSalePrice(purchaseProductDto.getSalePrice());
                productTypePriceRepository.save(productTypePrice);
                purchaseProduct.setProductTypePrice(productTypePrice);
            }

            purchaseProduct.setPurchasedQuantity(purchaseProductDto.getPurchasedQuantity());
            purchaseProduct.setSalePrice(purchaseProductDto.getSalePrice());
            purchaseProduct.setBuyPrice(purchaseProductDto.getBuyPrice());
            purchaseProduct.setTotalSum(purchaseDto.getTotalSum());

            purchaseProduct = purchaseProductRepository.save(purchaseProduct);
            purchaseProductList.add(purchaseProduct);

                /*if (!currency.getName().equalsIgnoreCase("SO'M")){
                    double salePrice = purchaseProductDto.getSalePrice();
                    salePrice = salePrice * course.getCurrentCourse();
                    product.setSalePrice(salePrice);
                    purchaseProduct.setSalePrice(salePrice);
                    double buyPrice = purchaseProductDto.getBuyPrice();
                    buyPrice = buyPrice * course.getCurrentCourse();
                    product.setBuyPrice(buyPrice);
                    purchaseProduct.setBuyPrice(buyPrice);
                    double totalSum = purchaseProductDto.getTotalSum();
                    totalSum = totalSum * course.getCurrentCourse();
                    purchaseProduct.setTotalSum(totalSum);
                }else {
                    product.setSalePrice(purchaseProductDto.getSalePrice());
                    product.setBuyPrice(purchaseProductDto.getBuyPrice());
                    purchaseProduct.setSalePrice(purchaseProductDto.getSalePrice());
                    purchaseProduct.setBuyPrice(purchaseProductDto.getBuyPrice());
                    purchaseProduct.setTotalSum(purchaseDto.getTotalSum());
                }*/
            }

        purchase.setPurchaseProductList(purchaseProductList);

        if (purchaseDto.getAvans() < purchaseDto.getTotalSum()) {
            supplier.setStoreDebt(supplier.getStoreDebt() + purchaseDto.getTotalSum() - purchaseDto.getAvans());
            supplier = supplierRepository.save(supplier);
        }
        purchase.setSupplier(supplier);
        purchase.setTotalSum(purchaseDto.getTotalSum());
        purchase.setDeliveryPrice(purchaseDto.getDeliveryPrice());
        purchase.setAvans(purchaseDto.getAvans());

        /*if (!currency.getName().equalsIgnoreCase("SO'M")){
            double deliveryPrice = purchaseDto.getDeliveryPrice();
            deliveryPrice = deliveryPrice * course.getCurrentCourse();
            purchase.setDeliveryPrice(deliveryPrice);
            double avans = purchaseDto.getAvans();
            avans = avans * course.getCurrentCourse();
            purchase.setAvans(avans);
        }else {
            purchase.setDeliveryPrice(purchaseDto.getDeliveryPrice());
            purchase.setAvans(purchaseDto.getAvans());
        }*/

        purchaseRepository.save(purchase);

        //TO SAVE AMOUNTS OF PRODUCTS TO WAREHOUSE
        warehouseService.addPurchase(purchase);

        //TO SAVE AMOUNTS OF PRODUCTS TO FIFO_CALCULATION
        fifoCalculationService.addProduct(purchase);
        return new ApiResponse("ADDED", true);
    }

    public ApiResponse edit(UUID id, PurchaseDto purchaseDto) {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(id);
        if (optionalPurchase.isEmpty()) return new ApiResponse("NOT FOUND", false);

        Purchase purchase = optionalPurchase.get();
        ApiResponse apiResponse = addPurchase(purchase, purchaseDto);

        if (!apiResponse.isSuccess()) return new ApiResponse("ERROR", false);
        return new ApiResponse("EDITED", true);
    }

    public ApiResponse get(UUID id) {
        if (!purchaseRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        Purchase purchase = purchaseRepository.findById(id).get();
        Purchase changePrices = changePrices(purchase);
        return new ApiResponse("FOUND", true, changePrices);
    }

    private Purchase changePrices(Purchase purchase){
        Currency currency = currencyRepository.findByBusinessIdAndActiveTrue(purchase.getBranch().getBusiness().getId());
        CurrentCource cource = currentCourceRepository.getByCurrencyIdAndActive(currency.getId(), true);
        if (!currency.getName().equalsIgnoreCase("SO'M")){
            double deliveryPrice = purchase.getDeliveryPrice();
            deliveryPrice = deliveryPrice / cource.getCurrentCourse();
            purchase.setDeliveryPrice(deliveryPrice);
            double avans = purchase.getAvans();
            avans = avans / cource.getCurrentCourse();
            purchase.setAvans(avans);
            double totalSum = purchase.getTotalSum();
            totalSum = totalSum / cource.getCurrentCourse();
            purchase.setTotalSum(totalSum);
            List<PurchaseProduct> productList = purchase.getPurchaseProductList();
            for (PurchaseProduct product : productList) {
                double salePrice = product.getSalePrice();
                salePrice = salePrice / cource.getCurrentCourse();
                product.setSalePrice(salePrice);
                double buyPrice = product.getBuyPrice();
                buyPrice = buyPrice / cource.getCurrentCourse();
                product.setBuyPrice(buyPrice);
            }
        }
        return purchase;
    }

    public ApiResponse delete(UUID id) {
        if (!purchaseRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        purchaseRepository.deleteById(id);
        return new ApiResponse("DELETED", false);
    }



    public ApiResponse getByDealerId(UUID dealer_id) {
        List<Purchase> allByDealer_id = purchaseRepository.findAllBySupplierId(dealer_id);
        if (allByDealer_id.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByDealer_id) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getByPurchaseStatusId(UUID purchaseStatus_id) {
        List<Purchase> allByPurchaseStatus_id = purchaseRepository.findAllByPurchaseStatus_Id(purchaseStatus_id);
        if (allByPurchaseStatus_id.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByPurchaseStatus_id) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getByPaymentStatusId(UUID paymentStatus_id) {
        List<Purchase> allByPaymentStatus_id = purchaseRepository.findAllByPaymentStatus_Id(paymentStatus_id);
        if (allByPaymentStatus_id.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByPaymentStatus_id) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getByBranchId(UUID branch_id) {
        List<Purchase> allByBranch_id = purchaseRepository.findAllByBranch_Id(branch_id);
        if (allByBranch_id.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByBranch_id) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getByDate(Date date) {
        List<Purchase> allByDate = purchaseRepository.findAllByDate(date);
        if (allByDate.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByDate) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getByTotalSum(double totalSum) {
        List<Purchase> allByTotalSum = purchaseRepository.findAllByTotalSum(totalSum);
        if (allByTotalSum.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByTotalSum) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getPdfFile(UUID id, HttpServletResponse response) throws IOException {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(id);
        if (optionalPurchase.isEmpty()) {
            return new ApiResponse("NOT FOUND PURCHASE", false);
        }
        PDFService pdfService = new PDFService();
        pdfService.createPdfPurchase(optionalPurchase.get(), response);
        return new ApiResponse("CREATED", true);
    }

    public ApiResponse getAllByBusiness(UUID businessId) {
        List<Purchase> allByBusinessId = purchaseRepository.findAllByBusinessId(businessId);
        if (allByBusinessId.isEmpty()) return new ApiResponse("NOT FOUND", false);
        List<Purchase> purchaseList = new ArrayList<>();
        for (Purchase purchase : allByBusinessId) {
            Purchase changePrices = changePrices(purchase);
            purchaseList.add(changePrices);
        }
        return new ApiResponse("FOUND", true, purchaseList);
    }

    public ApiResponse getCostbyBusiness(UUID businessId) {
        double cost = 0;
        double debt = 0;
        List<Purchase> purchaseList = purchaseRepository.findAllByBusinessId(businessId);
        for (Purchase purchase : purchaseList) {
            if (purchase.getTotalSum()>=purchase.getAvans() && purchase.getAvans()!=0){
                cost += purchase.getAvans();
                debt += (purchase.getTotalSum() - purchase.getAvans());
            }else {
                debt += purchase.getTotalSum();
            }
        }
        Statistic statistic = new Statistic(cost, debt);
        return new ApiResponse("Succesly", true, statistic);
    }
}
