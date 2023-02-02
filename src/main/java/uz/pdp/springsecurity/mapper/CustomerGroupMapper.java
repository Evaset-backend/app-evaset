package uz.pdp.springsecurity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.springsecurity.entity.CustomerGroup;
import uz.pdp.springsecurity.payload.CustomerGroupDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerGroupMapper {

    @Mapping(target = "businessId", source = "business.id")
    CustomerGroupDto toDto(CustomerGroup customerGroup);

    List<CustomerGroupDto> toDtoList(List<CustomerGroup> customerGroups);
}
