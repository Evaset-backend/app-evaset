package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.entity.CustomerGroup;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.CustomerGroupDto;
import uz.pdp.springsecurity.repository.BusinessRepository;
import uz.pdp.springsecurity.repository.CustomerGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerGroupService {

    @Autowired
    CustomerGroupRepository customerGroupRepository;

    @Autowired
    BusinessRepository businessRepository;

    public ApiResponse addCustomerGroup(CustomerGroupDto customerGroupDto) {

        Optional<Business> optionalBusiness = businessRepository.findById(customerGroupDto.getBusinessId());
        if (optionalBusiness.isEmpty()){
            return new ApiResponse("BRANCH NOT FOUND",false);
        }

        CustomerGroup customerGroup = new CustomerGroup(
                customerGroupDto.getName(),
                customerGroupDto.getPercent(),
                optionalBusiness.get()
        );
        customerGroupRepository.save(customerGroup);
        return new ApiResponse("ADDED", true);
    }

    public List<CustomerGroup> getAll() {
        List<CustomerGroup> customerGroups = customerGroupRepository.findAll();
        return customerGroups;
    }

    public ApiResponse delete(UUID id) {
        if (!customerGroupRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        customerGroupRepository.deleteById(id);
        return new ApiResponse("DELETED", true);
    }

    public ApiResponse getById(UUID id) {
        if (!customerGroupRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, customerGroupRepository.findById(id).get());
    }

    public ApiResponse edit(UUID id, CustomerGroupDto customerGroupDto) {
        if (!customerGroupRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        Optional<Business> optionalBusiness = businessRepository.findById(customerGroupDto.getBusinessId());
        if (optionalBusiness.isEmpty()){
            return new ApiResponse("BRANCH NOT FOUND",false);
        }

        CustomerGroup customerGroup = customerGroupRepository.getById(id);
        customerGroup.setName(customerGroupDto.getName());
        customerGroup.setPercent(customerGroupDto.getPercent());

        customerGroupRepository.save(customerGroup);
        return new ApiResponse("EDITED", true);
    }
}
