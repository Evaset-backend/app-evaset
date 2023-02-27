package uz.pdp.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.springsecurity.annotations.CheckPermission;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.service.InfoService;

import java.util.Date;
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
    public HttpEntity<?> getInfoByBranch(@PathVariable UUID branchId,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) Date startDate,
                                         @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = infoService.getInfoByBranch(branchId,date,startDate,endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }
}
