package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.entity.ProductTypePrice;
import uz.pdp.springsecurity.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto{

    private UUID productId;


    private UUID productTypePriceId;

    @NotNull
    private Double quantity;


    private double costForPerQuantity;


    @NotNull
    private double totalPrice;

    List<ContentProductDto> contentProductDtoList;
}
