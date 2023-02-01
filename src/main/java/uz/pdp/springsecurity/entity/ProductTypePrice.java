package uz.pdp.springsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.springsecurity.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductTypePrice extends AbsEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductTypeValue productTypeValue;

    private String barcode;

    private double buyPrice;

    private double salePrice;

    private double profitPercent;
//    private double amount;
}
