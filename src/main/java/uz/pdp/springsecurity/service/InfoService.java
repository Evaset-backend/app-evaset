package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Purchase;
import uz.pdp.springsecurity.entity.Trade;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.repository.PurchaseRepository;
import uz.pdp.springsecurity.repository.TradeRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;

    public ApiResponse getInfoByBusiness(UUID businessId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_BusinessId(businessId);
//        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        List<Trade> tradeList = tradeRepository.findAllByBranch_BusinessId(businessId);
//        if (tradeList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allTrade = 0;
        double allTradeDebt = 0;
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        infoDto.setMyTrade(allTrade);
        infoDto.setMyTrade(allTradeDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }

    public ApiResponse getInfoByBranch(UUID branchId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);
//        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        List<Trade> tradeList = tradeRepository.findAllByBranch_Id(branchId);
//        if (tradeList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allTrade = 0;
        double allTradeDebt = 0;
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        infoDto.setMyTrade(allTrade);
        infoDto.setMyTrade(allTradeDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }
}
