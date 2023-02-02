package uz.pdp.springsecurity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.springsecurity.entity.Customer;
import uz.pdp.springsecurity.payload.CustomerDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "customerGroupId", source = "customerGroup.id")
    @Mapping(target = "businessId", source = "business.id")
    @Mapping(target = "branchId", source = "branch.id")
    CustomerDto toDto(Customer customer);

    List<CustomerDto> toDtoList(List<Customer> customers);


}
