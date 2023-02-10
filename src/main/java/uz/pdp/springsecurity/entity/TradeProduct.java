package uz.pdp.springsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uz.pdp.springsecurity.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TradeProduct extends AbsEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trade trade;

    //USE FOR SINGLE TYPE// OR NULL
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    //USE FOR MANY TYPE// OR NULL
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProductTypePrice productTypePrice;

    private Double tradedQuantity;

//    private double buyPrice;
//
    private double totalSalePrice;

    //TOTAL PROFIT OF PRODUCT
    private double profit = 0;
}
