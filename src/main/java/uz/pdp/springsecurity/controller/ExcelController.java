package uz.pdp.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.springsecurity.annotations.CheckPermission;
import uz.pdp.springsecurity.configuration.ExcelGenerator;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ProductViewDto;
import uz.pdp.springsecurity.payload.ProductViewDtos;
import uz.pdp.springsecurity.repository.ProductRepository;
import uz.pdp.springsecurity.service.ExcelService;
import uz.pdp.springsecurity.service.ProductService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ExcelService excelService;

    @CheckPermission("GET_EXCEL")
    @GetMapping("/export-to-excel/{uuid}")
    public HttpEntity<?> exportIntoExcelFile(HttpServletResponse response, @PathVariable UUID uuid) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PRODUCT" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<ProductViewDtos> productViewDtos = excelService.getByBusiness(uuid);
        ExcelGenerator generator = new ExcelGenerator(productViewDtos);
        generator.generateExcelFile(response);

        return ResponseEntity.ok(response);
    }
}
