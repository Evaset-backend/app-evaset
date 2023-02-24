package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final BusinessRepository businessRepository;
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;
    @Autowired
    OutlayRepository outlayRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    TradeProductRepository tradeProductRepository;

    public ApiResponse getInfoByBusiness(UUID businessId) {

        Optional<Business> optionalBusiness = businessRepository.findById(businessId);
        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("Business Not Found", false);
        }

        return getInfoHelper(
                purchaseRepository.findAllByBranch_BusinessId(businessId),
                tradeRepository.findAllByBranch_BusinessId(businessId),
                outlayRepository.findAllByBusinessId(businessId),
                tradeProductRepository.findAllByProduct_BusinessId(businessId)
        );
    }

    public ApiResponse getInfoByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }

        return getInfoHelper(
                purchaseRepository.findAllByBranch_Id(branchId),
                tradeRepository.findAllByBranch_Id(branchId),
                outlayRepository.findAllByBranch_Id(branchId),
                tradeProductRepository.findAllByProduct_BranchId(branchId)
        );
    }

    private ApiResponse getInfoHelper(List<Purchase> purchaseList, List<Trade> tradeList, List<Outlay> outlayList, List<TradeProduct> tradeProductList) {


        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        double allTrade = 0;
        double allTradeDebt = 0;
        HashMap<String, Double> byPayMethods = new HashMap<>();
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
            String type = trade.getPayMethod().getType();
            byPayMethods.put(type, byPayMethods.getOrDefault(type, 0d) + trade.getPaidSum());
        }
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);
        infoDto.setByPayMethods(byPayMethods);

        double totalProfit = 0;
        for (TradeProduct tradeProduct : tradeProductList) {
            totalProfit += (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getSalePrice()) - (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getBuyPrice());
        }
        infoDto.setTotalProfit(totalProfit);

        double totalOutlay = 0;
        for (Outlay outlay : outlayList) {
            totalOutlay += outlay.getTotalSum();
        }
        infoDto.setMyOutlay(totalOutlay);

        return new ApiResponse("FOUND", true, infoDto);
    }
}
