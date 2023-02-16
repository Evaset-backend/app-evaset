package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Trade;
import uz.pdp.springsecurity.entity.TradeProduct;
import uz.pdp.springsecurity.entity.Warehouse;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.TradeGetOneDto;
import uz.pdp.springsecurity.repository.TradeProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TradeProductService {

    @Autowired
    TradeProductRepository tradeProductRepository;

    public ApiResponse getAllTrade(UUID businessId) {
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BusinessId(businessId);
        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }
        return new ApiResponse("Found",true,tradeProductList);
    }
}
