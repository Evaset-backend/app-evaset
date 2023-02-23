package uz.pdp.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.service.ReportsService;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    ReportsService reportsService;

    @GetMapping("/all-by-business/{branchId}")
    public HttpEntity<?> getAllBusinessAmount(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.allProductAmount(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/amounts-by-brand/{branchId}")
    public HttpEntity<?> getAllBrandAmount(@PathVariable UUID branchId,
                                           @RequestParam UUID brandId) {
        ApiResponse apiResponse = reportsService.allProductByBrand(branchId, brandId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/amounts-by-category/{branchId}/{categoryId}")
    public HttpEntity<?> getAllCategoryAmount(@PathVariable UUID branchId,
                                              @RequestParam UUID categoryId) {
        ApiResponse apiResponse = reportsService.allProductByCategory(branchId, categoryId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/amounts-branch/{branchId}")
    public HttpEntity<?> getAllDateByBrand(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.allProductAmountByBranch(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/most-sale/{branchId}")
    public HttpEntity<?> mostSaleProducts(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.mostSaleProducts(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/most-un-sale/{branchId}")
    public HttpEntity<?> mostUnSaleProducts(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.mostUnSaleProducts(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/purchase/{branchId}")
    public HttpEntity<?> purchaseReports(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.purchaseReports(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/purchase/by-date/{branchId}")
    public HttpEntity<?> purchaseReportsByDates(@PathVariable UUID branchId,
                                                @RequestParam(required = false) Date startDate,
                                                @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.purchaseReportsByDates(branchId, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

     @GetMapping("/production/by-date/{branchId}")
    public HttpEntity<?> productionReports(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.productionReports(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/purchase/by-supplier/{branchId}/{supplierId}")
    public HttpEntity<?> purchaseReportsBySupplier(@PathVariable UUID branchId,
                                                   @PathVariable UUID supplierId) {
        ApiResponse apiResponse = reportsService.purchaseReportsBySupplier(branchId, supplierId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/name/{branchId}/{name}")
    public HttpEntity<?> findByName(@PathVariable UUID branchId,
                                    @PathVariable String name) {
        ApiResponse apiResponse = reportsService.findByName(branchId, name);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/delivery/{branchId}")
    public HttpEntity<?> deliveryPriceGet(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.deliveryPriceGet(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/outlay/{branchId}")
    public HttpEntity<?> outlayReports(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.outlayReports(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/outlay-by-category/{branchId}/{categoryId}")
    public HttpEntity<?> outlayReportsByCategoryId(@PathVariable UUID branchId,
                                                   @PathVariable UUID categoryId) {
        ApiResponse apiResponse = reportsService.outlayReportsByCategory(branchId, categoryId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/outlay-by-date/{branchId}")
    public HttpEntity<?> outlayReportsByDate(@PathVariable UUID branchId,
                                             @RequestParam(required = false) Date startDate,
                                             @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.outlayReportsByDate(branchId, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/customer/{branchId}")
    public HttpEntity<?> customerReports(@PathVariable UUID branchId,
                                         @RequestParam(required = false) UUID customerId,
                                         @RequestParam(required = false) Date startDate,
                                         @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.customerReports(branchId, customerId, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/benefit-by-branch/{branchId}")
    public HttpEntity<?> benefitByBranchReports(@PathVariable UUID branchId,
                                                @RequestParam(required = false) String date,
                                                @RequestParam(required = false) Date startDate,
                                                @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.dateBenefitAndLostByProductReports(branchId, date, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/benefit-by-category/{branchId}")
    public HttpEntity<?> benefitByCategoryReports(@PathVariable UUID branchId,
                                                  @RequestParam(required = false) String date,
                                                  @RequestParam(required = false) Date startDate,
                                                  @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.benefitAndLostByCategoryReports(branchId, date, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/benefit-by-brand/{branchId}")
    public HttpEntity<?> benefitByBrandReports(@PathVariable UUID branchId,
                                               @RequestParam(required = false) String date,
                                               @RequestParam(required = false) Date startDate,
                                               @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.benefitAndLostByBrandReports(branchId, date, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/benefit-by-customer/{branchId}")
    public HttpEntity<?> benefitByCustomerReports(@PathVariable UUID branchId,
                                                  @RequestParam(required = false) String date,
                                                  @RequestParam(required = false) Date startDate,
                                                  @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.benefitAndLostByCustomerReports(branchId, date, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/benefit-by-one-date/{branchId}")
    public HttpEntity<?> benefitByDateReports(@PathVariable UUID branchId) {
        ApiResponse apiResponse = reportsService.benefitAndLostByOneDateReports(branchId);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/products-report/{branchId}")
    public HttpEntity<?> productsReport(@PathVariable UUID branchId,
                                        @RequestParam(required = false) UUID customerId,
                                        @RequestParam(required = false) String date,
                                        @RequestParam(required = false) Date startDate,
                                        @RequestParam(required = false) Date endDate) {
        ApiResponse apiResponse = reportsService.productsReport(customerId, branchId, date, startDate, endDate);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


}
