package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReportDto {
    private String name;
    private String branch;
    private String brand;
    private String category;
    private double buyPrice;
    private double salePrice;
    private double amount;
    private double SumBySalePrice;
    private double SumByBuyPrice;



}
