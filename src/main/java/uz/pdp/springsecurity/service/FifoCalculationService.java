package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.repository.FifoCalculationRepository;
import uz.pdp.springsecurity.repository.TradeProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class FifoCalculationService {
    @Autowired
    private FifoCalculationRepository fifoRepository;
    @Autowired
    private TradeProductRepository tradeProductRepository;

    public ApiResponse addProduct(Purchase purchase) {
        Branch branch = purchase.getBranch();
        List<PurchaseProduct> purchaseProductList = purchase.getPurchaseProductList();

        List<FifoCalculation> fifoCalculationList = new ArrayList<>();

        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            FifoCalculation fifoCalculation = new FifoCalculation(
                    branch,
                    purchaseProduct.getPurchasedQuantity(),
                    purchaseProduct.getPurchasedQuantity(),
                    purchaseProduct.getBuyPrice(),
                    purchase.getDate(),
                    purchase
            );
            if (purchaseProduct.getProduct()!=null){
                fifoCalculation.setProduct(purchaseProduct.getProduct());
            }else {
                fifoCalculation.setProductTypePrice(purchaseProduct.getProductTypePrice());
            }

            fifoCalculationList.add(fifoCalculation);
        }
        fifoRepository.saveAll(fifoCalculationList);
        return new ApiResponse(true);
    }

    public TradeProduct trade(Branch branch, TradeProduct tradeProduct) {
        Product product = tradeProduct.getProduct();
        List<FifoCalculation> fifoList = fifoRepository.findAllByBranchIdAndProductIdAndActiveTrueOrderByDateAscCreatedAtAsc(branch.getId(), product.getId());
        double tradedQuantity = tradeProduct.getTradedQuantity();
        double profit = 0;
        for (FifoCalculation fifo : fifoList) {
            if (fifo.getRemainAmount()>=tradedQuantity){
                double amount = fifo.getRemainAmount() - tradedQuantity;
                fifo.setRemainAmount(amount);
                profit = tradedQuantity * (product.getSalePrice() - fifo.getBuyPrice());
                tradedQuantity = 0;
                fifo.setActive(false);
                break;
            } else {
                double amount = fifo.getRemainAmount();
                fifo.setRemainAmount(0);
                profit += amount * (product.getSalePrice() - fifo.getBuyPrice());
                tradedQuantity -= amount;
            }
        }
        tradeProduct.setProfit(profit);
        fifoRepository.saveAll(fifoList);
        return tradeProduct;
    }
}
