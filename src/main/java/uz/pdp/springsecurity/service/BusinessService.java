package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.mapper.AddressMapper;
import uz.pdp.springsecurity.mapper.BranchMapper;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    TariffRepository tariffRepository;

    @Autowired
    UserService userService;

    private final BranchRepository branchRepository;

    private final AddressRepository addressRepository;

    private final BranchMapper branchMapper;

    private final AddressMapper addressMapper;


    public ApiResponse add(BusinessDto businessDto) {
        if (businessRepository.existsByName(businessDto.getName()))
            return new ApiResponse("A BUSINESS WITH THAT NAME ALREADY EXISTS", false);
        Business business = new Business();
        business.setName(businessDto.getName());
        business.setDescription(businessDto.getDescription());
        UUID tariffId = businessDto.getTariffId();
        Optional<Tariff> optionalTariff = tariffRepository.findById(tariffId);
        optionalTariff.ifPresent(business::setTariff);
        business.setActive(businessDto.isActive());
        business = businessRepository.save(business);
        Currency currencyUZB = currencyRepository.save(new Currency(
                "SO'M",
                "UZB",
                business,
                true));

        AddressDto addressDto = businessDto.getAddressDto();
        BranchDto branchDto = businessDto.getBranchDto();
        UserDto userDto = businessDto.getUserDto();

        Address address = addressRepository.save(addressMapper.toEntity(addressDto));

        branchDto.setAddressId(address.getId());
        branchDto.setBusinessId(business.getId());
        Branch branch = branchRepository.save(branchMapper.toEntity(branchDto));
        List<UUID> branchIds = new ArrayList<>();
        branchIds.add(branch.getId());
        userDto.setBranchId(branchIds);

        Optional<Role> optionalRole = roleRepository.findByName("Admin");
        if (optionalRole.isPresent()) {
            Role roleAdmin = optionalRole.get();
            Role role = new Role(roleAdmin.getName(), roleAdmin.getPermissions(), business);
            Role save = roleRepository.save(role);
            userDto.setRoleId(save.getId());
        }

        userService.add(userDto);

        return new ApiResponse("ADDED", true);
    }

    public ApiResponse edit(UUID id, BusinessDto businessDto) {
        Optional<Business> optionalBusiness = businessRepository.findById(id);
        if (optionalBusiness.isEmpty()) return new ApiResponse("BUSINESS NOT FOUND", false);

        if (businessRepository.existsByName(businessDto.getName()))
            return new ApiResponse("A BUSINESS WITH THAT NAME ALREADY EXISTS", false);

        Business business = optionalBusiness.get();
        business.setName(businessDto.getName());
        business.setDescription(businessDto.getDescription());
        UUID tariffId = businessDto.getTariffId();
        Optional<Tariff> optionalTariff = tariffRepository.findById(tariffId);
        optionalTariff.ifPresent(business::setTariff);
        business.setActive(businessDto.isActive());

        businessRepository.save(business);
        return new ApiResponse("EDITED", true);
    }

    public ApiResponse getOne(UUID id) {
        if (!businessRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, businessRepository.findById(id).get());
    }

    public ApiResponse getAll() {
        return new ApiResponse("FOUND", true, businessRepository.findAll());
    }

    public ApiResponse deleteOne(UUID id) {

        for (User user : userRepository.findAllByBusiness_Id(id)) {
            userRepository.deleteById(user.getId());
        }
        for (Role role : roleRepository.findAllByBusiness_Id(id)) {
            roleRepository.deleteById(role.getId());
        }

        if (!businessRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        businessRepository.deleteById(id);
        return new ApiResponse("DELETED", true);
    }
}
