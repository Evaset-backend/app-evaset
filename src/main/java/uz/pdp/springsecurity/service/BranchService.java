package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Address;
import uz.pdp.springsecurity.entity.Branch;
import uz.pdp.springsecurity.entity.Business;
import uz.pdp.springsecurity.entity.User;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.BranchDto;
import uz.pdp.springsecurity.repository.AddressRepository;
import uz.pdp.springsecurity.repository.BranchRepository;
import uz.pdp.springsecurity.repository.BusinessRepository;
import uz.pdp.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchService {
    @Autowired
    BranchRepository branchRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    BusinessRepository businessRepository;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    public ApiResponse addBranch(BranchDto branchDto) {
        Branch branch = new Branch();

        branch.setName(branchDto.getName());

        Optional<Address> byId = addressRepository.findById(branchDto.getAddressId());
        if (byId.isEmpty()) return new ApiResponse("ADDRESS NOT FOUND", false);
        branch.setAddress(byId.get());

        Optional<Business> optionalBusiness = businessRepository.findById(branchDto.getBusinessId());
        if (optionalBusiness.isEmpty()) return new ApiResponse("BUSINESS NOT FOUND", false);
        branch.setBusiness(optionalBusiness.get());

        branchRepository.save(branch);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.getBranches().add(branch);
        userRepository.save(user);
        invoiceService.create(branch);
        return new ApiResponse("ADDED", true);
    }


    public ApiResponse editBranch(UUID id, BranchDto branchDto) {
        if (!branchRepository.existsById(id)) return new ApiResponse("BRANCH NOT FOUND", false);

        Branch branch = branchRepository.getById(id);
        branch.setName(branchDto.getName());

        if (!addressRepository.existsById(branchDto.getAddressId())) return new ApiResponse("ADDRESS NOT FOUND", false);
        branch.setAddress(branch.getAddress());

        Optional<Business> optionalBusiness = businessRepository.findById(branchDto.getBusinessId());
        if (optionalBusiness.isEmpty()) return new ApiResponse("BUSINESS NOT FOUND", false);
        branch.setBusiness(optionalBusiness.get());
        branchRepository.save(branch);
        return new ApiResponse("EDITED", true);
    }

    public ApiResponse getBranch(UUID id) {
        if (!branchRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);
        return new ApiResponse("FOUND", true, branchRepository.findById(id).get());
    }

    public ApiResponse deleteBranch(UUID id) {
        if (!branchRepository.existsById(id)) return new ApiResponse("NOT FOUND", false);

        branchRepository.deleteById(id);
        return new ApiResponse("DELETED", true);
    }


    public ApiResponse getByBusinessId(UUID business_id) {
        List<Branch> allByBusiness_id = branchRepository.findAllByBusiness_Id(business_id);
        if (allByBusiness_id.isEmpty()) return new ApiResponse("BUSINESS NOT FOUND",false);
        return new ApiResponse("FOUND",true,allByBusiness_id);
    }
}
