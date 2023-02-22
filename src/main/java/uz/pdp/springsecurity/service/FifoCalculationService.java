package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.enums.Type;
import uz.pdp.springsecurity.repository.FifoCalculationRepository;
import uz.pdp.springsecurity.repository.ProductTypeComboRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FifoCalculationService {
    @Autowired
    private FifoCalculationRepository fifoRepository;
    @Autowired
    private ProductTypeComboRepository productTypeComboRepository;

    public void createCalculation(PurchaseProduct purchaseProduct) {
        FifoCalculation fifoCalculation = new FifoCalculation(
                purchaseProduct.getPurchase().getBranch(),
                purchaseProduct.getPurchasedQuantity(),
                purchaseProduct.getPurchasedQuantity(),
                purchaseProduct.getBuyPrice(),
                purchaseProduct.getPurchase().getDate(),
                purchaseProduct
        );
        if (purchaseProduct.getProduct()!=null){
            fifoCalculation.setProduct(purchaseProduct.getProduct());
        }else {
            fifoCalculation.setProductTypePrice(purchaseProduct.getProductTypePrice());
        }
        fifoRepository.save(fifoCalculation);
    }

    public void editFifoCalculation(PurchaseProduct purchaseProduct, Double amount) {
        Optional<FifoCalculation> optionalFifoCalculation = fifoRepository.findByPurchaseProductId(purchaseProduct.getId());
        if (optionalFifoCalculation.isEmpty()) return;
        FifoCalculation fifoCalculation = optionalFifoCalculation.get();
        fifoCalculation.setBuyPrice(purchaseProduct.getBuyPrice());
        fifoCalculation.setPurchasedAmount(purchaseProduct.getPurchasedQuantity());
        fifoCalculation.setRemainAmount(fifoCalculation.getRemainAmount() + amount);
        if (fifoCalculation.getRemainAmount() <= 0d)fifoCalculation.setActive(false);
        fifoRepository.save(fifoCalculation);
    }

    public TradeProduct createOrEditTradeProduct(Branch branch, TradeProduct tradeProduct, double quantity) {
        List<FifoCalculation> fifoList = null;
        double salePrice = 0;
        double profit = 0;
        if (tradeProduct.getProduct() != null) {
            Product product = tradeProduct.getProduct();
            if (product.getType().equals(Type.SINGLE)) {
                salePrice = product.getSalePrice();
                fifoList = fifoRepository.findAllByBranchIdAndProductIdAndActiveTrueOrderByDateAscCreatedAtAsc(branch.getId(), product.getId());
                profit = createOrEditTradeProductHelper(fifoList, quantity, salePrice);
                fifoRepository.saveAll(fifoList);
            }else {
                List<ProductTypeCombo> comboList = productTypeComboRepository.findAllByMainProductId(product.getId());
                for (ProductTypeCombo combo : comboList) {
                    salePrice = combo.getContentProduct().getSalePrice();
                    fifoList = fifoRepository.findAllByBranchIdAndProductIdAndActiveTrueOrderByDateAscCreatedAtAsc(branch.getId(), combo.getContentProduct().getId());
                    profit += createOrEditTradeProductHelper(fifoList, quantity * combo.getAmount(), salePrice);
                    fifoRepository.saveAll(fifoList);
                }
            }
        } else {
            ProductTypePrice productTypePrice = tradeProduct.getProductTypePrice();
            salePrice = productTypePrice.getSalePrice();
            fifoList = fifoRepository.findAllByBranchIdAndProductTypePriceIdAndActiveTrueOrderByDateAscCreatedAtAsc(branch.getId(), productTypePrice.getId());
            profit = createOrEditTradeProductHelper(fifoList, quantity, salePrice);
            fifoRepository.saveAll(fifoList);
        }

        tradeProduct.setProfit(tradeProduct.getProfit() + profit);
        return tradeProduct;
    }

    private Double createOrEditTradeProductHelper(List<FifoCalculation> fifoList, Double quantity, Double salePrice) {
        double profit = 0;
        for (FifoCalculation fifo : fifoList) {
            if (fifo.getRemainAmount()>quantity){
                fifo.setRemainAmount(fifo.getRemainAmount() - quantity);
                profit += quantity * (salePrice - fifo.getBuyPrice());
                break;
            } else if (fifo.getRemainAmount() < quantity) {
                double amount = fifo.getRemainAmount();
                quantity -= amount;
                profit += amount * (salePrice - fifo.getBuyPrice());
                fifo.setRemainAmount(0);
                fifo.setActive(false);
            }else {
                profit += quantity * (salePrice - fifo.getBuyPrice());
                fifo.setRemainAmount(0);
                fifo.setActive(false);
                break;
            }
        }
        return profit;
    }

    public void returnedTrade(Branch branch, TradeProduct tradeProduct, double quantity) {
        List<FifoCalculation> fifoList = null;
        double salePrice = 0;
        Double profit = 0d;
        if (tradeProduct.getProduct() != null) {
            Product product = tradeProduct.getProduct();
            if (product.getType().equals(Type.SINGLE)) {
                salePrice = product.getSalePrice();
                fifoList = fifoRepository.findAllByBranchIdAndProductIdOrderByDateDescCreatedAtDesc(branch.getId(), product.getId());
                profit = returnedTradeHelper(fifoList, quantity, salePrice);
                fifoRepository.saveAll(fifoList);
            }else {
                List<ProductTypeCombo> comboList = productTypeComboRepository.findAllByMainProductId(product.getId());
                for (ProductTypeCombo combo : comboList) {
                    salePrice = combo.getContentProduct().getSalePrice();
                    fifoList = fifoRepository.findAllByBranchIdAndProductIdOrderByDateDescCreatedAtDesc(branch.getId(), combo.getContentProduct().getId());
                    profit += returnedTradeHelper(fifoList, quantity * combo.getAmount(), salePrice);
                    fifoRepository.saveAll(fifoList);
                }
            }
        } else {
            ProductTypePrice productTypePrice = tradeProduct.getProductTypePrice();
            salePrice = productTypePrice.getSalePrice();
            fifoList = fifoRepository.findAllByBranchIdAndProductTypePriceIdOrderByDateDescCreatedAtDesc(branch.getId(), productTypePrice.getId());
            profit = returnedTradeHelper(fifoList, quantity, salePrice);
            fifoRepository.saveAll(fifoList);
        }
        tradeProduct.setProfit(tradeProduct.getProfit() - profit);
    }

    private Double returnedTradeHelper(List<FifoCalculation> fifoList, Double quantity, Double salePrice) {
        double profit = 0;
        for (FifoCalculation fifo : fifoList) {
            if (fifo.getPurchasedAmount() == fifo.getRemainAmount())continue;
            double soldQuantity = fifo.getPurchasedAmount() - fifo.getRemainAmount();
            if (soldQuantity >= quantity) {
                fifo.setRemainAmount(fifo.getRemainAmount() + quantity);
                fifo.setActive(true);
                profit += quantity * (salePrice - fifo.getBuyPrice());
                break;
            } else {
                quantity -= soldQuantity;
                fifo.setRemainAmount(fifo.getPurchasedAmount());
                fifo.setActive(true);
                profit += soldQuantity * (salePrice - fifo.getBuyPrice());
            }
        }
        return profit;
    }

    public void createExchange(ExchangeProductBranch exchangeProductBranch) {
        for (ExchangeProduct exchangeProduct : exchangeProductBranch.getExchangeProductList()) {
            if (exchangeProduct.getProduct() != null) {
                List<FifoCalculation> fifoCalculationList = fifoRepository.findAllByBranchIdAndProductIdAndActiveTrueOrderByDateAscCreatedAtAsc(
                        exchangeProductBranch.getShippedBranch().getId(), exchangeProduct.getProduct().getId());
                createExchangeHelper(fifoCalculationList, exchangeProduct);

                FifoCalculation fifoCalculation = new FifoCalculation();
                fifoCalculation.setBranch(exchangeProductBranch.getReceivedBranch());
                fifoCalculation.setDate(exchangeProductBranch.getExchangeDate());
                fifoCalculation.setPurchasedAmount(exchangeProduct.getExchangeProductQuantity());
                fifoCalculation.setRemainAmount(exchangeProduct.getExchangeProductQuantity());
                fifoCalculation.setProduct(exchangeProduct.getProduct());
                fifoCalculation.setBuyPrice(exchangeProduct.getProduct().getBuyPrice());
                fifoRepository.save(fifoCalculation);
            } else {
                List<FifoCalculation> fifoCalculationList = fifoRepository.findAllByBranchIdAndProductTypePriceIdAndActiveTrueOrderByDateAscCreatedAtAsc(
                        exchangeProductBranch.getShippedBranch().getId(), exchangeProduct.getProductTypePrice().getId());
                createExchangeHelper(fifoCalculationList, exchangeProduct);

                FifoCalculation fifoCalculation = new FifoCalculation();
                fifoCalculation.setBranch(exchangeProductBranch.getReceivedBranch());
                fifoCalculation.setDate(exchangeProductBranch.getExchangeDate());
                fifoCalculation.setPurchasedAmount(exchangeProduct.getExchangeProductQuantity());
                fifoCalculation.setRemainAmount(exchangeProduct.getExchangeProductQuantity());
                fifoCalculation.setProductTypePrice(exchangeProduct.getProductTypePrice());
                fifoCalculation.setBuyPrice(exchangeProduct.getProductTypePrice().getBuyPrice());
                fifoRepository.save(fifoCalculation);
            }
        }

    }

    private void createExchangeHelper(List<FifoCalculation> fifoCalculationList, ExchangeProduct exchangeProduct) {
        Double quantity = exchangeProduct.getExchangeProductQuantity();
        for (FifoCalculation fifoCalculation : fifoCalculationList) {
            if (fifoCalculation.getRemainAmount()>quantity){
                fifoCalculation.setRemainAmount(fifoCalculation.getRemainAmount() - quantity);
                break;
            } else if (fifoCalculation.getRemainAmount() < quantity) {
                double amount = fifoCalculation.getRemainAmount();
                quantity -= amount;
                fifoCalculation.setRemainAmount(0);
                fifoCalculation.setActive(false);
            }else {
                fifoCalculation.setRemainAmount(0);
                fifoCalculation.setActive(false);
                break;
            }
        }
        fifoRepository.saveAll(fifoCalculationList);
    }


}
