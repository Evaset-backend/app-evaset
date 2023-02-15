package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.repository.*;

import java.util.*;

@Service
public class ReportsService {


    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    public ApiResponse allProductAmount(UUID branchId){

        Optional<Business> optionalBusiness = businessRepository.findById(branchId);

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

        if (optionalBusiness.isEmpty()){
            return new ApiResponse("Business Not Found",false);
        }

        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);

        if (productList.isEmpty()){
            return new ApiResponse("No Found Products");
        }

        double totalSumBySalePrice = 0D;
        double totalSumByBuyPrice = 0D;
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            totalSumBySalePrice += amount * salePrice;
        }

        List<Double> doubleList=new ArrayList<>();
        doubleList.add(totalSumByBuyPrice);
        doubleList.add(totalSumBySalePrice);

        return new ApiResponse("Business Products Amount" , true , doubleList);
    }

    public ApiResponse allProductAmountByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

        List<Product> productList = productRepository.findAllByBranchIdAndActiveTrue(branchId);

        if (productList.isEmpty()){
            return new ApiResponse("No Found Products");
        }

        double totalSum = 0;
        double amount = 0;
        double salePrice = 0;
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            amount = warehouse.getAmount();
            salePrice = product.getSalePrice();
            totalSum += amount * salePrice;
        }

        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            amount = warehouse.getAmount();
            salePrice = product.getBuyPrice();
            totalSum += amount * salePrice;
        }


        return new ApiResponse("Branch Products Amount" , true , totalSum);
    }


    public ApiResponse mostSaleProducts(UUID branchId){
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        Business business = optionalBranch.get().getBusiness();
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BusinessId(business.getId());
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }

        tradeProductList.sort(Comparator.comparing(TradeProduct::getTradedQuantity));

        return new ApiResponse("Found",true,tradeProductList);
    }


}
