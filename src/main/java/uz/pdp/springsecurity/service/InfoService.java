package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Purchase;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.repository.PurchaseRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final PurchaseRepository purchaseRepository;

    public ApiResponse getInfoByBusiness(UUID businessId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_BusinessId(businessId);
        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }

    public ApiResponse getInfoByBranch(UUID branchId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);
        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }
}
