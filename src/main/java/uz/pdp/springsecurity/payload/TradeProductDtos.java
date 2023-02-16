package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uz.pdp.springsecurity.entity.*;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeProductDtos {

    private String trader;
    private String branch;
    private String paymentStatus;
    private String payMethod;
    private Date payDate;
    private Double totalSum;
    private Double paidSum;
    private double debtSum = 0;
    private Double totalProfit = 0.0;
    private String address;

    private double tradedQuantity;
}
