package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostSaleProductsDto {

    private String name;
    private String barcode;
    private double amount;
    private String measurement;
}