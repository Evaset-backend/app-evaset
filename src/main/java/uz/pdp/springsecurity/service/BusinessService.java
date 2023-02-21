package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.mapper.AddressMapper;
import uz.pdp.springsecurity.mapper.BranchMapper;
import uz.pdp.springsecurity.mapper.BusinessMapper;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;
import uz.pdp.springsecurity.util.Constants;

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

    private final SubscriptionRepository subscriptionRepository;

    private final BusinessMapper businessMapper;

    public ApiResponse add(BusinessDto businessDto) {
        if (businessRepository.existsByName(businessDto.getName()))
            return new ApiResponse("A BUSINESS WITH THAT NAME ALREADY EXISTS", false);
        Business business = new Business();
        business.setName(businessDto.getName());
        business.setDescription(businessDto.getDescription());
        UUID tariffId = businessDto.getTariffId();
        Optional<Tariff> optionalTariff = tariffRepository.findById(tariffId);
        business.setActive(businessDto.isActive());
        business.setDelete(false);
        business = businessRepository.save(business);
        Currency currencyUZB = currencyRepository.save(new Currency(
                "SO'M",
                "UZB",
                business,
                true));

        Subscription subscription = new Subscription();

        subscription.setBusiness(business);
        optionalTariff.ifPresent(subscription::setTariff);
        subscription.setActive(true);
        subscriptionRepository.save(subscription);


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
            userDto.setRoleId(roleAdmin.getId());
        }
        userDto.setBusinessId(business.getId());

        userService.add(userDto);

        return new ApiResponse("ADDED", true);
    }

    public ApiResponse edit(UUID id, BusinessEditDto businessEditDto) {
        Optional<Business> optionalBusiness = businessRepository.findById(id);
        if (optionalBusiness.isEmpty()) return new ApiResponse("BUSINESS NOT FOUND", false);

        Optional<Business> businessOptional = businessRepository.findByName(businessEditDto.getName());
        if (businessOptional.isPresent()) {
            if (!businessOptional.get().getId().equals(id)) {
                return new ApiResponse("A BUSINESS WITH THAT NAME ALREADY EXISTS", false);
            }
        }


        Business business = optionalBusiness.get();
        business.setName(businessEditDto.getName());
        business.setDescription(businessEditDto.getDescription());
        business.setActive(businessEditDto.isActive());
        business.setDelete(businessEditDto.isDelete());

        businessRepository.save(business);
        return new ApiResponse("EDITED", true);
    }

    public ApiResponse getOne(UUID id) {
        Optional<Business> optionalBusiness = businessRepository.findById(id);
        return optionalBusiness.map(business -> new ApiResponse("FOUND", true, business)).orElseGet(() -> new ApiResponse("not found business", false));
    }

    public ApiResponse getAllSubscription() {
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        if (subscriptionList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, subscriptionList);
    }

    public ApiResponse getAllBusinessmen() {
        Optional<Role> optionalRole = roleRepository.findByName(Constants.SUPERADMIN);
        if (optionalRole.isEmpty()) return new ApiResponse("NOT FOUND", false);
        Role superAdmin = optionalRole.get();

        Optional<Role> optionalAdmin = roleRepository.findByNameAndBusinessId(Constants.ADMIN, superAdmin.getBusiness().getId());
        if (optionalAdmin.isEmpty()) return new ApiResponse("NOT FOUND", false);
        Role admin = optionalAdmin.get();

        List<User> userList = userRepository.findAllByRole_Id(admin.getId());
        if (userList.isEmpty())return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, userList);
    }

    public ApiResponse deleteOne(UUID id) {
        Optional<Business> optionalBusiness = businessRepository.findById(id);
        if (optionalBusiness.isEmpty()) {
            return new ApiResponse("not found business", false);
        }
        Business business = optionalBusiness.get();
        business.setDelete(true);
        business.setActive(false);
        businessRepository.save(business);
        return new ApiResponse("DELETED", true);
    }

    public ApiResponse getAll() {
        List<Business> all = businessRepository.findAllByDeleteIsFalse();
        return new ApiResponse("all business", true, businessMapper.toDtoList(all));
    }
}
