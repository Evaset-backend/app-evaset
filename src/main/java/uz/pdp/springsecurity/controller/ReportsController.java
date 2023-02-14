package uz.pdp.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.springsecurity.annotations.CheckPermission;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.PurchaseDto;
import uz.pdp.springsecurity.repository.BusinessRepository;
import uz.pdp.springsecurity.service.ReportsService;

import java.util.UUID;

@RestController("/api/reports")
public class ReportsController {

    @Autowired
    ReportsService reportsService;

    @CheckPermission("GET_BUSINESS_ALL_AMOUNT")
    @GetMapping("/{branchId}")
    public HttpEntity<?> getAllBusinessAmount(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.allProductAmount(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @CheckPermission("GET_BUSINESS_ALL_AMOUNT")
    @GetMapping("/{id}")
    public HttpEntity<?> getAllBranchAmount(@PathVariable UUID id) {
        ApiResponse apiResponse = reportsService.allProductAmountByBranch(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


}
