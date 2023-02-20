package uz.pdp.springsecurity.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDto {
    @NotNull(message = "required line")
    private String name;
    private String description;

    private UUID tariffId;

    private boolean isActive;

    private UserDto userDto;
}
