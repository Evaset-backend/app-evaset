package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;
    @Autowired
    OutlayRepository outlayRepository;
    @Autowired
    BranchRepository branchRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    public ApiResponse getInfoByBusiness(UUID businessId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_BusinessId(businessId);
//        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        double allTrade = 0;
        double allTradeDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }

        List<Trade> tradeList = tradeRepository.findAllByBranch_BusinessId(businessId);
//        if (tradeList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }

    public ApiResponse getInfoByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);
        if (purchaseList.isEmpty()){
            return new ApiResponse("Purchased Product Not Found");
        }

        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(branchId);



        double allPurchase = 0;
        double allMyDebt = 0;
        double allTrade = 0;
        double allTradeDebt = 0;
        double totalProfit = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }

        for (TradeProduct tradeProduct : tradeProductList) {
            totalProfit += (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getSalePrice()) - (tradeProduct.getTradedQuantity()*tradeProduct.getProduct().getBuyPrice());
        }

        List<Trade> tradeList = tradeRepository.findAllByBranch_Id(branchId);
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);
        infoDto.setTotalProfit(totalProfit);

        List<Outlay> outlayList = outlayRepository.findAllByBranch_Id(branchId);
        double totalOutlay = 0;
        if (!outlayList.isEmpty()){
            for (Outlay outlay : outlayList) {
                totalOutlay += outlay.getTotalSum();
            }
            infoDto.setMyOutlay(totalOutlay);
        }
        return new ApiResponse("FOUND", true, infoDto);
    }
}
