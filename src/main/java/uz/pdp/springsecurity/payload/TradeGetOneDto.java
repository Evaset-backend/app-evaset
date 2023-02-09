package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.springsecurity.entity.Purchase;
import uz.pdp.springsecurity.entity.PurchaseProduct;
import uz.pdp.springsecurity.entity.Trade;
import uz.pdp.springsecurity.entity.TradeProduct;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeGetOneDto {
    private Trade trade;
    private List<TradeProduct> tradeProductList;
}
