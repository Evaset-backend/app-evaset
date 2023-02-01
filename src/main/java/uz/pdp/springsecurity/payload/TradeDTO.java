package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TradeDTO {

    private Timestamp firstDate;

    private Timestamp secondDate;

    private UUID customerId;
    /**
     * savdogar id
     */
    private UUID userId;

    /**
     * product idlari
     */
    private List<ProductTradeDto> productTraderDto;

    private Date payDate;

    private UUID branchId;


    private UUID payMethodId;

    /**
     * umumiy summa
     */
    private double totalSum;

    /**
     * to'langan summa
     */
    private Double amountPaid;
    /**
     * qarz
     */
    private UUID addressId;

    private UUID businessId;

}
