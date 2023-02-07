package uz.pdp.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.springsecurity.annotations.CheckPermission;
import uz.pdp.springsecurity.configuration.ExcelGenerator;
import uz.pdp.springsecurity.payload.ProductViewDtos;
import uz.pdp.springsecurity.repository.ProductRepository;
import uz.pdp.springsecurity.service.ExcelHelper;
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
        String headerValue = "attachment; filename = PRODUCT "+ ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<ProductViewDtos> productViewDtos = excelService.getByBusiness(uuid);
        ExcelGenerator generator = new ExcelGenerator(productViewDtos);
        generator.generateExcelFile(response);

        return ResponseEntity.ok(response);
    }

    @CheckPermission("POST_EXCEL")
    @PostMapping("/upload/{id}")
    public HttpEntity<?> uploadFile(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        String message = "";
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                excelService.save(file,id);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((message));
    }
}
