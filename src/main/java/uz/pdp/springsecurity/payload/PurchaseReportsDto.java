package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseReportsDto {

    private String name;

    private String barcode;

    private String supplier;

    private Date purchasedDate;

    private double purchasedAmount;

    private double tax;

    private double buyPrice;

    private double totalSum;

    private double debt;


}
