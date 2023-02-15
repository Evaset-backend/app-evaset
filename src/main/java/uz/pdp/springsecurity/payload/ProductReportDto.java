package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReportDto {

    private String name;

    private String barcode;

    private String description;

    private Date purchesedDate;

    private String supplier;

    private double buyPrice;


}
