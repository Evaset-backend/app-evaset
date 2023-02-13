package uz.pdp.springsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uz.pdp.springsecurity.entity.template.AbsEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FifoCalculation extends AbsEntity {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProductTypePrice productTypePrice;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    private double purchasedAmount;

    private double remainAmount;

    private double buyPrice;

    private boolean active = true;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PurchaseProduct purchaseProduct;

    public FifoCalculation(Branch branch, double purchasedAmount, double remainAmount, double buyPrice, Date date, PurchaseProduct purchaseProduct) {
        this.branch = branch;
        this.purchasedAmount = purchasedAmount;
        this.remainAmount = remainAmount;
        this.buyPrice = buyPrice;
        this.date = date;
        this.purchaseProduct = purchaseProduct;
    }


}
