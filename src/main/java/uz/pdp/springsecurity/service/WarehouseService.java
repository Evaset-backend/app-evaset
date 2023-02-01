package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.repository.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {
    @Autowired
    WarehouseRepository warehouseRepository;

    public void addPurchase(Purchase purchase) {
        Branch branch = purchase.getBranch();
        List<PurchaseProduct> purchaseProductList = purchase.getPurchaseProductList();
        List<Warehouse> warehouseList = new ArrayList<>();

        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            Warehouse warehouse = null;
            if (purchaseProduct.getProduct() != null) {
                Product product = purchaseProduct.getProduct();
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductId(branch.getId(), product.getId());
                if (optionalWarehouse.isPresent()){
                    warehouse = optionalWarehouse.get();
                    warehouse.setAmount(warehouse.getAmount() + purchaseProduct.getPurchasedQuantity());
                }else {
                    warehouse = new Warehouse();
                    warehouse.setBranch(branch);
                    warehouse.setProduct(product);
                    warehouse.setAmount(purchaseProduct.getPurchasedQuantity());
                }
            } else {
                ProductTypePrice productTypePrice = purchaseProduct.getProductTypePrice();
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranchIdAndProductTypePriceId(branch.getId(), productTypePrice.getId());
                if (optionalWarehouse.isPresent()){
                    warehouse = optionalWarehouse.get();
                    warehouse.setAmount(warehouse.getAmount() + purchaseProduct.getPurchasedQuantity());
                }else {
                    warehouse = new Warehouse();
                    warehouse.setBranch(branch);
                    warehouse.setProductTypePrice(productTypePrice);
                    warehouse.setAmount(purchaseProduct.getPurchasedQuantity());
                }
            }
            warehouseList.add(warehouse);
        }
        warehouseRepository.saveAll(warehouseList);
    }
}
