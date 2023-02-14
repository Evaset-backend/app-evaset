package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Branch;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.entity.Warehouse;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        double totalSum = 0D;
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            totalSum = amount * salePrice;
        }

        return new ApiResponse("All Business Products Amount" , true , totalSum);
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

        double totalSum = 0D;
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            totalSum = amount * salePrice;
        }

        return new ApiResponse("All Branch Products Amount" , true , totalSum);
    }


    public ApiResponse mostSaleProducts(UUID branchId){


        return new ApiResponse("Found",true);
    }
}
