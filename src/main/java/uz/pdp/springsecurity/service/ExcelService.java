package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.enums.Type;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ProductViewDtos;
import uz.pdp.springsecurity.repository.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;



    public List<ProductViewDtos> getByBusiness(UUID businessId) {
        List<ProductViewDtos> productViewDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);
        if (productList.isEmpty()) {
            return null;
        } else {
            for (Product product : productList) {
                ProductViewDtos productViewDto = new ProductViewDtos();
                productViewDto.setProductName(product.getName());
                productViewDto.setBrandName(product.getBrand().getName());
                productViewDto.setBarcode(productViewDto.getBarcode());
                productViewDto.setBuyPrice(product.getBuyPrice());
                productViewDto.setSalePrice(product.getSalePrice());
                productViewDto.setMinQuantity(product.getMinQuantity());
                List<Branch> branchList = product.getBranch();
                for (Branch branch : branchList) {
                    productViewDto.setBranch(branch.getName());
                }
                productViewDto.setExpiredDate(product.getExpireDate());

                Optional<Measurement> optionalMeasurement = measurementRepository.findById(product.getMeasurement().getId());
                optionalMeasurement.ifPresent(measurement -> productViewDto.setMeasurementId(measurement.getName()));
                Optional<Warehouse> optionalWarehouse = warehouseRepository.findByBranch_BusinessIdAndProductId(businessId, product.getId());
                optionalWarehouse.ifPresent(warehouse -> productViewDto.setAmount(warehouse.getAmount()));
                productViewDtoList.add(productViewDto);
            }

            return productViewDtoList;
        }
    }

    @SneakyThrows
    public ApiResponse save(MultipartFile file, UUID branchId, UUID measurementId, UUID categoryId,UUID brandId) {

        Business business = null;

        Optional<Category> optionalCategory = categoryRepository.findById(branchId);
        Optional<Branch> optionalBranch = branchRepository.findById(categoryId);
        Optional<Measurement> optionalMeasurement = measurementRepository.findById(measurementId);
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);


        if (optionalBranch.isEmpty()){
            return new ApiResponse("NOT FOUND BRANCH");
        }
        if (optionalCategory.isEmpty()){
            return new ApiResponse("NOT FOUND CATEGORY");
        }
        if (optionalMeasurement.isEmpty()){
            return new ApiResponse("NOT FOUND MEASUREMENT");
        }
        if (optionalBrand.isEmpty()){
            return new ApiResponse("NOT FOUND BRAND");
        }

        try {
            Branch branch = optionalBranch.get();
            business = branch.getBusiness();
            UUID productId = UUID.randomUUID();

            List<ProductViewDtos> productViewDtosList = ExcelHelper.excelToTutorials(file.getInputStream());
            List<Product> productList=new ArrayList<>();
            for (ProductViewDtos productViewDtos : productViewDtosList) {
                Product product=new Product();
                product.setBusiness(business);
                product.setName(productViewDtos.getProductName());
                product.setExpireDate(productViewDtos.getExpiredDate());
                product.setBarcode(String.valueOf(productViewDtos.getBarcode()));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                product.setDueDate(formatter.parse(formatter.format(productViewDtos.getExpiredDate())));
                product.setBuyPrice(productViewDtos.getBuyPrice());
                product.setSalePrice(productViewDtos.getSalePrice());
                product.setMinQuantity(productViewDtos.getMinQuantity());
                product.setTax(10);

                product.setCategory(optionalCategory.get());
                product.setMeasurement(optionalMeasurement.get());
                product.setBrand(optionalBrand.get());
                product.setType(Type.SINGLE);
                product.setPhoto(null);
                productList.add(product);
            }
            if (productViewDtosList.size()>0){
                productRepository.saveAll(productList);
                return new ApiResponse("Successfully Added ",true);
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());

        }
        return new ApiResponse();
    }
}