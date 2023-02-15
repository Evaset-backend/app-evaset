package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoDto {
    private double myDebt;

    private double myPurchase;

    private double myTrade;

    private double tradersDebt;
}
