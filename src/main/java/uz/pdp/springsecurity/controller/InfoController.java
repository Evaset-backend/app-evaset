package uz.pdp.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.springsecurity.annotations.CheckPermission;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.service.InfoService;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/info")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @CheckPermission("VIEW_INFO")
    @GetMapping("/get-info-by-business/{businessId}")
    public HttpEntity<?> getInfoByBusiness(@PathVariable UUID businessId) {
        ApiResponse apiResponse = infoService.getInfoByBusiness(businessId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @CheckPermission("VIEW_INFO")
    @GetMapping("/get-info-by-branch/{branchId}")
    public HttpEntity<?> getInfoByBranch(@PathVariable UUID branchId) {
        ApiResponse apiResponse = infoService.getInfoByBranch(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }
}
