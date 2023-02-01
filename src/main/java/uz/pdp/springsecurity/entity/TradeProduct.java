package uz.pdp.springsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uz.pdp.springsecurity.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TradeProduct extends AbsEntity {

    private Double tradedQuantity;

//    private double buyPrice;
//
    private double salePrice;

    //TOTAL PROFIT OF PRODUCT
    private double profit = 0;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;
}
