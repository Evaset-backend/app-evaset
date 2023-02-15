package uz.pdp.springsecurity.payload;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeReportsDto {
    private String name;
    private String barcode;
    private String customerName;
    private Date tradedDate;
    private double amount;
    private double salePrice;
    private double discount;
    private double tax;
    private double totalSum;
}
