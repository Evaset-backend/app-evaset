package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {
    private String name;

    private String description;

    private int branchAmount;

    private long productAmount;

    private int employeeAmount;

    private long tradeAmount;

    private String lifetime;

    private int testDay;

    private int interval;

    private double price;

    private double discount;

    private boolean isActive;

    private boolean isDelete;
}
