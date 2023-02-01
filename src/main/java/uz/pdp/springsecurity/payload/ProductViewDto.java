package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.springsecurity.entity.Branch;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductViewDto {
    private String productName;
    private List<Branch> branch;
    private double buyPrice;
    private double salePrice;
    private double amount;
    private String brandName;
    private double minQuantity;
    private Date expiredDate;
}
